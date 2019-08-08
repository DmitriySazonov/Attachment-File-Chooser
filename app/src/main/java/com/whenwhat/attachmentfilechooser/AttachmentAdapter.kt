package com.whenwhat.attachmentfilechooser

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.whenwhat.attachmentfilechooser.fragment.Attachment

class AttachmentAdapter : RecyclerView.Adapter<AttachmentAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val attachments = ArrayList<Attachment>()

    fun setAttachments(attachments: List<Attachment>) {
        this.attachments.clear()
        this.attachments.addAll(attachments)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            Attachment.Image::class.hashCode() -> ImageView(parent.context)
            else -> TextView(parent.context)
        }.let(::ViewHolder)
    }

    override fun getItemViewType(position: Int): Int {
        return attachments[position]::class.hashCode()
    }

    override fun getItemCount(): Int {
        return attachments.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = attachments[position]
        val view = holder.itemView
        if (item is Attachment.Image) {
            Glide.with(view.context)
                .load(item.uri)
                .into(view as ImageView)
        } else {
            (view as TextView).text = item.uri.path
        }
    }
}