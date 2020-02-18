package com.example.ShikeApplication.fragment

import android.os.Bundle
import android.app.Fragment
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast

import com.example.ShikeApplication.R
import com.example.ShikeApplication.activity.VideoPlayActivity

class NPlayerFragment : Fragment() ,View.OnClickListener{

    private var mPlayVideo :Button ? = null
    private var mPlayRtmp :Button ? = null
    private var mPlayVideoEditText :Button ? = null
    private var mPlayRtmpEditText :Button ? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        var view : View? = inflater.inflate(R.layout.fragment_native_player,container,false)
        mPlayVideo = view?.findViewById(R.id.playvideo)
        mPlayRtmp = view?.findViewById(R.id.playrtmp)
        mPlayVideoEditText = view?.findViewById(R.id.fileurl)
        mPlayRtmpEditText = view?.findViewById(R.id.rtmpurl)
        mPlayVideo?.setOnClickListener(this)
        mPlayRtmp?.setOnClickListener(this)
        return view
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.playvideo -> {
                Toast.makeText(this.activity,"PalyVideo" ,Toast.LENGTH_LONG).show()
                val initent = Intent(this.activity ,VideoPlayActivity::class.java)
                startActivity(initent)
            }
            R.id.playrtmp -> {
                Toast.makeText(this.activity,"PalyRtmp" ,Toast.LENGTH_LONG).show()
                val initent = Intent(this.activity ,VideoPlayActivity::class.java)
                startActivity(initent)
            }
        }
    }
}
