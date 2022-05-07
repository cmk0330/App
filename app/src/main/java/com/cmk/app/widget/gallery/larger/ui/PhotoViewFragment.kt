package com.cmk.app.widget.gallery.larger.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cmk.app.databinding.FragmentPhotoViewBinding

class PhotoViewFragment:Fragment() {

    private var _binding:FragmentPhotoViewBinding ?=null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPhotoViewBinding.inflate(inflater, container, false)
        return binding.root
    }
}