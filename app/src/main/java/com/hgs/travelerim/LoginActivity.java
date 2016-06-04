package com.hgs.travelerim;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.travelerim.data.TravelerDate;
import com.travelerim.data.User;
import com.travelerim.database.DatabaseUtil;
import com.travelerim.database.SQL;
import com.travelerim.tool.ProxyXMPPUtil;
import com.travelerim.tool.TravelerUtil;
import com.travelerim.tool.XMPPUtil;


import org.jivesoftware.smack.XMPPConnection;

/**
 * Created by Administrator on 2016/3/14.
 */
public class LoginActivity extends BaseActivity {
    private EditText etUsername;
    private EditText etPassword;
    private EditText etServerAdd;
    private CheckBox cb_remember;

    private User user;
    //private ProxyXMPPUtil proxyXMPPUtil;
    XMPPConnection connection;
    DatabaseUtil databaseUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        //proxyXMPPUtil = new ProxyXMPPUtil();
        databaseUtil = new DatabaseUtil(this);
        etUsername = (EditText) findViewById(R.id.login_username);
        etPassword = (EditText) findViewById(R.id.login_password);
        etServerAdd = (EditText) findViewById(R.id.login_server);
        cb_remember = (CheckBox) findViewById(R.id.checkBox_remember);
        firstInit();//初次使用
    }

    private void firstInit() {
        boolean isFirst = TravelerUtil.isFirstUse(this);
        if (!isFirst) {
            checkRem();//从数据库检查是否为记住密码
        } else {
            TravelerUtil.setFirstUse(this, false);
        }
    }

    private void checkRem() {
        User user = TravelerUtil.getUser(this);
        String userName = user.getUsername().trim();
        Log.i("login check userName:", userName);
        if (user.getUsername().length() > 1) {
            try {

                databaseUtil.createOrOpenDB(userName.toUpperCase());
                String result[] = databaseUtil.query("select remember from userACCOUNT" +
                        " where userName = '" + userName + "'", 0);
                //  System.out.println(result[0]);
                if (result[0].equals("1")) {
                    System.out.println(userName + "   " + user.getPassword());
                    cb_remember.setChecked(true);
                    etUsername.setText(userName);
                    etPassword.setText(user.getPassword());
                }
                databaseUtil.closeDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x1:
                    myToast("用户名或密码为空。");
                    break;
                case 0x2:
                    myToast("登入失败，请检查用户名、密码和服务器！");
            }

        }
    };

    public void login(View view) {
        boolean isOk = false;//用于检测信息的完整性
        String name = etUsername.getText().toString();
        String psw = etPassword.getText().toString();
        final String server = etServerAdd.getText().toString();
        if (name.length() > 0 & psw.length() > 0) {//& server != null
            user = new User(name, psw);
            isOk = true;
        } else {
            handler.sendEmptyMessage(0x1);
            isOk = false;
        }
        //如果信息填写完整，进行登入操作
        if (isOk) {
            System.out.println("------1");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //如果登入成功
                    if (XMPPUtil.login(TravelerDate.HOST, user)) {
                        exeDatabaseStep(user);
                        checkBox(user);

                        TravelerUtil.saveUser(LoginActivity.this, user);
                        TravelerDate.DBNAME = user.getUsername().toUpperCase().trim();
                        TravelerDate.USERNAME = user.getUsername().toLowerCase();
                        TravelerDate.PASSWORD = user.getPassword();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        Bundle bundle = new Bundle();
                        //将bundle一个序列化的user，传于MainActivity中
                        bundle.putSerializable("user", user);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finishActivity();
                    } else {
                        handler.sendEmptyMessage(0x2);
                    }
                }
            }).start();
        }

    }

    /**
     * 执行数据库的步骤
     */
    private void exeDatabaseStep(User user) {
        String jid = user.getUsername() + TravelerDate.REGION;
        String userName = user.getUsername().trim();
        String passWord = user.getPassword().trim();
        try {
            databaseUtil.createOrOpenDB(user.getUsername().toUpperCase());
            boolean isExist = databaseUtil.tableIsExist("userAccount");
            if (!isExist) {
                databaseUtil.createTable(SQL.SQL_CT_USERACCOUNT);
                databaseUtil.insert("insert into userAccount(JID,userName,passWord) " +
                        "values('" + jid + "','" + userName + "','" + passWord + "')");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */

    private void checkBox(User user) {
        if (cb_remember.isChecked()) {
            databaseUtil.update("update userAccount set remember = '1' where userName = '" + user.getUsername().trim() + "'");
        } else {
            databaseUtil.update("update userAccount set remember = '0' where userName = '" + user.getUsername().trim() + "'");
        }
    }

    /**
     * 启动注册界面
     *
     * @param view
     */
    public void regist(View view) {
        Intent intent = new Intent(LoginActivity.this, RegistActivity.class);
        startActivity(intent);
    }

    @Override
    public void finish() {
        super.finish();
        databaseUtil.closeDatabase();
    }

}
