package com.travelerim.data;

/**
 * Created by Administrator on 2016/3/15.
 */
public abstract class TravelerDate {
    public static final String HOST = "192.16.137.1";
    public static final int PORT = 5222;
    public static final String REGION = "@hgs";
    public static final String CHAT_TABLE_FLAG = "chat_";
    public static final int RECEIVE = 0;
    public static final int SEND = 1;
    public static String DBNAME = "";
    public static String USERNAME = "";
    public static String PASSWORD = "";
    public static String USERJID(String DBNAME){
        return DBNAME+REGION;
    }
}
