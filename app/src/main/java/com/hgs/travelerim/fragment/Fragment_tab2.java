package com.hgs.travelerim.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.hgs.travelerim.Adapter.FlatListAdapter;
import com.hgs.travelerim.ChatActivity;
import com.hgs.travelerim.MainActivity;
import com.hgs.travelerim.R;
import com.hgs.travelerim.SearchFriActivity;
import com.travelerim.data.ListInfo;
import com.travelerim.data.TravelerDate;
import com.travelerim.database.DatabaseUtil;
import com.travelerim.database.SQL;
import com.travelerim.tool.ProxyXMPPUtil;
import com.travelerim.tool.TravelerUtil;
import com.travelerim.tool.XMPPUtil;


/**
 * Created by Administrator on 2016/3/17.
 */
public class Fragment_tab2 extends Fragment implements OnChildClickListener, View.OnClickListener {
    private ExpandableListView expandableListView;
    private static ListInfo<String> listInfo = new ListInfo<String>();
    private DatabaseUtil databaseUtil;

    //private ProxyXMPPUtil proxyXMPPUtil;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseUtil = new DatabaseUtil(this.getContext());
        //proxyXMPPUtil = new ProxyXMPPUtil();
        listInfo = XMPPUtil.getListInfo();
        Log.i("fragment2:", "fragment2_create");
    }

    /**
     * onCreateView 在周期中是多次调用的，时不时 刷新
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_frient_layout, null);
        expandableListView = (ExpandableListView) view.findViewById(R.id.fragment_expandableList_friend);
        expandableListView.setAdapter(new FlatListAdapter(this.getActivity(), listInfo.getGroupList(), listInfo.getChildList()));
        expandableListView.setOnChildClickListener(this);
        //top
        EditText et_search = (EditText) view.findViewById(R.id.fragment_top_search);
        et_search.setOnClickListener(this);
        Log.i("fragment2:", "OnCreateView");
        return view;
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        Intent intent = new Intent(this.getActivity(), ChatActivity.class);
        String friendName = listInfo.getChildList().get(groupPosition).get(childPosition);
        intent.putExtra("friendName", friendName);//所点击的用户名传入 chatActivity
        startActivity(intent);
//        databaseUtil.createOrOpenDB(TravelerDate.DBNAME);
//        databaseUtil.update(SQL.SQL_DEL_REPEAT("recent_chat"));
//        String sql = "insert into recent_chat(JID,name,msgCount) values('" + friendName + "_chat','" + friendName + "',0)";
//        databaseUtil.insert(sql);
//        databaseUtil.closeDatabase();
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_top_search:
                Intent intent = new Intent(getActivity(), SearchFriActivity.class);
                startActivity(intent);
                break;
        }
    }

}
