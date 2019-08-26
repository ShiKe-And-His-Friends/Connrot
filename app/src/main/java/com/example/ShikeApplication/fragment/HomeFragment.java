package com.example.ShikeApplication.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.ShikeApplication.R;
import com.example.ShikeApplication.carmera.CameraSurfaceView;

public class HomeFragment extends Fragment {

    private Button buttonOk = null ;
    private Button buttonCancel = null ;
    private CameraSurfaceView surfaceView = null ;

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,null);
        buttonCancel = view.findViewById(R.id.button_cancel);
        buttonOk = view.findViewById(R.id.button_ok);
        surfaceView = view.findViewById(R.id.carmare_surface_view);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                surfaceView.doTakePhotoPath();
            }
        });
        return inflater.inflate(R.layout.fragment_home, container, false);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
