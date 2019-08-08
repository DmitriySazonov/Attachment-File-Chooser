package com.whenwhat.attachmentfilechooser.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.fragment.app.Fragment
import com.whenwhat.attachmentfilechooser.*
import com.whenwhat.attachmentfilechooser.dialog.DialogAttachmentChooser
import com.whenwhat.attachmentfilechooser.dialog.DialogAttachmentChooserDelegate
import com.whenwhat.attachmentfilechooser.other.ObservableList
import kotlinx.android.synthetic.main.fragment_attachment.*
import java.io.File

typealias Delegate = AttachmentFragmentDelegate

class AttachmentFragment : Fragment(R.layout.fragment_attachment), DialogAttachmentChooserDelegate {

    companion object {
        private const val GALLERY_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE
        private const val SAVE_FILE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE
        private const val CAMERA_PERMISSION = Manifest.permission.CAMERA

        private const val DIALOG_PERMISSION_REQUEST_ID = 1
        private const val CAMERA_PERMISSION_REQUEST_ID = 2

        private const val GALLERY_CONTENT_REQUEST_ID = 1
        private const val DOCUMENTS_CONTENT_REQUEST_ID = 2
        private const val CAMERA_CONTENT_REQUEST_ID = 2
    }

    override val existImageAttachmentCount: Int
        get() = attachments.count { it is Attachment.Image }

    private val delegate: Delegate
        get() = activity as Delegate
    private val attachments = ObservableList<Attachment>()

    private var tmpFilePhoto: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        attachments.subscribeOnChange { _, newList ->
            delegate.onAttachmentChange(newList)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        attachFile.setOnClickListener(::tryOpenDialogAttachment)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.any { it == PackageManager.PERMISSION_DENIED })
            return
        when (requestCode) {
            DIALOG_PERMISSION_REQUEST_ID -> openDialogAttachment()
            CAMERA_PERMISSION_REQUEST_ID -> openCamera()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAMERA_CONTENT_REQUEST_ID)
            onCameraResult(resultCode, data)

        if (resultCode != Activity.RESULT_OK)
            return
        data ?: return

        if (requestCode == GALLERY_CONTENT_REQUEST_ID)
            onGalleryResult(data)
        if (requestCode == DOCUMENTS_CONTENT_REQUEST_ID)
            onFilePickerResult(data)
    }

    override fun onSelectedImages(images: List<Uri>) {
        attachments.addAll(images.map(Attachment::Image))
    }

    override fun onNeedOpenGallery() {
        openContentPicker("image/*", GALLERY_CONTENT_REQUEST_ID)
    }

    override fun onNeedOpenFilePicker() {
        openContentPicker("*/*", DOCUMENTS_CONTENT_REQUEST_ID)
    }

    override fun onNeedOpenCamera() {
        tryOpenCamera()
    }

    private fun onGalleryResult(intent: Intent) {
        attachments.addAll(intent.getFileUris().map(Attachment::Image))
    }

    private fun onFilePickerResult(intent: Intent) {
        attachments.addAll(intent.getFileUris().map(Attachment::Document))
    }

    private fun onCameraResult(resultCode: Int, intent: Intent?) {
        val tmpFile = tmpFilePhoto ?: return
        if (resultCode == Activity.RESULT_OK && intent != null)
            attachments.add(Attachment.Image(Uri.fromFile(tmpFile)))
        else
            tmpFile.deleteOnExit()
        tmpFilePhoto = null
    }

    private fun tryOpenCamera() {
        if (context!!.permissionsIsGranted(CAMERA_PERMISSION, SAVE_FILE_PERMISSION)) {
            openCamera()
        } else {
            requestPermissions(
                arrayOf(CAMERA_PERMISSION, SAVE_FILE_PERMISSION),
                CAMERA_PERMISSION_REQUEST_ID
            )
        }
    }

    private fun tryOpenDialogAttachment() {
        if (context!!.permissionsIsGranted(GALLERY_PERMISSION))
            openDialogAttachment()
        else
            requestPermissions(arrayOf(GALLERY_PERMISSION), DIALOG_PERMISSION_REQUEST_ID)
    }

    private fun openCamera() {
        val file = File(getInternalPhotoStorage(), "${System.currentTimeMillis()}.jpg")
        tmpFilePhoto = file
        takePhoto(file, CAMERA_CONTENT_REQUEST_ID)
    }

    private fun openDialogAttachment() {
        DialogAttachmentChooser.show(this)
    }

    private fun getInternalPhotoStorage(): File {
        val file = File(
            Environment.getExternalStorageDirectory(),
            "WhenWhat"
        )
        if (!file.exists())
            file.mkdir()
        return file
    }
}