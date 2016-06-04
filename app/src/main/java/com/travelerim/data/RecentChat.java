package com.travelerim.data;

/**
 * 封装的是最近聊天列表的数据
 * Created by Administrator on 2016/4/13.
 */
public class RecentChat {
    private String name;
    private String content;
    private String time;
    private String msgCount;

    public RecentChat(String name, String content, String time, String msgCount) {
        this.name = name;
        this.content = content;
        this.time = time;
        this.msgCount = msgCount;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public String getTime() {
        return time;
    }

    public String getMsgCount() {
        return msgCount;
    }
}
