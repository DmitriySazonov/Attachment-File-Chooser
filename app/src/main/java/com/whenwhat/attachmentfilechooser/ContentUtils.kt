package com.whenwhat.attachmentfilechooser

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import java.io.File
import java.util.*


fun Activity.getImageFromGallery(): List<Uri> {
    val images = LinkedList<String>()

    val cursor = contentResolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        arrayOf(MediaStore.MediaColumns.DATA), null, null, null
    )
    val columnIndex = cursor!!.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)

    while (cursor.moveToNext())
        images.add(cursor.getString(columnIndex))
    cursor.close()

    return images.map {
        Uri.fromFile(File(it))
    }
}

fun Fragment.openContentPicker(mimiType: String, requestId: Int) {
    val intent = Intent().setType(mimiType)
        .setAction(Intent.ACTION_GET_CONTENT)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)

    startActivityForResult(intent, requestId)
}

fun Intent.getFileUris(): List<Uri> {
    val clipData = clipData
    return if (clipData == null) {
        listOfNotNull(data)
    } else {
        (0 until clipData.itemCount).mapNotNull {
            clipData.getItemAt(it)?.uri
        }
    }
}

fun Fragment.takePhoto(saveInto: File, requestId: Int) {
    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    if (intent.resolveActivity(context!!.packageManager) == null)
        return

    val photoURI: Uri = FileProvider.getUriForFile(context!!,
        "com.whenwhat.attachmentfilechooser.fileprovider", saveInto)
    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
    startActivityForResult(intent, requestId)
}