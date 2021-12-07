package com.example.skucise

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
import com.example.skucise.adapter.MessageAdapter
import com.example.skucise.adapter.RecentMessageAdapter
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.fragment_chat_with_user.*
import org.json.JSONArray
import org.json.JSONObject

private const val ARG_PARAM1 = "otherUserId"

/**
 * A simple [Fragment] subclass.
 * Use the [ChatWithUserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChatWithUserFragment : Fragment() {
    private val messages: ArrayList<Message> = ArrayList()

    private var otherUserId: UInt? = null
    lateinit var mAdapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            otherUserId = it.getInt(ARG_PARAM1).toUInt()
        }
        mAdapter = MessageAdapter(otherUserId = otherUserId!!, messages = messages)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_with_user, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rcv_messages.apply {
            mAdapter.setupNavMenu(requireActivity().findViewById(R.id.nav_bottom_navigator))
            adapter = mAdapter
            layoutManager = LinearLayoutManager(activity)
        }

        val url = "http://10.0.2.2:5000/api/message/get_chat?otherUserId=$otherUserId"

        ReqSender.sendRequestArray(
            this.requireActivity(),
            Request.Method.POST,
            url,
            null,
            { response ->
                val js = (response as JSONArray)
                messages.clear()
                for(i in 0 until js.length()){
                    val jsonObj = js[i] as JSONObject
                    messages.add(MessageJSON.toMessage(jsonObj))
                }
                mAdapter.updateData()
                rcv_messages.scrollToPosition(mAdapter.getItemCount()-1);
            },
            { error ->
                Toast.makeText(activity, "error:\n$error", Toast.LENGTH_LONG).show()
            }
        )
    }

    companion object {
        @JvmStatic
        fun newInstance(userId: Int) =
            ChatWithUserFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, userId)
                }
            }
    }
}