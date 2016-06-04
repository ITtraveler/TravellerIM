package com.travelerim.data;

import java.util.List;

/**
 * Created by Administrator on 2016/3/20.
 */
public class ListInfo<E> {
    private List<E> groupList;
    private List<List<E>> childList;

    public ListInfo() {

    }

    public ListInfo(List<E> groupList, List<List<E>> childList) {
        this.groupList = groupList;
        this.childList = childList;
    }

    public List<E> getGroupList() {
        return groupList;
    }

    public List<List<E>> getChildList() {
        return childList;
    }
}
