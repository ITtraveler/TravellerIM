package com.hgs.travelerim.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hgs.travelerim.R;
import com.travelerim.data.RecentChat;

import java.util.List;

/**
 * Created by Administrator on 2016/4/12.
 */
public class CurChatListAdapter extends BaseAdapter {
    private Context context;
    private List<RecentChat> list;

    public CurChatListAdapter(Context context, List<RecentChat> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String name, content, time, count;
        TextView tv_name;
        TextView tv_content;
        TextView tv_time;
        TextView tv_count;
        convertView = LayoutInflater.from(context).inflate(R.layout.recent_msg_list_layout, null);
        tv_name = (TextView) convertView.findViewById(R.id.recent_msg_name);
        tv_content = (TextView) convertView.findViewById(R.id.recent_msg_content);
        tv_time = (TextView) convertView.findViewById(R.id.recent_msg_time);
        tv_count = (TextView) convertView.findViewById(R.id.recent_msg_count);
        name = list.get(position).getName();
        content = list.get(position).getContent();
        time = list.get(position).getTime();
        count = list.get(position).getMsgCount();
        tv_name.setText(name);
        tv_content.setText(content);
        tv_time.setText(time);
        if (count.equals("0")) {
            tv_count.setVisibility(View.GONE);
        } else {
            tv_count.setVisibility(View.VISIBLE);
            tv_count.setText(count);
        }
        return convertView;
    }
}
