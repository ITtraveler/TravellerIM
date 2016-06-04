package com.hgs.travelerim.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hgs.travelerim.BaseActivity;
import com.hgs.travelerim.LoginActivity;
import com.hgs.travelerim.R;
import com.travelerim.tool.XMPPUtil;

/**
 * Created by Administrator on 2016/3/17.
 */
public class Fragment_tab3 extends Fragment implements View.OnClickListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set_layout, null);
        view.findViewById(R.id.bn_exitLogin).setOnClickListener(this);
        view.findViewById(R.id.bn_exitApp).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bn_exitLogin:
                exitLogin();
                break;
            case R.id.bn_exitApp:
                exitApp();
                break;
        }
    }


    private void exitLogin() {
        BaseActivity baseActivity = (BaseActivity)getActivity();
        XMPPUtil.exitLogin();//使账号退出登入
        Intent intent = new Intent(this.getActivity(), LoginActivity.class);
        startActivity(intent);
        this.getActivity().finish();

    }

    private void exitApp() {
      //  ActivityManager manager = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
     //   manager.killBackgroundProcesses(getContext().getPackageName());
        onDestroy();
        System.exit(0);
    }

}
