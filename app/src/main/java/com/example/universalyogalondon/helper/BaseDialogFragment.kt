package com.example.universalyogalondon.helper

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding

abstract class BaseDialogFragment <VB : ViewBinding> : DialogFragment(){
    private var _binding: VB? = null
    protected open val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = bindView(inflater)
        val view = binding.root
        return view
    }

    abstract fun bindView(inflater: LayoutInflater): VB

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}