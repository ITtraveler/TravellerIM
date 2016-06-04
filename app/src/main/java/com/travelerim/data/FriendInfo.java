package com.travelerim.data;

/**
 * Created by Administrator on 2016/3/20.
 */
public class FriendInfo {
    private String head;
    private String name;
    private String status;

    public FriendInfo(String head, String name, String status) {
        this.head = head;
        this.name = name;
        this.status = status;
    }

    public String getHead() {
        return head;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }
}
