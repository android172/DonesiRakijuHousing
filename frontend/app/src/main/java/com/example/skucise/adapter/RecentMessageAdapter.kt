package com.example.skucise.adapter

import android.net.Uri
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skucise.DateTimeHelper
import com.example.skucise.FileData
import com.example.skucise.R
import com.example.skucise.RecentMessage
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.item_recent_message.*
import kotlinx.android.synthetic.main.item_recent_message.view.*
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class RecentMessageAdapter (
    private val recentMessages: ArrayList<RecentMessage> = ArrayList()
) : RecyclerView.Adapter<RecentMessageAdapter.RecentMessageViewHolder>() {

    inner class RecentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecentMessageAdapter.RecentMessageViewHolder {
        this.parentFragment = parent
        return RecentMessageViewHolder(LayoutInflater.from(parent.context).inflate(
                R.layout.item_recent_message,
                parent,
                false
            )
        )
    }

    private var navigationView: BottomNavigationView? = null

    fun setupNavMenu(bottomNavigationView: BottomNavigationView) {
        this.navigationView = bottomNavigationView
    }

    fun updateData(){
        notifyDataSetChanged()
    }

    private lateinit var parentFragment : ViewGroup

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecentMessageViewHolder, position: Int) {
        val rm = recentMessages[position]

        holder.itemView.apply {
            tv_from.text = rm.User.DisplayName
            tv_message.text = rm.Message.Content
            tv_date.text = DateTimeHelper.getReadableDate(rm.Message.SendDate)
            if(rm.User.Id != rm.Message.ReceiverId) {
                if(rm.Message.Seen) {
                    img_seen.visibility = View.GONE
                }else{
                    img_seen.setBackgroundResource(R.drawable.ic_alert)
                }
            }else{
                if(rm.Message.Seen) {
                    img_seen.setBackgroundResource(R.drawable.ic_message_seen)
                }else{
                    img_seen.setBackgroundResource(R.drawable.ic_message_sent)
                }
            }

            img_user.clipToOutline = true
            Glide.with(context)
                .load("http:10.0.2.2:5000/api/image/get_user_image_file?userId=${rm.User.Id}")
                    .centerCrop()
                .placeholder(R.drawable.ic_offline)
                .into(img_user)

            img_status.clipToOutline = true
            var statusIcon = R.drawable.ic_offline
            var statusColor = R.color.msgs_unavailable
            if(rm.User.Online) {
                statusIcon = R.drawable.ic_online
                statusColor = R.color.msgs_available
            }
            img_status.foreground =  context.getResources().getDrawable(statusIcon)
            img_status.foregroundTintList = context.getResources().getColorStateList(statusColor)

            csl_recent_message.setOnClickListener {
                if (navigationView == null) return@setOnClickListener

                navigationView!!.menu.setGroupCheckable(0, true, false)
                for (i in 0 until navigationView!!.menu.size()) {
                    navigationView!!.menu.getItem(i).isChecked = false
                }
                navigationView!!.menu.setGroupCheckable(0, true, true)

                val args = Bundle()
                args.putInt("otherUserId", rm.User.Id.toInt())
                parentFragment.findNavController().navigate(R.id.chatWithUserFragment, args)
            }
        }
    }

    override fun getItemCount(): Int {
        return recentMessages.size
    }
}