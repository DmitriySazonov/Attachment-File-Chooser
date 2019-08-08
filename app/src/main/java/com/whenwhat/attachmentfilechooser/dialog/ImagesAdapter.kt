package com.whenwhat.attachmentfilechooser.dialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.whenwhat.attachmentfilechooser.R
import kotlinx.android.synthetic.main.item_dialog_image_attachment.view.*

class ImagesAdapter(private val images: List<ImageItem>) : RecyclerView.Adapter<ImagesAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    var onImageClickListener: ((ImageItem.Image) -> Unit)? = null
    var onCameraClickListener: (() -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dialog_image_attachment, parent, false)
            .let(ImagesAdapter::ViewHolder)
    }

    override fun getItemCount(): Int = images.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        return when (val item = images[position]) {
            is ImageItem.Image -> holder.itemView.bindAsImage(item)
            is ImageItem.Camera -> holder.itemView.bindAsCamera()
        }
    }

    fun notifyItemChanged(item: ImageItem) {
        notifyItemChanged(images.indexOf(item))
    }

    private fun View.bindAsCamera() {
        setBackgroundColor(ContextCompat.getColor(context, R.color.gray_selector))
        counter.setBackgroundColor(0)
        image.scaleType = ImageView.ScaleType.CENTER
        image.setImageResource(R.drawable.photo)

        setOnClickListener {
            onCameraClickListener?.invoke()
        }
    }

    private fun View.bindAsImage(item: ImageItem.Image) {
        setBackgroundColor(0)
        image.scaleType = ImageView.ScaleType.CENTER_CROP

        Glide.with(context)
            .load(item.image)
            .into(image)

        counter.apply {
            setBackgroundResource(
                if (item.selectedNumber == null)
                    R.drawable.background_image_counter_unselected
                else
                    R.drawable.background_image_counter_selected
            )
            text = item.selectedNumber?.toString()
        }

        setOnClickListener {
            onImageClickListener?.invoke(item)
        }
    }
}