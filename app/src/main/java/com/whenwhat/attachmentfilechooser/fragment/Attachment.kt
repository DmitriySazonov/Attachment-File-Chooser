package com.whenwhat.attachmentfilechooser.fragment

import android.net.Uri

sealed class Attachment(val uri: Uri) {
    class Image(uri: Uri) : Attachment(uri)
    class Document(uri: Uri) : Attachment(uri)
    class Voice(uri: Uri) : Attachment(uri)
}