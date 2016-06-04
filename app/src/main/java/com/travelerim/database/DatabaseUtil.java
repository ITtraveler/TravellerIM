package com.travelerim.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/4/2.
 */
public class DatabaseUtil {
    private Context context;
    private SQLiteDatabase db;

    public DatabaseUtil(Context context) {
        this.context = context;

    }

    public void createOrOpenDB(String dbName) {
        String filePath = context.getFilesDir().getAbsolutePath();
        File file = new File(filePath + "/database");
        if (!file.exists()) {
            file.mkdir();
        }
        db = context.openOrCreateDatabase(file.getAbsolutePath() + "/" + dbName + ".db3", Context.MODE_PRIVATE, null);
    }

    public void createTable(String SQL) {
        if (db.isOpen()) {
            db.beginTransaction();
            db.execSQL(SQL);
            db.setTransactionSuccessful();
            db.endTransaction();
        }
    }

    /**
     * @param SQL
     * @param position 数据的第几个字段
     * @return
     */
    public String[] query(String SQL, int position) {
        List<Object> list = new ArrayList<>();
        if (db.isOpen()) {
            db.beginTransaction();
            Cursor cursor = db.rawQuery(SQL, null);
            while (cursor.moveToNext()) {
                //遍历出表名
                String name = cursor.getString(position);
                list.add(name);
            }
            String[] tableName = new String[list.size()];
            list.toArray(tableName);
            cursor.close();
            db.setTransactionSuccessful();
            db.endTransaction();
            return tableName;
        }
        return null;
    }

    public String[][] query(String sql) {
        db.beginTransaction();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null) {
            int row = cursor.getCount();
            int column = cursor.getColumnCount();
            String result[][] = new String[row][column];
            int count = 0;
            while (cursor.moveToNext()) {
                for (int i = 0; i < column; i++) {
                    String s = cursor.getString(i);
                    result[count][i] = s;
                   // System.out.println(s);
                }
                count++;
            }
            cursor.close();
            db.setTransactionSuccessful();
            db.endTransaction();
            return result;
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        return null;
    }

    public void insert(String insertSQL) {
        if (db.isOpen()) {
            db.beginTransaction();
            db.execSQL(insertSQL);
            db.setTransactionSuccessful();
            db.endTransaction();
        }
    }

    public void delect(String delectSQL) {
        if (db.isOpen()) {
            db.beginTransaction();
            db.execSQL(delectSQL);
            db.setTransactionSuccessful();
            db.endTransaction();
        }
    }

    public void update(String updateSQL) {
        if (db.isOpen()) {
            db.beginTransaction();
            db.execSQL(updateSQL);
            db.setTransactionSuccessful();
            db.endTransaction();
        }
    }

    public boolean tableIsExist(String tableName) {
        boolean isExist = false;
        String sql = "select count(*) as c from sqlite_master where type ='table' and name ='" + tableName.trim() + "'";
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            int count = cursor.getInt(0);
            if (count > 0) {
                return true;
            }
        }
        cursor.close();
        return isExist;
    }

    public void closeDatabase() {
        db.close();
    }


}
