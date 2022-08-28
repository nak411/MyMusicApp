package com.naveed.mymusicapp.features.songlist.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.PackageManagerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.naveed.mymusicapp.databinding.FragmentSongListBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * Contains the UI for displaying the list of songs
 */
@AndroidEntryPoint
class SongListFragment : Fragment() {

    private var _binding: FragmentSongListBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: SongListViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            sendEvent(SongListEvent.LoadSongs)
        } else {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val permissionStatus = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            sendEvent(SongListEvent.LoadSongs)
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSongListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * All events to the view model are sent through this function.  This is the
     * exit point for this fragment
     */
    private fun sendEvent(event: SongListEvent) {
        viewModel.onEvent(event)
    }

}