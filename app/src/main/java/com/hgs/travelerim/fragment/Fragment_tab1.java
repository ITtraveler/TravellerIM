package com.hgs.travelerim.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import com.hgs.travelerim.Adapter.CurChatListAdapter;
import com.hgs.travelerim.ChatActivity;
import com.hgs.travelerim.R;
import com.travelerim.data.RecentChat;
import com.travelerim.data.TravelerDate;
import com.travelerim.database.DatabaseUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by Administrator on 2016/3/17.
 */
public class Fragment_tab1 extends Fragment implements AdapterView.OnItemClickListener {


    private ListView recentlist;
    private CurChatListAdapter myAdapter;
    private DatabaseUtil databaseUtil;
    private List<RecentChat> rcList = new ArrayList<>();
    Timer timer = new Timer();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseUtil = new DatabaseUtil(getContext());
        timerTast();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recent_layout, null);
        recentlist = (ListView) view.findViewById(R.id.fragment_recent_list);
        myAdapter = new CurChatListAdapter(this.getContext(), rcList);
        recentlist.setAdapter(myAdapter);
        recentlist.setOnItemClickListener(this);
        // recentlist.setOnClickListener(this);
        return view;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x001:
                    myAdapter.notifyDataSetChanged();
                    recentlist.invalidate();
                    break;
            }
        }
    };

    private void timerTast() {

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                rcList.clear();
                RecentChat[] rc = getRecent_chat();

                if (rc.length > 0 & rc != null)
                    for (int i = 0; i < rc.length; i++) {
                        rcList.add(rc[i]);
                    }
                handler.sendEmptyMessage(0x001);
            }
        }, 500, 1000);
    }

    /**
     * 从recent_chat表中获取name，recentContent，time，msgCount。并封装成RecentChat
     *
     * @return
     */
    private RecentChat[] getRecent_chat() {
        String sql = "select name,recentContent,time,msgCount from recent_chat Order by time desc";
        RecentChat recentChats[];
        String name, content, time, msgCount;
        databaseUtil.createOrOpenDB(TravelerDate.DBNAME);
        String result[][] = databaseUtil.query(sql);
        if (result != null) {
            recentChats = new RecentChat[result.length];
            for (int i = 0; i < result.length; i++) {
                name = result[i][0];
                content = result[i][1];
                time = result[i][2];
                msgCount = result[i][3];
                //if (name != null & content != null & time != null & msgCount != null) {
                RecentChat recentChat = new RecentChat(name, content, time, msgCount);
                recentChats[i] = recentChat;
                //}
            }
            databaseUtil.closeDatabase();
            return recentChats;
        }
        databaseUtil.closeDatabase();
        // dbUtil.query(sql);
        return null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //System.out.println(position + "   " + id);
        //Toast.makeText(this.getContext(), "click", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this.getActivity(), ChatActivity.class);
        String friendName = rcList.get(position).getName();
        intent.putExtra("friendName", friendName);//所点击的用户名传入 chatActivity
        startActivity(intent);


    }
}
