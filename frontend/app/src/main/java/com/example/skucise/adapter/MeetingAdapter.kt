package com.example.skucise.adapter

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.example.skucise.Meeting
import com.example.skucise.R
import com.example.skucise.ReqSender
import kotlinx.android.synthetic.main.item_meeting_request.view.*
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class MeetingAdapter(
    private var meetingRequests: ArrayList<Meeting> = ArrayList()
    ): RecyclerView.Adapter<MeetingAdapter.MeetingViewHolder>() {

    class MeetingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private var parentViewGroup: ViewGroup? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeetingViewHolder {
        parentViewGroup = parent
        return MeetingViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_meeting_request,
                parent,
                false
            )
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateMeetings(meetingRequests: ArrayList<Meeting>) {
        this.meetingRequests = meetingRequests
        notifyDataSetChanged()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MeetingViewHolder, position: Int) {
        val currentMeetingRequest = meetingRequests[position]

        holder.itemView.apply {
            tv_meeting_title.text = currentMeetingRequest.title
            tv_meeting_username.text = currentMeetingRequest.username
            tv_meeting_time.text = currentMeetingRequest.proposedTime.format(
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT))
            tv_meeting_date_created.text = currentMeetingRequest.dateCreated.format(
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT))

            if (currentMeetingRequest.proposedTime < LocalDateTime.now()) {
                btn_meeting_accept.visibility = View.GONE
                btn_meeting_tweak_time.visibility = View.GONE
                btn_meeting_cancel.visibility = View.GONE

                btn_meeting_confirm.visibility = View.VISIBLE
                btn_meeting_delete.visibility = View.VISIBLE

                btn_meeting_confirm.setOnClickListener {
                    ReqSender.sendRequestString(
                        parentViewGroup!!.context,
                        Request.Method.POST,
                        "http://10.0.2.2:5000/api/meeting/conclude_meeting",
                        hashMapOf(Pair("meetingId", currentMeetingRequest.id.toString())),
                        {
                            val dropPosition = meetingRequests.indexOf(currentMeetingRequest)
                            meetingRequests.drop(dropPosition)
                            notifyItemRemoved(dropPosition)
                        },
                        { error ->
                            Toast.makeText(parentViewGroup!!.context, "error:\n$error", Toast.LENGTH_LONG).show()
                        }
                    )
                }

                btn_meeting_delete.setOnClickListener {
                    ReqSender.sendRequestString(
                        parentViewGroup!!.context,
                        Request.Method.POST,
                        "http://10.0.2.2:5000/api/meeting/delete_meeting",
                        hashMapOf(Pair("meetingId", currentMeetingRequest.id.toString())),
                        {
                            val dropPosition = meetingRequests.indexOf(currentMeetingRequest)
                            meetingRequests.drop(dropPosition)
                            notifyItemRemoved(dropPosition)
                        },
                        { error ->
                            Toast.makeText(parentViewGroup!!.context, "error:\n$error", Toast.LENGTH_LONG).show()
                        }
                    )
                }

            }
            else {
                // Already accepted the meeting
                if ((currentMeetingRequest.owner && currentMeetingRequest.agreedOwner) ||
                    (!currentMeetingRequest.owner && currentMeetingRequest.agreedVisitor)) {
                    btn_meeting_accept.isEnabled = false
                }

                // 24h before the meeting cancel and proposed time tweak are disabled
                if (LocalDateTime.now() > currentMeetingRequest.proposedTime.minusDays(1)) {
                    btn_meeting_cancel.isEnabled = false
                    btn_meeting_tweak_time.isEnabled = false
                }

                // on accept
                btn_meeting_accept.setOnClickListener {
                    ReqSender.sendRequestString(
                        parentViewGroup!!.context,
                        Request.Method.POST,
                        "http://10.0.2.2:5000/api/meeting/confirm_meeting",
                        hashMapOf(Pair("meetingId", currentMeetingRequest.id.toString())),
                        {
                            if (currentMeetingRequest.owner)
                                currentMeetingRequest.agreedOwner = true
                            else
                                currentMeetingRequest.agreedVisitor = true
                            notifyItemChanged(meetingRequests.indexOf(currentMeetingRequest))
                        },
                        { error ->
                            Toast.makeText(parentViewGroup!!.context, "error:\n$error", Toast.LENGTH_LONG).show()
                        }
                    )
                }

                // Date Changed
                btn_meeting_tweak_time.setOnClickListener {
                    // Callback for when new time is picked
                    fun changeTime(newTime: LocalDateTime) {
                        val params: HashMap<String, String> = HashMap()
                        params["meetingId"] = currentMeetingRequest.id.toString()
                        params["newTime"] = newTime.toString()

                        ReqSender.sendRequestString(
                            parentViewGroup!!.context,
                            Request.Method.POST,
                            "http://10.0.2.2:5000/api/meeting/edit_meeting_proposal",
                            params,
                            {
                                currentMeetingRequest.proposedTime = newTime
                                notifyItemChanged(meetingRequests.indexOf(currentMeetingRequest))
                            },
                            { error ->
                                Toast.makeText(parentViewGroup!!.context, "error:\n$error", Toast.LENGTH_LONG).show()
                            }
                        )
                    }

                    // Call date and time picker
                    val now = LocalDateTime.now()
                    val datePickerDialog = DatePickerDialog(parentViewGroup!!.context, {
                            _, year: Int, month: Int, day: Int ->
                        TimePickerDialog(parentViewGroup!!.context, {
                                _, hour: Int, minute: Int ->
                            changeTime(LocalDateTime.of(year, month + 1, day, hour, minute))
                        }, now.hour, now.minute, true).show()
                    }, now.year, now.monthValue - 1, now.dayOfMonth)
                    val nowZ = now.atZone(ZoneId.systemDefault())
                    datePickerDialog.datePicker.minDate = nowZ.toInstant().toEpochMilli()
                    datePickerDialog.datePicker.maxDate = nowZ.plusYears(1).toInstant().toEpochMilli()
                    datePickerDialog.show()
                }

                btn_meeting_cancel.setOnClickListener {
                    ReqSender.sendRequestString(
                        parentViewGroup!!.context,
                        Request.Method.POST,
                        "http://10.0.2.2:5000/api/meeting/cancel_meeting",
                        hashMapOf(Pair("meetingId", currentMeetingRequest.id.toString())),
                        {
                            val dropPosition = meetingRequests.indexOf(currentMeetingRequest)
                            meetingRequests.drop(dropPosition)
                            notifyItemRemoved(dropPosition)
                        },
                        { error ->
                            Toast.makeText(parentViewGroup!!.context, "error:\n$error", Toast.LENGTH_LONG).show()
                        }
                    )
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return meetingRequests.size
    }
}