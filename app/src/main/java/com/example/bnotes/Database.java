package com.example.bnotes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;


public class Database extends SQLiteOpenHelper {
    private static final String Points_Table="POINTS";
    private static final String col_id="ID";
    private static final String col_x="X";
    private static final String col_y="Y";
    public Database(Context context) {
        super(context,"note.db",null,1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql=String.format("create table %s(%s INTEGER PRIMARY KEY,%s INTEGER NOT NULL,%s INTEGER NOT NULL)",Points_Table,col_id,col_x,col_y);
        db.execSQL(sql);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void storePoints(List<Point> points){
        SQLiteDatabase db=getWritableDatabase();

        db.delete(Points_Table,null,null);
        int i=0;
        for(Point point:points){
            ContentValues values=new ContentValues();
            values.put(col_id,i);
            values.put(col_x,point.x);
            values.put(col_y,point.y);
            db.insert(Points_Table,null,values);
            i++;
        }

        db.close();
    }

    public List<Point> getPoint(){
        List<Point> points=new ArrayList<Point>();
        SQLiteDatabase db=getReadableDatabase();
        String sql=String.format("SELECT %s,%s FROM %s ORDER BY %s",col_x,col_y,Points_Table,col_id);
        Cursor cursor= db.rawQuery(sql,null);
        while(cursor.moveToNext()){
            int x=cursor.getInt(0);
            int y=cursor.getInt(1);

            points.add(new Point(x,y));
        }

        db.close();

        return points;
    }
}
