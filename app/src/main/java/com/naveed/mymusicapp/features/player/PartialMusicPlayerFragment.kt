package com.naveed.mymusicapp.features.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.naveed.mymusicapp.databinding.FragmentPartialMusicPlayerBinding

class PartialMusicPlayerFragment : Fragment() {

    private var _binding: FragmentPartialMusicPlayerBinding? = null
    private val binding: FragmentPartialMusicPlayerBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPartialMusicPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}