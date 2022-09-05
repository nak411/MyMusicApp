package com.naveed.mymusicapp.core.presentation.delegates

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

interface StoragePermissionHandler {

    fun registerLifecycleOwner(owner: Fragment, onGranted: () -> Unit, onDenied: () -> Unit)
}

class StoragePermissionHandlerImpl : StoragePermissionHandler, LifecycleEventObserver {

    private lateinit var fragment: Fragment
    private lateinit var onGranted: () -> Unit
    private lateinit var onDenied: () -> Unit

    private val requestPermissionLauncher = fragment.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onGranted()
        } else {
            onDenied()
        }
    }

    override fun registerLifecycleOwner(
        owner: Fragment,
        onGranted: () -> Unit,
        onDenied: () -> Unit
    ) {
        fragment = owner
        this.onGranted = onGranted
        this.onDenied = onDenied
        owner.lifecycle.addObserver(this)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_CREATE -> checkStoragePermission()
            else -> Unit
        }
    }

    private fun checkStoragePermission() {
        val permissionStatus = ContextCompat.checkSelfPermission(
            fragment.requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            onGranted()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }
}