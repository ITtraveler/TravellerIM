package com.hgs.travelerim;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.hgs.travelerim.Adapter.ChatListAdapter;
import com.travelerim.data.ChatEntity;
import com.travelerim.data.TravelerDate;
import com.travelerim.database.DatabaseUtil;
import com.travelerim.database.SQL;
import com.travelerim.tool.TravelerUtil;
import com.travelerim.tool.XMPPUtil;


import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/3/16.
 */
public class ChatActivity extends BaseActivity implements OnClickListener {

    private ImageButton more, expand;
    private TextView title_name;
    private Button send, back;
    private EditText sendContent;
    private ListView msgList;
    private String friendName, userName;
    private String table_name = "";
    // private List<ChatEntity> chatEntityList = new ArrayList<>();//废弃封装的数据，原因太占内存，容易引起内存溢出
    private ChatListAdapter listAdapter;
    private String unReadMsg[];
    //private MyMessageListener myMessageListener;
    private Chat chat;
    private Boolean isExistChatTable = false;
    DatabaseUtil databaseUtil;

    private int dataPosition = 0;
    //一下两 要一起设值，保存两的长度相同，本可以将他们封装用，为了减轻内存，就不封装了
    private List<String> contents = new ArrayList<>();//
    private List<Integer> types = new ArrayList<>();//

    private boolean isFirstSend = true;
    Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_layout);
        getSupportActionBar().hide();
        initData();
        init();
        title_name.setText(friendName);
        listAdapter = new ChatListAdapter(this, contents, types);
        msgList.setAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();//通知适配器，数据更新
        msgList.setSelection(listAdapter.getCount());//可以在xml文件中设置stackFromBottom 不过 这样在开始聊天是向上顶的感觉。
        timeTask();
    }

    private void initData() {
        /*//test
        for(int i = 0;i<1000;i++){
            //chatEntityList.add(new ChatEntity("2016.13,21 22:16", "我很好", ChatEntity.RECEIVE));
            msg.add("msg"+i);
            type.add(0);
        }*/

        databaseUtil = new DatabaseUtil(this);
        friendName = getIntent().getStringExtra("friendName").trim();
        userName = TravelerDate.USERNAME;
        table_name = TravelerDate.CHAT_TABLE_FLAG + friendName;
        createChat(friendName + "@hgs");//创建一个聊天
        //没有未读信息历史内容

        String getReadMsg[] = getReadMsg(friendName, 0, 5, 0);
        String getsORr[] = getReadMsg(friendName, 0, 5, 1);
        if (getReadMsg != null & getsORr != null) {
            for (int i = 0; i < getReadMsg.length; i++) {
                System.out.println(getReadMsg[i] + "    " + getsORr[i]);
                contents.add(0, getReadMsg[i]);
                types.add(0, Integer.valueOf(getsORr[i]));
            }
        }

        //初始化展示未读信息
        unReadMsg = getUnreadMsg(friendName);
        if (unReadMsg != null) {
            //将recent_chat中的msgCount变为0，这里不需要在判断recent_chat是否有此字段了，因为，在接收信息时，一定创建了此字段
            databaseUtil.createOrOpenDB(TravelerDate.DBNAME);
            databaseUtil.update(SQL.SQL_UP_RECENT_CHAT(friendName));
            databaseUtil.closeDatabase();
            for (String msg : unReadMsg) {
                System.out.println(msg);
                contents.add(msg);
                types.add(TravelerDate.RECEIVE);
                //chatEntityList.add(new ChatEntity("", msg, ChatEntity.RECEIVE));
            }
        }
    }

    private void init() {
        back = (Button) findViewById(R.id.chat_back);
        more = (ImageButton) findViewById(R.id.chat_userMore);
        title_name = (TextView) findViewById(R.id.chat_friend_name);
        expand = (ImageButton) findViewById(R.id.chat_bn_expand);
        send = (Button) findViewById(R.id.chat_bn_send);
        sendContent = (EditText) findViewById(R.id.chat_send_content);
        msgList = (ListView) findViewById(R.id.chat_list);
        back.setOnClickListener(this);
        more.setOnClickListener(this);
        expand.setOnClickListener(this);
        send.setOnClickListener(this);
    }

    //各按钮事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chat_back:
                bn_back();
                break;
            case R.id.chat_userMore:
                bn_userMore();
                break;
            case R.id.chat_bn_expand:
                bn_expand();
                break;
            case R.id.chat_bn_send:
                bn_send();
                break;
        }
    }


    /**
     * 发送消息
     */
    private void bn_send() {
        String content = sendContent.getText().toString().trim();

        if (content.length() > 0 & !content.isEmpty()) {//当输入框有内容时
            contents.add(content);
            types.add(TravelerDate.SEND);
            Log.i("ChatActivity:", content);
            try {
                synchronized (this) {
                    sendMessage(content);//发送内容
                    saveToDB(content);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            listAdapter.notifyDataSetChanged();//通知适配器，数据更新
            msgList.setSelection(listAdapter.getCount());//可以在xml文件中设置stackFromBottom 不过 这样在开始聊天是向上顶的感觉。
            msgList.invalidate();
            if (isFirstSend) {
                init_Recent_chatTable();
            }
            //sendContent.setText("");
        }
    }

    private void init_Recent_chatTable() {
        databaseUtil.createOrOpenDB(TravelerDate.DBNAME);
        databaseUtil.delect("delete from recent_chat where name = '" + friendName + "'");
        String JID = friendName + "@hgs";
        String sql = "insert into recent_chat(JID,name,msgCount) values('" + JID + "','" + friendName + "',0)";
        databaseUtil.insert(sql);
        databaseUtil.closeDatabase();
        isFirstSend = false;
    }

    private void bn_expand() {
        myToast("功能待开发，敬请期待！");
    }

    private void bn_userMore() {
        myToast("功能待开发，敬请期待！");
    }

    private void bn_back() {
        this.finish();
        // chat.removeMessageListener(myMessageListener);
    }

    @Override
    public void finish() {

        contents = null;
        types = null;
        databaseUtil.createOrOpenDB(TravelerDate.DBNAME);
        String tableName = "chat_" + friendName;
        Boolean isExit = databaseUtil.tableIsExist(tableName);
        if (isExit) {
            //查询最后一条数据的内容和时间
            String result[][] = databaseUtil.query("select content,time from chat_" + friendName + " order by time desc limit 1");
            //当结果不为空，有进行发送操作时
            System.out.println("firstSend:" + isFirstSend);
            if (result.length > 0) {
                String content = result[0][0];
                String time = result[0][1];
                System.out.println("cc:" + content + "  " + time);
                //将内容和时间，更新到recent_chat
                try {
                    databaseUtil.update("update recent_chat set recentContent = '" + content + "',time = '" + time + "',msgCount = 0 where name = '" + friendName + "'");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        databaseUtil.closeDatabase();
        timer.cancel();
        //chatEntityList = null;
        //chat.removeMessageListener(myMessageListener);//移除当前监听器
        System.out.println("当前聊天窗口已退出。");
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("ChatActivity:", "onDestroy");
    }

    /**
     * 创建一个聊天，并实时监听着对方，发来的信息
     *
     * @param userJID
     */
    private void createChat(String userJID) {
        XMPPConnection xmppConnection = XMPPUtil.getXMPPConnection();//获取当前的连接
        ChatManager cm = xmppConnection.getChatManager();
        chat = cm.createChat(userJID, null);
        /*
        cm.addChatListener(new ChatManagerListener() {
            @Override
            public void chatCreated(Chat chat, boolean b) {
                //会出现内存溢出和xmppConnection空指针异常   原因 listView 界面适配器有问题
                myMessageListener = new MyMessageListener();
                chat.addMessageListener(myMessageListener);
            }
        });*/

    }

    private void sendMessage(String content) {
        try {
            chat.sendMessage(content);
        } catch (XMPPException e) {
            myToast("消息发送失败！");
            e.printStackTrace();
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0x001:

                    listAdapter.notifyDataSetChanged();//通知适配器，数据更新
                    msgList.setSelection(listAdapter.getCount());
                    msgList.invalidate();
                    break;
            }
        }
    };

//ok

    /**
     * 获取未读消息
     *
     * @param friendName
     * @return
     */
    private String[] getUnreadMsg(String friendName) {
        //String table_name = TravelerDate.CHAT_TABLE_FLAG + friendName;
        databaseUtil.createOrOpenDB(TravelerDate.DBNAME);
        // System.out.println(table_name);
        Boolean isExist = databaseUtil.tableIsExist(table_name);
        // System.out.println("getUnreadMsg:" + isExist);
        if (isExist) {
            //String cResult[] = databaseUtil.query(SQL.SQL_COUNT_UNREAD(friendName));
            //int count = Integer.valueOf(cResult[0]);
            //System.out.println(count + "   " + cResult.length);
            String content[] = databaseUtil.query(SQL.SQL_Q_UNREADMSG(friendName), 0);
            databaseUtil.update(SQL.SQL_UP_READ(friendName));//将表中未读的数据改为已读
            databaseUtil.closeDatabase();
            return content;
        }
        databaseUtil.closeDatabase();
        return null;
    }

    //ok
    public String[] getReadMsg(String friendName, int start, int end, int position) {
        databaseUtil.createOrOpenDB(TravelerDate.DBNAME);
        Boolean isExist = databaseUtil.tableIsExist(table_name);
        if (isExist) {
            String c[] = databaseUtil.query(SQL.SQL_COUNT_UNREAD(friendName), 0);
            int count = Integer.valueOf(c[0]);
            if (count == 0) {
                String content[] = databaseUtil.query(SQL.SQL_QRead_RANGE(friendName, start, end), position);
                databaseUtil.closeDatabase();
                return content;
            }
        }
        databaseUtil.closeDatabase();
        return null;
    }

    //ok
    public void saveToDB(String content) {
        try {
            String sql = "insert into " + table_name + "(JID,name,content,time,sORr,isRead)" +
                    " values('" + TravelerDate.USERJID(TravelerDate.DBNAME).toLowerCase() + "','" + TravelerDate.DBNAME + "','" +
                    content + "','" + TravelerUtil.getTime() + "'," + TravelerDate.SEND + ",1)";
            databaseUtil.createOrOpenDB(TravelerDate.DBNAME);
            if (!isExistChatTable) {//目的减少多次的检查表
                System.out.println(table_name);
                Boolean isExist = databaseUtil.tableIsExist(table_name);
                System.out.println(isExist);
                if (!isExist) {//建表
                    databaseUtil.createTable(SQL.SQL_CT_Chat(friendName));
                }
                databaseUtil.insert(sql);
                isExistChatTable = true;
            } else {//保存数据
                databaseUtil.insert(sql);
            }
            System.out.println("save to db.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        databaseUtil.closeDatabase();
    }

    //ok
    private void timeTask() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                String msgs[] = getUnreadMsg(friendName);
                if (msgs != null) {
                    for (String msg : msgs) {
                        // System.out.println(msg);
                        contents.add(msg);

                            types.add(TravelerDate.RECEIVE);
                            //chatEntityList.add(new ChatEntity("", msg, ChatEntity.RECEIVE));
                            handler.sendEmptyMessage(0x001);

                    }
                }
            }
        }, 500, 1000);
    }
}
