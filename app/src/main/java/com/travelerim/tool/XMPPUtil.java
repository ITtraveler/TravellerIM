package com.travelerim.tool;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.travelerim.data.ListInfo;
import com.travelerim.data.TravelerDate;
import com.travelerim.data.User;
import com.travelerim.data.UserInfo;
import com.travelerim.xmpp.MyProcessMessage;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;

import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/3/14.
 */
public class XMPPUtil {
    private static ConnectionConfiguration config;
    private static XMPPConnection xmppConnection = null;
    private static String exception = "";
    private static AccountManager accountManager;
    private static int reConnectionCount = 0;//记录重新连接的次数

    //配合下面属性设置之setReconnectionAllowed(true)的使用，否则起不来作用。作用：用于重连

    static {
        try {
            Class.forName("org.jivesoftware.smack.ReconnectionManager");
        } catch (Exception e) {
            System.out.println("ReconnectionM");
            e.printStackTrace();
        }
    }

    /**
     * 连接服务器
     *
     * @param server
     * @param port
     * @return
     */
    public static XMPPConnection getXMPPConnection(String server, int port) {
        try {

            config = new ConnectionConfiguration(server, port);
            config.setReconnectionAllowed(true);
            config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
            config.setSendPresence(true);
            SASLAuthentication.supportSASLMechanism("PLAIN", 0);//验证方式
            xmppConnection = new XMPPConnection(config);
            if (!xmppConnection.isConnected()) {
                xmppConnection.connect();
            }
            reConnectionCount = 0;//当重新连接成功时，重连次数置0
            return xmppConnection;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("服务器连接失败。");
            exception = "连接服务器失败!";
            if (reConnectionCount < 10) {//重连次数大于十次不再重连
                xmppConnection = getXMPPConnection(TravelerDate.HOST, TravelerDate.PORT);
                login(TravelerDate.USERNAME, TravelerDate.PASSWORD);
                reConnectionCount++;
            }
        }
        return null;
    }

    public static XMPPConnection getXMPPConnection(String server) {
        return getXMPPConnection(server, TravelerDate.PORT);
        //toXMPPConnection(server, TravelerDate.PORT);
    }

    /**
     * 获取当前的连接
     *
     * @return
     */
    public static XMPPConnection getXMPPConnection() {
        return xmppConnection;
    }

    /**
     * 是否连接服务器
     *
     * @return
     */

    public static boolean isXMPPConnection() {
        // XMPPConnection conn = getXMPPConnection(server);
        if (xmppConnection != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 创建账户
     *
     * @param server
     * @param userInfo
     */
    public static boolean createAccount(String server, UserInfo userInfo) {
        boolean isOk;
        getXMPPConnection(server);
        AccountManager accountManager = xmppConnection.getAccountManager();
        String email = userInfo.getEmail();
        if (email == null) {
            try {
                accountManager.createAccount(userInfo.getUserName(), userInfo.getPsw());
                isOk = true;
            } catch (Exception e) {
                isOk = false;
                e.printStackTrace();
                System.out.println("创建失败");
                exception = "账号创建失败！";
            }
        } else {
            try {
                isOk = true;
                Map<String, String> map = new HashMap<>();
                map.put("email", email);
                accountManager.createAccount(userInfo.getUserName(), userInfo.getPsw(), map);
            } catch (Exception e) {
                isOk = false;
                e.printStackTrace();
                System.out.println("创建失败");
                exception = "账号创建失败！";
            }
        }
        return isOk;
    }

    /**
     * 登入
     *
     * @param server
     * @param user
     * @return
     */
    public static boolean login(String server, User user) {

        try {
            getXMPPConnection(TravelerDate.HOST);//连接服务器
            xmppConnection.login(user.getUsername(), user.getPassword());
            // connection.disconnect(new Presence(Presence.Type.unavailable));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    /**
     * 用于重新登入使用
     *
     * @param userName
     * @param passWord
     */
    private static void login(String userName, String passWord) {
        try {
            xmppConnection.login(userName, passWord);
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }

    /**
     * 退出登入，是账号处于离线状态
     */
    public static void exitLogin() {
        // xmppConnection.sendPacket(new Presence(Presence.Type.unavailable));//设为离线状态
        xmppConnection.disconnect(new Presence(Presence.Type.unavailable));//断开连接
        //toXMPPConnection(TravelerDate.HOST);
    }


    public static String getException() {
        return exception;
    }

    private static void getAccountManager() {
        accountManager = xmppConnection.getAccountManager();
    }

    /**
     * 获取花名册，封装于ListInfo中
     * 注意getUser是得到好友的jid(即有加后缀@hgs)，而 getName是得到好友的名称
     * 为了统一化，都用getName，当是使用jid时只要在name@hgs
     *
     * @return
     */
    public static ListInfo<String> getListInfo() {
        List<String> groupList = new ArrayList<>();
        List<List<String>> childList = new ArrayList<>();
        //所有的好友，包括未分组好友
        groupList.add("A   Friend");
        if (!isXMPPConnection()) {
            Log.i("XMPPUtil：", "unConnected");
            getXMPPConnection(TravelerDate.HOST);
        } else {
            Collection<RosterEntry> ungrouped = xmppConnection.getRoster().getEntries();
            List<String> friend = new ArrayList<>();
            for (RosterEntry rosterEntry : ungrouped)
                friend.add(rosterEntry.getName());
            childList.add(friend);
            //已分组的好友
            Collection<RosterGroup> groups = xmppConnection.getRoster().getGroups();
            for (RosterGroup rg : groups) {
                groupList.add(rg.getName());
                List<String> friends = new ArrayList<>();
                Collection<RosterEntry> listName = rg.getEntries();
                for (RosterEntry re : listName) {
                    friends.add(re.getName());
                    System.out.println(re.getType() + "   status:" + re.getStatus());

                }
                childList.add(friends);
            }
        }

        return new ListInfo<>(groupList, childList);
    }


    public static void message(final Context context, final User user) {
        xmppConnection.getChatManager().addChatListener(new ChatManagerListener() {
            @Override
            public void chatCreated(Chat chat, boolean b) {
                chat.addMessageListener(new MyProcessMessage(context, user));
            }
        });
    }

    public void process() {
        PacketFilter filter = new AndFilter(new PacketTypeFilter(Presence.class));
        PacketListener listener = new PacketListener() {
            @Override
            public void processPacket(Packet packet) {
                System.out.println("pack" + packet.toXML());
                Log.i("PresenceService:", packet.toXML());
            }
        };
        xmppConnection.addPacketListener(listener, filter);
    }

/*
    //
    public static void createChat(String userJID) {
        Handler handler = new Handler();

        // List<String> messageList = new ArrayList<>();//用来存放message
        Chat chat = xmppConnection.getChatManager().createChat(userJID, new MessageListener() {//监听jid这位好友发来的信息
            @Override
            public void processMessage(Chat chat, Message message) {
                if (message.getType() == Message.Type.chat) {//详情API Message类
                    String s = message.getBody();
                }
            }
        });
    }*/
}
