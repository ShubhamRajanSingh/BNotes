package com.example.bnotes;

import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class PointCollector implements View.OnTouchListener {
    public static final int NUM_POINTS=4;
    private static List<Point> points=new ArrayList<Point>();
    private PointCollectorListener listener;

    public void setListener(PointCollectorListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        int x=Math.round(event.getX());
        int y=Math.round(event.getY());
        String message=String.format("Coordinates:( %d, %d)",x,y);
        Log.d(MainActivity.DEBUGTAG,message);

        points.add(new Point(x,y));

        if(points.size()==4){
            if(listener!=null){
                Log.d(MainActivity.DEBUGTAG,"Reset2");
                listener.pointCollected(points);

            }
        }

        return false;
    }

    public static void clear(){
       points.clear();
    }
}
