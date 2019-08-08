package com.whenwhat.attachmentfilechooser.dialog

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.whenwhat.attachmentfilechooser.R
import com.whenwhat.attachmentfilechooser.getImageFromGallery
import kotlinx.android.synthetic.main.dialog_bottom_sheet_chooser.*

private typealias Delegate = DialogAttachmentChooserDelegate

class DialogAttachmentChooser private constructor() : BottomSheetDialogFragment() {

    companion object {

        fun <T> show(fragment: T) where T : Fragment, T : Delegate {
            DialogAttachmentChooser()
                .show(fragment.childFragmentManager, "DialogAttachmentChooser")
        }
    }

    private val images by lazy(::makeImageItems)
    private val adapter by lazy(::createImageAdapter)
    private var currentAttachmentNumber: Int = 1
    private val delegate: Delegate
        get() = parentFragment as Delegate

    private val selectedImageUris: List<Uri>
        get() = images.filter {
            it.selectedNumber != null
        }.map { it.image }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentAttachmentNumber = delegate.startNumberAttachment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_bottom_sheet_chooser, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter.onImageClickListener = ::onItemClick
        adapter.onCameraClickListener = ::onCameraClick

        imagesList.layoutManager = LinearLayoutManager(context).apply {
            orientation = LinearLayoutManager.HORIZONTAL
        }
        imagesList.addItemDecoration(ItemLinearMarginDecorator(30))
        imagesList.adapter = adapter

        send.setOnDismissibleClick {
            delegate.onSelectedImages(selectedImageUris)
        }
        photo.setOnDismissibleClick(delegate::onNeedOpenGallery)
        file.setOnDismissibleClick(delegate::onNeedOpenFilePicker)
    }

    private fun onCameraClick() {
        delegate.onNeedOpenCamera()
        dismiss()
    }

    private fun onItemClick(item: ImageItem.Image) {
        if (item.selectedNumber == null) {
            item.selectedNumber = currentAttachmentNumber
            adapter.notifyItemChanged(item)
            currentAttachmentNumber++
        } else {
            currentAttachmentNumber--
            item.selectedNumber = null
            updateSelectedNumber(images)
            adapter.notifyDataSetChanged()
        }
    }

    private fun updateSelectedNumber(items: List<ImageItem.Image>) {
        var number = delegate.startNumberAttachment
        items.filter {
            it.selectedNumber != null
        }.sortedBy {
            it.selectedNumber
        }.forEach {
            it.selectedNumber = number++
        }
    }

    private fun makeImageItems(): List<ImageItem.Image> {
        return activity!!.getImageFromGallery().map {
            ImageItem.Image(it, null)
        }
    }

    private fun View.setOnDismissibleClick(action: () -> Unit) {
        setOnClickListener {
            action()
            dismiss()
        }
    }

    private fun createImageAdapter(): ImagesAdapter {
        return ImagesAdapter(listOf(ImageItem.Camera) + images)
    }

    private val Delegate.startNumberAttachment: Int
        get() = existImageAttachmentCount.inc()
}