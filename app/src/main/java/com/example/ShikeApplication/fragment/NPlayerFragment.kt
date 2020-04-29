package com.example.ShikeApplication.fragment

import android.os.Bundle
import android.app.Fragment
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import com.example.ShikeApplication.R
import com.example.ShikeApplication.activity.VideoPlayActivity
import com.example.ShikeApplication.ndkdemo.*
import kotlinx.android.synthetic.main.activity_opengles_smaple.*

class NPlayerFragment : Fragment() ,View.OnClickListener{
    private var TAG = "NPlayer"
    private var mPlayVideo :Button ? = null
    private var mPlayRtmp :Button ? = null
    private var mPlayVideoEditText :EditText ? = null
    private var mPlayRtmpEditText :EditText ? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        Log.i(TAG, "player selected page show.")
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
                /*var bundle = Bundle()
                bundle.putString("videoUrl" ,mPlayVideoEditText?.text.toString())
                initent.putExtras(bundle)*/
                this.activity.startActivity(initent)
            }
            R.id.playrtmp -> {
                Log.i(TAG, "player set rtmp")
                val initent = Intent(this.activity ,VideoPlayActivity::class.java)
                Log.i(TAG, "rtmp url is " + mPlayRtmpEditText?.text.toString())
                ndktool.NPlayerOpenUrl(mPlayRtmpEditText?.text.toString())
                startActivity(initent)
            }
        }
    }
}
