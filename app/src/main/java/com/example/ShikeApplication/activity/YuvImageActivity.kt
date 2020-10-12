package com.example.ShikeApplication.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.ShikeApplication.R
import com.example.ShikeApplication.YuvImage.GLSurface
import com.example.ShikeApplication.YuvImage.MyGLRender
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException

class YuvImageActivity : BaseActivity() {

    var width = 1280
    var height = 720
    private var mglsuface: GLSurface? = null
    private var mrender: MyGLRender? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_yuv_image)
        mglsuface = findViewById(R.id.preview)
        mrender = MyGLRender(mglsuface)
        mrender!!.update(width, height)
        var file: File = File("/sdcard/test.yuv")

        if (!file.exists()) {
            // A test.yuv file that format is YUV NY14 4:2:0 size is height 720 width 1280
            Toast.makeText(this, "/sdcard/test.yuv 1280X720 ，文件不存在", Toast.LENGTH_LONG).show()
        } else {
            object : Thread() {
                override fun run() {
                    super.run()
                    val yuvFile = File("/sdcard/test.yuv")
                    var fis: FileInputStream? = null
                    try {
                        fis = FileInputStream(yuvFile)
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    }
                    val size = width * height * 3 / 2

                    val input = ByteArray(size)
                    var hasRead = 0
                    while (true) {
                        try {
                            hasRead = fis!!.read(input)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                        if (hasRead == -1) {
                            break
                        }
                        mrender!!.update(input)
                        Log.i("thread", "thread is executing hasRead: $hasRead")
                        try {
                            sleep(100)
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }
                    }
                }
            }.start()
        }
    }

}