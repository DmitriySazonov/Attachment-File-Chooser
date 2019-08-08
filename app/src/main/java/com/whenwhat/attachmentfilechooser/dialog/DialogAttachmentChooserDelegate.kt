package com.whenwhat.attachmentfilechooser.dialog

import android.net.Uri

interface DialogAttachmentChooserDelegate {
    val existImageAttachmentCount: Int
    fun onSelectedImages(images: List<Uri>)
    fun onNeedOpenGallery()
    fun onNeedOpenFilePicker()
    fun onNeedOpenCamera()
}