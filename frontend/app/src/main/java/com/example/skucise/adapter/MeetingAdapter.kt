package com.example.skucise.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.skucise.Meeting
import com.example.skucise.R
import kotlinx.android.synthetic.main.item_meeting_request.view.*
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class MeetingAdapter(
    private val meetingRequests: ArrayList<Meeting> = ArrayList()
    ): RecyclerView.Adapter<MeetingAdapter.MeetingViewHolder>() {

    class MeetingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeetingViewHolder {
        return MeetingViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_meeting_request,
                parent,
                false
            )
        )
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
        }
    }

    override fun getItemCount(): Int {
        return meetingRequests.size
    }
}