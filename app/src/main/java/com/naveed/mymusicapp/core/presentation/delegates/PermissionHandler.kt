package com.naveed.mymusicapp.core.presentation.delegates

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

/**
 * Used for simplifying and extracting out the storage permission flow for [Fragment].
 */
interface StoragePermissionHandler {

    /**
     * Call this method from [Fragment.onCreate] to register for permission flow
     *
     * @param owner The fragment making the call
     * @param onGranted the function invoke when permission is granted
     * @param onDenied the function to invoke when permission is denied
     */
    fun registerStoragePermissionLifecycleOwner(
        owner: Fragment,
        onGranted: () -> Unit,
        onDenied: () -> Unit
    )
}

class StoragePermissionHandlerImpl : StoragePermissionHandler, LifecycleEventObserver {

    // Setup lifecycle bound lateinit variable
    private lateinit var fragment: Fragment
    private lateinit var onGranted: () -> Unit
    private lateinit var onDenied: () -> Unit
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun registerStoragePermissionLifecycleOwner(
        owner: Fragment,
        onGranted: () -> Unit,
        onDenied: () -> Unit
    ) {
        fragment = owner
        this.onGranted = onGranted
        this.onDenied = onDenied
        owner.lifecycle.addObserver(this)
        requestPermissionLauncher = fragment.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) onGranted() else onDenied()
        }
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