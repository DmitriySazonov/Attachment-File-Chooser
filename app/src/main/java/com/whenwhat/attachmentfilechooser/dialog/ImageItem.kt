package com.whenwhat.attachmentfilechooser.dialog

import android.net.Uri

sealed class ImageItem {
    
    class Image(
        val image: Uri,
        var selectedNumber: Int?
    ) : ImageItem()

    object Camera : ImageItem()
}