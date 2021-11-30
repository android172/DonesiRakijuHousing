package com.example.skucise.fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.example.skucise.Meeting
import com.example.skucise.R
import com.example.skucise.ReqSender
import com.example.skucise.adapter.MeetingAdapter
import kotlinx.android.synthetic.main.fragment_calendar.*
import org.json.JSONObject
import java.time.LocalDateTime

/**
 * A simple [Fragment] subclass.
 * Use the [CalendarFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CalendarFragment : Fragment() {

    private var meetingRequests: ArrayList<Meeting> = arrayListOf()

    private val meetingRequestAdapter: MeetingAdapter = MeetingAdapter(meetingRequests)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // request meetings
        ReqSender.sendRequestArray(
            requireContext(),
            Request.Method.POST,
            "http://10.0.2.2:5000/api/meeting/get_my_meetings",
            null,
            { response ->
                meetingRequests = ArrayList()
                for (i in 0 until response.length()) {
                    val meeting = response[i] as JSONObject
                    meetingRequests.add(Meeting(
                        id            = meeting.getJSONObject("meetingData").getInt("id"),
                        advertId      = meeting.getJSONObject("meetingData").getInt("advertId"),
                        agreedVisitor = meeting.getJSONObject("meetingData").getBoolean("agreedVisitor"),
                        agreedOwner   = meeting.getJSONObject("meetingData").getBoolean("agreedOwner"),
                        concluded     = meeting.getJSONObject("meetingData").getBoolean("concluded"),
                        otherUser     = meeting.getInt("otherUserId"),
                        username      = meeting.getString("otherUsername"),
                        title         = meeting.getString("advertTitle"),
                        owner         = meeting.getBoolean("amIOwner"),
                        proposedTime  = LocalDateTime.parse(
                                meeting.getJSONObject("meetingData").getString("time")),
                        dateCreated   = LocalDateTime.parse(
                                meeting.getJSONObject("meetingData").getString("dateCreated"))
                    ))
                }
                meetingRequestAdapter.updateMeetings(meetingRequests)
            },
            { error ->
                Toast.makeText(context, "error:\n$error", Toast.LENGTH_LONG).show()
            }
        )

        // initialize recycler view
        rcv_calendar_page.apply {
            adapter = meetingRequestAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment CalendarFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            CalendarFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}