package com.example.skucise.adapter

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
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
import java.time.LocalDate
import java.time.LocalDateTime
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
                            meetingRequests.drop(position)
                            notifyItemRemoved(position)
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
                            meetingRequests.drop(position)
                            notifyItemRemoved(position)
                        },
                        { error ->
                            Toast.makeText(parentViewGroup!!.context, "error:\n$error", Toast.LENGTH_LONG).show()
                        }
                    )
                }

            }
            else {
                if ((currentMeetingRequest.owner && currentMeetingRequest.agreedOwner) ||
                    (!currentMeetingRequest.owner && currentMeetingRequest.agreedVisitor)) {
                    btn_meeting_accept.isEnabled = false
                }

                btn_meeting_accept.setOnClickListener {
                    ReqSender.sendRequestString(
                        parentViewGroup!!.context,
                        Request.Method.POST,
                        "http://10.0.2.2:5000/api/meeting/confirm_meeting",
                        hashMapOf(Pair("meetingId", currentMeetingRequest.id.toString())),
                        { btn_meeting_accept.isEnabled = false },
                        { error ->
                            Toast.makeText(parentViewGroup!!.context, "error:\n$error", Toast.LENGTH_LONG).show()
                        }
                    )
                }

                btn_meeting_tweak_time.setOnClickListener {}

                btn_meeting_cancel.setOnClickListener {
                    ReqSender.sendRequestString(
                        parentViewGroup!!.context,
                        Request.Method.POST,
                        "http://10.0.2.2:5000/api/meeting/cancel_meeting",
                        hashMapOf(Pair("meetingId", currentMeetingRequest.id.toString())),
                        {
                            meetingRequests.drop(position)
                            notifyItemRemoved(position)
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