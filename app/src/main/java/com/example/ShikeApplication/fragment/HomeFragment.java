package com.example.ShikeApplication.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.example.ShikeApplication.R;
import com.example.ShikeApplication.carmera.CameraSurfaceView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class HomeFragment extends Fragment {

    @BindView(R.id.button_cancel)
    Button buttonCancel;
    @BindView(R.id.button_ok)
    Button buttonOk;
    @BindView(R.id.carmare_surface_view)
    CameraSurfaceView surfaceView;
    Unbinder unbinder;

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        unbinder =  ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @OnClick({R.id.button_ok , R.id.button_cancel})
    public void onViewClicked(@Nullable View view) {
        switch (view.getId()) {
            case R.id.button_ok:
                surfaceView.doTakePhotoPath();
                break;
        }
    }

}
