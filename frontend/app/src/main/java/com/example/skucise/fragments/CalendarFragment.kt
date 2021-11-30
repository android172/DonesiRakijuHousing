package com.example.skucise.fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.skucise.Meeting
import com.example.skucise.R
import com.example.skucise.adapter.MeetingAdapter
import kotlinx.android.synthetic.main.fragment_calendar.*
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

        rcv_calendar_page.apply {

            meetingRequests.addAll(arrayListOf(
                Meeting(1, "Some title", "User1", LocalDateTime.now(), LocalDateTime.now()),
                Meeting(2, "Some other title", "User2", LocalDateTime.now(), LocalDateTime.now()),
                Meeting(3, "None title", "User3", LocalDateTime.now(), LocalDateTime.now())
            ))

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