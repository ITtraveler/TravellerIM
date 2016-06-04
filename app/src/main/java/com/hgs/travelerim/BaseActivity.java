package com.hgs.travelerim;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/3/14.
 */
public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void finishActivity() {
        this.finish();
    }

    public void exitApp() {
        System.exit(0);
    }

    public void myToast(String content) {
        LayoutInflater inflater = this.getWindow().getLayoutInflater();
        Toast toast = new Toast(BaseActivity.this);
        View view = inflater.inflate(R.layout.toast_layout, null);
        TextView textView = (TextView) view.findViewById(R.id.toast_content);
        textView.setText(content);
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }


}
