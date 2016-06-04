package com.travelerim.xmpp;

import android.content.Context;
import android.util.Log;

import com.travelerim.data.TravelerDate;
import com.travelerim.data.User;
import com.travelerim.database.DatabaseUtil;
import com.travelerim.database.SQL;
import com.travelerim.tool.TravelerUtil;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

/**
 * Created by Administrator on 2016/3/31.
 */

public class MyProcessMessage implements MessageListener {
    DatabaseUtil databaseUtil;
    private User user;

    public MyProcessMessage(Context context, User user) {
        databaseUtil = new DatabaseUtil(context);
        this.user = user;
    }

    /**
     * 用于接收所有消息，将收到的信息存入数据库
     *
     * @param chat
     * @param message
     */
    @Override
    public void processMessage(Chat chat, Message message) {
        message.getBody();
        Log.i("ProcessMessage:", message.toXML());
        String type = message.getType().toString();

        System.out.println("from:" + message.getFrom() + " to:" + message.getTo() + "  message:" + message.getBody()
                + "\n  Language:" + message.getLanguage() + "  Thread:" + message.getThread() + "  " + message.getPacketID());

        //将数据存入数据库
        if (message.getType() == Message.Type.chat) {
            String from[] = message.getFrom().split("/");
            String JID = from[0];
            String[] s = JID.split("@");
            String name = s[0];
            String content = message.getBody().trim();
            String time = TravelerUtil.getTime();
            try {
                if (content.length() > 0) {//当有内容时
                    databaseUtil.createOrOpenDB(user.getUsername().toUpperCase());
                    //System.out.println("name:"+name[0]);
                    if (!databaseUtil.tableIsExist("chat_" + name)) {
                        String chatTable = SQL.SQL_CT_Chat(name);
                        databaseUtil.createTable(chatTable);
                    }
                    String insert_sql = "insert into chat_" + name + "(JID,name,content,time,type,sORr)" +
                            " values('" + JID + "','" + name + "','" + content + "','" + time + "','" + type + "'," + TravelerDate.RECEIVE + ")";
                    databaseUtil.insert(insert_sql);

                    databaseUtil.delect("delete from recent_chat where name = '" + name + "'");//删除recent_chat表中name的记录
                    String result[] = databaseUtil.query(SQL.SQL_COUNT_UNREAD(name), 0);
                    String unReadCount = result[0];
                    String sql = "insert into recent_chat values('" + JID + "','" + name + "','" + content + "','" + time + "'," + unReadCount + ")";
                    databaseUtil.insert(sql);
                    databaseUtil.closeDatabase();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
