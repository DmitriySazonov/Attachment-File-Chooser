package com.whenwhat.attachmentfilechooser.fragment

interface AttachmentFragmentDelegate {
    fun onAttachmentChange(attachments: List<Attachment>)
}