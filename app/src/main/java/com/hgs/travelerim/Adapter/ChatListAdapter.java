package com.hgs.travelerim.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.hgs.travelerim.R;
import com.travelerim.data.ChatEntity;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Administrator on 2016/3/21.
 */
public class ChatListAdapter extends BaseAdapter {
    private Context context;
    private List<String> msgList;
    private List<Integer> messageType;
    private String time;
    private int sendOrReceive = -1;

    class ViewHolder {
        TextView textViewSend;
        TextView textViewReceive;
    }

    public ChatListAdapter(Context context, List<String> msgList, List<Integer> type) {
        this.context = context;
        this.msgList = msgList;
        this.messageType = type;
    }

    @Override
    public int getCount() {
        return msgList.size();
    }

    @Override
    public Object getItem(int position) {
        return msgList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

      /*  ViewHolder holder;
        String msg = msgList.get(position);
        int type = messageType.get(position);

        switch (type) {
            case ChatEntity.SEND:
                holder = new ViewHolder();//做了稍微的优化。作为static类来重用其TextView
                if (sendOrReceive != ChatEntity.SEND) {

                    convertView = LayoutInflater.from(context).inflate(R.layout.chat_message_out_layout, null);
                    holder.textViewSend = (TextView) convertView.findViewById(R.id.chat_my_message);
                    sendOrReceive = ChatEntity.SEND;
                    convertView.setTag(holder);
                    Log.i("tag:", "haveTag");
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                holder.textViewSend.setText(msg);
                break;
            case ChatEntity.RECEIVE:
                holder = new ViewHolder();//做了稍微的优化。作为static类来重用其TextView
                if (sendOrReceive != ChatEntity.RECEIVE) {

                    convertView = LayoutInflater.from(context).inflate(R.layout.chat_message_in_layout, null);
                    holder.textViewReceive = (TextView) convertView.findViewById(R.id.chat_friend_message);
                    sendOrReceive = ChatEntity.RECEIVE;
                    convertView.setTag(holder);
                    Log.i("tag:", "haveTag2");
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                //chatReceive(convertView, msg);
                holder.textViewReceive.setText(msg);
                break;
            case ChatEntity.TIME:
                convertView = LayoutInflater.from(context).inflate(R.layout.chat_time_layout, null);
                break;
        }*/

        String msg = msgList.get(position);
        int type = messageType.get(position);
        switch (type) {
            case ChatEntity.SEND:
                convertView = LayoutInflater.from(context).inflate(R.layout.chat_message_out_layout, null);
                TextView textView = (TextView) convertView.findViewById(R.id.chat_my_message);
                textView.setText(msg);
                break;
            case ChatEntity.RECEIVE:
                convertView = LayoutInflater.from(context).inflate(R.layout.chat_message_in_layout, null);
                TextView textView2 = (TextView) convertView.findViewById(R.id.chat_friend_message);
                textView2.setText(msg);
                break;
            case ChatEntity.TIME:
                convertView = LayoutInflater.from(context).inflate(R.layout.chat_time_layout, null);
                break;
        }

        return convertView;
    }


    //为发送消息
    private void chatSend(View convertView, String msg) {

        EditText et = (EditText) convertView.findViewById(R.id.chat_my_message);
        et.setText(msg);
    }


    //为接收消息
    private void chatReceive(View convertView, String msg) {
        EditText et = (EditText) convertView.findViewById(R.id.chat_friend_message);
        et.setText(msg);
    }
}
 /* RelativeLayout rightMsgLayout, leftMsgLayout;//左 右消息的布局
        ViewStub timeLayout;
        TextView time,myName,friName;
        EditText myMsg, friMsg;

        convertView = LayoutInflater.from(context).inflate(R.layout.chat_message, null);
        timeLayout = (ViewStub) convertView.findViewById(R.id.chat_time_layout);
        rightMsgLayout = (RelativeLayout) convertView.findViewById(R.id.chat_my_layout);
        leftMsgLayout = (RelativeLayout) convertView.findViewById(R.id.chat_friend_layout);
        time = (TextView) convertView.findViewById(R.id.chat_time);
        ChatEntity chatEntity = chatEntityList.get(position);//聊天对象
        myMsg = (EditText) convertView.findViewById(R.id.chat_my_message);//消息内容
        friMsg = (EditText) convertView.findViewById(R.id.chat_friend_message);
        friName = (TextView)convertView.findViewById(R.id.chat_friend_name2);//名字
        myName = (TextView)convertView.findViewById(R.id.chat_my_name);
        //时间条
        String msgTime = chatEntity.getSendTime().trim();

        if (msgTime.length() > 1) {//当有日期时
            timeLayout.setVisibility(View.VISIBLE);//可见性设为可见
          //  time.setText("" + msgTime);
        }

        String msgContext = chatEntity.getContent().trim();
        int msgType = chatEntity.getMessageType();
        if (msgType == ChatEntity.RECEIVE) {//当为接收时,即为好友发来消息是，用左消息布局
            leftMsgLayout.setVisibility(View.VISIBLE);
            rightMsgLayout.setVisibility(View.GONE);
            friMsg.setText(msgContext);
            friName.setText(friendName);
        } else {
            leftMsgLayout.setVisibility(View.GONE);
            rightMsgLayout.setVisibility(View.VISIBLE);
            myMsg.setText(msgContext);
            myName.setText(userName);
        }*/
