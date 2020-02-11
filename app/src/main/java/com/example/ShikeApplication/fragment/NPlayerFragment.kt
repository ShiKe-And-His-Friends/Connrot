package com.example.ShikeApplication.fragment

import android.os.Bundle
import android.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.ShikeApplication.R

class NPlayerFragment : Fragment() {

    private Button mFil

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_native_player, container, false)
    }



}
