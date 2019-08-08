package com.whenwhat.attachmentfilechooser

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.whenwhat.attachmentfilechooser.fragment.Attachment
import com.whenwhat.attachmentfilechooser.fragment.AttachmentFragmentDelegate
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), AttachmentFragmentDelegate {

    private val adapter = AttachmentAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView.layoutManager = GridLayoutManager(this, 2, RecyclerView.VERTICAL, false)
        recyclerView.adapter = adapter
    }

    override fun onAttachmentChange(attachments: List<Attachment>) {
        adapter.setAttachments(attachments)
    }
}
