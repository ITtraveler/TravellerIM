package com.hgs.travelerim;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;

import com.travelerim.data.TravelerDate;
import com.travelerim.data.UserInfo;
import com.travelerim.tool.ProxyXMPPUtil;
import com.travelerim.tool.XMPPUtil;


/**
 * Created by Administrator on 2016/3/15.
 */
public class RegistActivity extends BaseActivity {
    private EditText rName;
    private EditText rPsw;
    private EditText rPsw_a;
    private EditText rEmail;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x1:
                    myToast("账户创建成功！");
                    break;
                case 0x2:
                    myToast("账户创建失败,用户名已被占用！");
                    break;
            }
        }
    };

    //private ProxyXMPPUtil proxyXMPPUtil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.regist_layout);
       // proxyXMPPUtil = new ProxyXMPPUtil();
        init();
    }

    private void init() {
        rName = (EditText) findViewById(R.id.regist_username);
        rPsw = (EditText) findViewById(R.id.regist_psw);
        rPsw_a = (EditText) findViewById(R.id.regist_psw_a);
        rEmail = (EditText) findViewById(R.id.regist_email);

    }

    /**
     * 创建账户，提交信息
     *
     * @param v
     */
    public void createUser(View v) {
        final String name = rName.getText().toString();
        final String psw = rPsw.getText().toString();
        String psw_a = rPsw_a.getText().toString();
        final String email = rEmail.getText().toString();
        if (dealInfo(name, psw, psw_a)) {//填写的信息都ok了
            System.out.println("OK......");
            if (email == null)
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (XMPPUtil.createAccount(TravelerDate.HOST, new UserInfo(name, psw))) {
                            // myToast("账户创建成功！");
                            handler.sendEmptyMessage(0x1);
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            RegistActivity.this.finish();
                        } else {
                            handler.sendEmptyMessage(0x2);
                        }
                    }
                }).start();
            else
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (XMPPUtil.createAccount(TravelerDate.HOST, new UserInfo(name, psw, email))) {
                            // myToast("账户创建成功！");
                            handler.sendEmptyMessage(0x1);
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            RegistActivity.this.finish();
                        } else {
                            handler.sendEmptyMessage(0x2);
                        }
                    }
                }).start();
        }

    }

    private boolean dealInfo(String name, String psw, String psw_a) {
        if (name.length() == 0 || psw.length() == 0 || psw_a.length() == 0) {
            // handler.sendEmptyMessage(0x3);
            myToast("请填写完整信息。");
            return false;
        } else if (name.length() < 3 || name.length() > 12) {
            myToast("用户名请保持在3到12个字符。");
            return false;
        } else if (psw.length() < 6 || psw.length() > 16) {
            myToast("密码长度请保持在6到16个字符。");
            return false;
        } else if (!psw.equals(psw_a)) {
            // handler.sendEmptyMessage(0x4);
            myToast("两次密码不匹配，请重新输入！");
            return false;
        }
        return true;
    }


}
