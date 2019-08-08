package com.whenwhat.attachmentfilechooser

import android.content.Context
import android.content.pm.PackageManager
import android.view.View
import androidx.core.content.ContextCompat

fun View.setOnClickListener(action: () -> Unit) {
    setOnClickListener {
        action()
    }
}

fun Context.permissionsIsGranted(vararg permissions: String): Boolean {
    return permissions.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }
}