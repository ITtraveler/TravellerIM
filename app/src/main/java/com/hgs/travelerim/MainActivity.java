package com.hgs.travelerim;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioGroup;

import com.hgs.travelerim.Service.PresenceService;
import com.hgs.travelerim.fragment.Fragment_tab1;
import com.hgs.travelerim.fragment.Fragment_tab2;
import com.hgs.travelerim.fragment.Fragment_tab3;
import com.travelerim.data.ListInfo;
import com.travelerim.data.TravelerDate;
import com.travelerim.data.User;
import com.travelerim.database.DatabaseUtil;
import com.travelerim.database.SQL;
import com.travelerim.tool.XMPPUtil;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

import java.util.List;


public class MainActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {
    User user;

    private RadioGroup radioGroup;
    private FragmentTransaction transaction;
    private Fragment fragment1;
    private Fragment fragment2;
    private Fragment fragment3;
    private FragmentManager fm;

    DatabaseUtil databaseUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("MainActivity:", "created");
        setContentView(R.layout.activity_main);
        user = (User) this.getIntent().getSerializableExtra("user");
        //XMPPUtil.login(TravelerDate.HOST,user);
        init();

        //new XMPPUtil().process();

        //Intent presenceService = new Intent(this, PresenceService.class);
        //startService(presenceService);
        initDatabase();

        new XMPPUtil().message(MainActivity.this, user);
      //  new AsyncTaskDB();
    }

    private void init() {

        radioGroup = (RadioGroup) findViewById(R.id.tab_group);
        radioGroup.setOnCheckedChangeListener(this);
        fragment1 = new Fragment_tab1();
        fragment2 = new Fragment_tab2();
        fragment3 = new Fragment_tab3();
        Bundle bundle = new Bundle();
        bundle.putString("userName", user.getUsername());
        fragment2.setArguments(bundle);
        //fragment1.setArguments();
        fm = getSupportFragmentManager();
        transaction = fm.beginTransaction();
        transaction.add(R.id.frameLayout, fragment3);
        transaction.add(R.id.frameLayout, fragment2);
        transaction.add(R.id.frameLayout, fragment1);
        transaction.hide(fragment2);
        transaction.hide(fragment3);
        fragment2.onPause();// 暂停fragment，防止fragment乱窜
        fragment3.onPause();
        transaction.commit();
    }

    /**
     * 将好友列表存入数据库
     */
    private void initDatabase() {
        databaseUtil = new DatabaseUtil(this);
        databaseUtil.createOrOpenDB(user.getUsername().toUpperCase());
        //创建最近聊天表
        boolean isExist = databaseUtil.tableIsExist("recent_chat");
        if (!isExist) {
            databaseUtil.createTable(SQL.SQL_CT_RECENT_CHAT);
        }
        //创建朋友表
        boolean isExist2 = databaseUtil.tableIsExist("friends");
        if (!isExist2) {
            databaseUtil.createTable(SQL.SQL_CT_FRIENDS);
        }
        ListInfo list = XMPPUtil.getListInfo();

        List<String> groups = list.getGroupList();
        List<List<String>> child = list.getChildList();
        for (int i = 1; i < child.size(); i++) {
            List<String> friends = child.get(i);
            String groupName = groups.get(i);
            for (String friend : friends) {
                databaseUtil.insert("insert into friends(JID,name,groupName,msgCount) values('" + friend + TravelerDate.REGION + "','" +
                        friend + "','" +
                        groupName + "','0')");
            }
        }
        //删除重复数据
        databaseUtil.delect(SQL.SQL_DEL_REPEAT("friends"));
        databaseUtil.closeDatabase();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        System.out.println("select :" + checkedId);

        switch (checkedId) {
            case R.id.tab1:
                System.out.println("selecttab1 :" + checkedId);
                // transaction.show(fragment1);
                setTabSelect(0);
                break;
            case R.id.tab2:
                System.out.println("selecttab2 :" + checkedId);
                setTabSelect(1);
                // transaction.show(fragment2);
                break;
            case R.id.tab3:
                System.out.println("selecttab3 :" + checkedId);
                // transaction.show(fragment3);
                setTabSelect(2);
                break;
            default:

                break;
        }
        // transaction.commit();
    }

    // 利用事务显示对应的fragment
    private void setTabSelect(int i) {
        fm = getSupportFragmentManager();
        transaction = fm.beginTransaction();
        hideFragments(transaction);
        switch (i) {
            case 0:
                if (fragment1 == null) {
                    fragment1 = new Fragment_tab1();
                    transaction.add(R.id.frameLayout, fragment1);

                } else {
                    fragment1.onResume();
                    transaction.show(fragment1);
                }
                break;
            case 1:
                if (fragment2 == null) {
                    fragment2 = new Fragment_tab2();
                    Bundle bundle = new Bundle();
                    bundle.putString("userName", user.getUsername());
                    fragment2.setArguments(bundle);
                    transaction.add(R.id.frameLayout, fragment2);
                } else {
                    fragment2.onResume();
                    transaction.show(fragment2);
                }
                break;
            case 2:
                if (fragment3 == null) {
                    fragment3 = new Fragment_tab3();
                    transaction.add(R.id.frameLayout, fragment3);
                } else {
                    fragment3.onResume();
                    transaction.show(fragment3);
                }
                break;
            default:
                break;
        }
        transaction.commit();
    }

    // 对fragment隐藏，并暂停
    private void hideFragments(FragmentTransaction transaction) {
        if (fragment1 != null) {
            transaction.hide(fragment1);
            fragment1.onPause();
        }
        if (fragment2 != null) {
            transaction.hide(fragment2);
            fragment2.onPause();
        }
        if (fragment3 != null) {
            transaction.hide(fragment3);
            fragment2.onPause();
        }
    }

    /**
     * 重写finish 使其不结束activity生命周期，而是返回home
     */


    /**
     * 对系统返回键重新
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // return super.onKeyDown(keyCode, event);
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            Intent mHomeIntent = new Intent(Intent.ACTION_MAIN);
            mHomeIntent.addCategory(Intent.CATEGORY_HOME);
            mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            startActivity(mHomeIntent);
            return true;
        }
        return false;
    }

    @Override
    public void finish() {
        super.finish();
        Log.i("MainActivity:", "finish");
    }

    @Override
    public void recreate() {
        super.recreate();
        Log.i("MainActivity:", "recreate");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("MainActivity:", "onResume");
    }

    class AsyncTaskDB extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_add_friend:
                myToast("添加好友");
                return true;
            case R.id.menu_help:
                myToast("help");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        System.out.println("onRestart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("onDestory");
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("onPause");
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("onStart");
    }
}
