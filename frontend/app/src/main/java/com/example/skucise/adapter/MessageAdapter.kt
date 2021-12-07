package com.example.skucise.adapter

import android.net.Uri
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skucise.*
import com.example.skucise.DateTimeHelper.Companion.sameAgeClass
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.item_message.view.*
import kotlinx.android.synthetic.main.item_message.view.img_seen
import kotlinx.android.synthetic.main.item_message.view.img_user
import kotlinx.android.synthetic.main.item_message.view.tv_date
import kotlinx.android.synthetic.main.item_message.view.tv_message
import java.time.Duration

class MessageAdapter (
    private val otherUserId: UInt,
    private val messages: ArrayList<Message> = ArrayList()
) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MessageAdapter.MessageViewHolder {
        this.parentFragment = parent
        return MessageViewHolder(LayoutInflater.from(parent.context).inflate(
                R.layout.item_message,
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

    var height = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val msg = messages[position]

        val layouts = arrayListOf(
            R.drawable.rounded_message,
            R.drawable.rounded_message_top,
            R.drawable.rounded_message_bottom,
            R.drawable.rounded_message_middle,
            R.drawable.rounded_message,
            R.drawable.rounded_message_top_rtl,
            R.drawable.rounded_message_bottom_rtl,
            R.drawable.rounded_message_middle_rtl
        )
        var customLayout = 0
        var userOffset = 0
        val minTimeDiff = Duration.ofMinutes(5)

        var hideTime = false

        if(position > 0) {
            val prevMessage = messages[position - 1]
            val sameAge = sameAgeClass(msg.SendDate, prevMessage.SendDate)
            val sameSender = prevMessage.SenderId == msg.SenderId
            if(sameAge){
                hideTime = true
                if(sameSender)
                    customLayout += 2
            }

        }

        if(position < messages.size - 1){
            val nextMessage = messages[position + 1]
            val sameAge = sameAgeClass(msg.SendDate, nextMessage.SendDate)
            val sameSender = nextMessage.SenderId == msg.SenderId
            if(sameAge && sameSender)
                customLayout += 1
        }

        if(msg.SenderId != otherUserId)
            userOffset = 4

        holder.itemView.apply {
            if(customLayout>1 || hideTime) {
                tv_date.visibility = View.GONE
            }
            else {
                tv_date.visibility = View.VISIBLE
            }
            if(height==0)
                this@MessageAdapter.height = csl_user_image_holder.layoutParams.height
            if(customLayout == 0 || customLayout == 2){
                img_user.clipToOutline = true
                Glide.with(context)
                    .load("http:10.0.2.2:5000/api/image/get_user_image_file?userId=${otherUserId}")
                    .centerCrop()
                    .placeholder(R.drawable.ic_offline)
                    .into(img_user)
                csl_user_image_holder.visibility = View.VISIBLE
                csl_user_image_holder.layoutParams.height = this@MessageAdapter.height
            }else{
                csl_user_image_holder.visibility = View.INVISIBLE
                csl_user_image_holder.layoutParams.height = 1
            }
            csl_message_content.background = resources.getDrawable(layouts[customLayout+userOffset])

            tv_message.text = msg.Content
            tv_date.text = DateTimeHelper.getReadableDate(msg.SendDate)
            if(otherUserId == msg.SenderId) {
                img_seen.visibility = View.GONE
                ll_message.layoutDirection = View.LAYOUT_DIRECTION_LTR
                csl_message_content.backgroundTintList = context.getResources().getColorStateList(R.color.msgs_background)
                img_seen.foregroundTintList = context.getResources().getColorStateList(R.color.msgs_grey)
                tv_message.setTextColor(resources.getColor(R.color.black))
            }else{
                if(msg.Seen) {
                    img_seen.visibility = View.VISIBLE
                    img_seen.setBackgroundResource(R.drawable.ic_message_seen)
                }else{
                    img_seen.visibility = View.GONE
                }
                csl_user_image_holder.visibility = View.GONE
                ll_message.layoutDirection = View.LAYOUT_DIRECTION_RTL
                csl_message_content.backgroundTintList = context.getResources().getColorStateList(R.color.logo_color)
                tv_message.setTextColor(resources.getColor(R.color.white))
            }


            /*img_status.clipToOutline = true
            var statusIcon = R.drawable.ic_offline
            var statusColor = R.color.msgs_unavailable
            if(rm.User.Online) {
                statusIcon = R.drawable.ic_online
                statusColor = R.color.msgs_available
            }
            img_status.foreground =  context.getResources().getDrawable(statusIcon)
            img_status.foregroundTintList = context.getResources().getColorStateList(statusColor)
            */
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }
}