package com.example.ShikeApplication.carmera;

import java.util.ArrayList;

public class cameraPreview {

    //相机预览
    private boolean bShooting = false ;
    private int shooting_num = 0 ;
    private ArrayList<String> mShootingArray ;

    public ArrayList<String> getShootingList(){
        return mShootingArray;
    }

    public void doTakeShooting(){
        mShootingArray = new ArrayList<String>();
        bShooting = true ;
        shooting_num = 0 ;
    }

}
