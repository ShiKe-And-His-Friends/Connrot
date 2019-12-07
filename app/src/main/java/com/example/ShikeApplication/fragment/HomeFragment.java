package com.example.ShikeApplication.fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.ShikeApplication.R;
import com.example.ShikeApplication.carmera.CameraSurfaceView;
import com.example.ShikeApplication.ndkdemo.ndktool;
import com.example.ShikeApplication.opengl.GLSLPlayer;
import com.example.ShikeApplication.opengl.GLSLSurfaceView;
import com.example.ShikeApplication.utils.AssetsUtil;
import com.serenegiant.usb.USBMonitor;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class HomeFragment extends Fragment {

    private static HomeFragment homeFragment;
    @BindView(R.id.fagment_native_demo_text)
    TextView textView;
    @BindView(R.id.opengl_el_surface_view)
    GLSLSurfaceView mLocalGLSLSurfaceView;
//    @BindView(R.id.button_cancel)
//    Button buttonCancel;
//    @BindView(R.id.button_ok)
//    Button buttonOk;
//    @BindView(R.id.carmare_surface_view)
//    CameraSurfaceView surfaceView;
    Unbinder unbinder;

    private USBMonitor mUSBMonitor;
    private GLSLPlayer mGLSLPlayer;

    private HomeFragment() {
    }

    public static HomeFragment getInstance(){
        if(homeFragment == null){
            synchronized (HomeFragment.class){
                if(homeFragment == null){
                    homeFragment = new HomeFragment();
                }
            }
        }
        return homeFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_home,container,false);
        View view = inflater.inflate(R.layout.fragment_native_demo,container,false);
        unbinder =  ButterKnife.bind(this,view);
        textView.setText(ndktool.getSomeDumpTextFromNDK() + "\n"+ndktool.getNativeLibraryVersion());
        Toast.makeText(this.getContext(),ndktool.getNativeCompileVersion(),Toast.LENGTH_LONG).show();

        mGLSLPlayer.setGLSLSurfaceView(mLocalGLSLSurfaceView);
        mGLSLPlayer.onCallRenderYUV();
        showDemoDialog();
        return view;
    }

    private void showDemoDialog() {
        String str = AssetsUtil.initAssets("PrivacyPolicy.txt",getActivity());
        final View inflate = LayoutInflater.from(getActivity()).inflate(R.layout.privacy_policy_background_dialog, null);
        TextView tv_title = (TextView) inflate.findViewById(R.id.tv_title);
        tv_title.setText("隐私政策");
        TextView tv_content = (TextView) inflate.findViewById(R.id.tv_content);
        tv_content.setText(str);
        final Dialog dialog = new AlertDialog
                .Builder(getActivity())
                .setView(inflate)
                .show();
        final WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = 800;
        params.height = 1200;
        dialog.getWindow().setAttributes(params);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
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

    //[08/10/19 sk] native jni demo
//    @OnClick({R.id.button_ok , R.id.button_cancel})
//    public void onViewClicked(@Nullable View view) {
//        switch (view.getId()) {
//            case R.id.button_ok:
//                surfaceView.doTakePhotoPath();
//                break;
//        }
//    }

}
