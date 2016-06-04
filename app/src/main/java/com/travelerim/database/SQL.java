package com.travelerim.database;

import com.travelerim.data.TravelerDate;

/**
 * Created by Administrator on 2016/4/2.
 */
public class SQL {
    public static final String SQL_CT_USERACCOUNT = "create table userAccount(" +
            "JID primary key," +
            "userName," +
            "passWord," +
            "status," +
            "remember)";

    public static final String SQL_CT_FRIENDS = "create table friends(" +
            "JID," +
            "name," +
            "groupName," +
            "msgCount)";

    public static final String SQL_CT_RECENT_CHAT = "create table recent_chat(" +
            "JID," +
            "name," +
            "recentContent," +
            "time," +
            "msgCount)";


    //用于动态创建聊天表，对于每个聊天对象，对应一张表
    public static String SQL_CT_Chat(String friName) {
        String SQL_CT_Chat = "create table chat_" + friName + "(" +
                "JID," +
                "name," +
                "content," +
                "time," +
                "type," +
                "sORr," +
                "isRead DEFAULT 0)";
        return SQL_CT_Chat;
    }

    public static String SQL_Q_UNREADMSG(String friendName) {
        String chat_table = TravelerDate.CHAT_TABLE_FLAG + friendName;
        String SQL_Q_UNREAD = "SELECT content from " + chat_table + " where isRead = 0 Order by time";
        return SQL_Q_UNREAD;
    }

    public static String SQL_UP_READ(String friendName) {
        String chat_table = TravelerDate.CHAT_TABLE_FLAG + friendName;
        String SQL_UP = "UPDATE " + chat_table + " SET isRead = 1 WHERE isRead = 0";
        return SQL_UP;
    }

    public static String SQL_COUNT_UNREAD(String friendName) {
        String chat_table = TravelerDate.CHAT_TABLE_FLAG + friendName;
        String SQL_COUNT = "SELECT COUNT(isRead) from " + chat_table + " where isRead = 0";
        return SQL_COUNT;
    }

    public static String SQL_COUNT_READ(String friendName) {
        String chat_table = TravelerDate.CHAT_TABLE_FLAG + friendName;
        String SQL_COUNT = "SELECT COUNT(isRead) from " + chat_table + " where isRead = 0";
        return SQL_COUNT;
    }

    /**
     * 从未位开始
     *
     * @param friendName
     * @param start      查询范围的起始位置
     * @param end        查询范围的未位置
     * @return
     */
    public static String SQL_QRead_RANGE(String friendName, int start, int end) {
        String chat_table = TravelerDate.CHAT_TABLE_FLAG + friendName;
        String SQL_RNAGE = "SELECT content,sORr from " + chat_table + " where isRead = 1 order by time desc limit " + start + "," + end;
        return SQL_RNAGE;
    }

    public static String SQL_QUNRead_RANGE(String friendName, int start, int end) {
        String chat_table = TravelerDate.CHAT_TABLE_FLAG + friendName;
        String SQL_RNAGE = "SELECT content from " + chat_table + " where isRead = 0 limit " + start + "," + end;
        return SQL_RNAGE;
    }

    /**
     * 删除表中重复的数据
     *
     * @param tableName
     * @return
     */
    public static String SQL_DEL_REPEAT(String tableName) {
        String SQL_DEL = "DELETE FROM " + tableName + " WHERE rowid NOT IN(SELECT MAX(rowid) rowid FROM friends GROUP BY JID)";
        return SQL_DEL;
    }

    public static String SQL_UP_RECENT_CHAT(String friendName) {
        String sql = "update recent_chat set msgCount = 0 where name = '" + friendName + "'";
        return sql;
    }
}
