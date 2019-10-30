package com.example.ShikeApplication.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ShikeApplication.R;

public class NativeRenderFragment extends Fragment {

    private static volatile NativeRenderFragment nativeRenderFragment;

    private NativeRenderFragment(){}

    public static NativeRenderFragment getInstance(){
        if(nativeRenderFragment == null){
            synchronized (NativeRenderFragment.class){
                if(nativeRenderFragment == null){
                    nativeRenderFragment = new NativeRenderFragment();
                }
            }
        }
        return nativeRenderFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nativerendler,container,false);
        return view;
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
