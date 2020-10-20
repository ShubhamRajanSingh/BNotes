package com.example.bnotes;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.util.Log;


public class ImageDatabase extends SQLiteOpenHelper {

    private static final String Table_name="Images";
    private static final String  Id="id";
    private static final String col="imageUri";
    SQLiteDatabase dab=getWritableDatabase();
//    private static final String stat="Insert into Images(id,image) values(?,?)";


    public ImageDatabase(Context context) {
        super(context,"image.db",null,1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql=String.format("create table %s(%s INTEGER PRIMARY KEY,%s String NOT NULL)",Table_name,Id,col);
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void insertImg(int id , String img ) {

//        SQLiteStatement insertStatement_logo=dab.compileStatement(stat);
//        SQLiteDatabase db=getWritableDatabase();
        try {
        String sql=String.format("DELETE FROM %s WHERE %s=%d",Table_name,Id,1);
        dab.execSQL(sql);
        }catch(Exception e){
           Log.d(MainActivity.DEBUGTAG,"Not deleted");
        }

//        insertStatement_logo.bindLong(1, id);
//        insertStatement_logo.bindBlob(2, data);

//        insertStatement_logo.execute();
        String insertSql=String.format("Insert into %s (%s,%s) values(%d,%s)",Table_name,Id,col,1,img);
        dab.execSQL(insertSql);
        Log.d("Images","Saved Images");

    }

//    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
//        return outputStream.toByteArray();
//    }
    public String getImage(int i){
        SQLiteDatabase db=getReadableDatabase();
        String qu = String.format("select %s,%s  from %s where %s=%d" ,Id,col,Table_name,Id,i) ;
        Cursor cur = db.rawQuery(qu, null);

        if (cur.moveToFirst()){

            String imgStringUri = cur.getString(1);
            cur.close();
            return imgStringUri;
        }
        if (cur != null && !cur.isClosed()) {
            cur.close();
        }

        return null;
    }

}
