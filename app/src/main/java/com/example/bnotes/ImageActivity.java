package com.example.bnotes;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;

import static android.graphics.BitmapFactory.decodeResource;


public class ImageActivity extends Activity implements PointCollectorListener {

    private static final String PASSWORD_SET = "PASSWORD_SET";
    private static final String Index = "index";
    private static final String IMAGE_SET = "Image_SET";
    private static final int POINT_CLOSENESS = 40;
    private PointCollector pointCollector = new PointCollector();
    private Database db = new Database(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        addTouchListener();

        SharedPreferences prefImage = getPreferences(MODE_PRIVATE);

        Boolean resetimage=prefImage.getBoolean(IMAGE_SET,false);

           if(!resetimage){
               setDefaultImage();
    //           SharedPreferences.Editor editor = prefImage.edit();
    //           editor.putBoolean(IMAGE_SET,true);
    //           editor.commit();
           }
		   else{
			   ImageDatabase dbi = new ImageDatabase(this);
			  String str= dbi.getImage(1);
			  Uri ImageUri=Uri.parse(str);
			   ImageView imageView=findViewById(R.id.touchImage);
			   imageView.setImageURI(ImageUri);
			   SharedPreferences.Editor editor = prefImage.edit();
			   editor.putBoolean(IMAGE_SET,true);
			   editor.commit();
		   }





        Bundle extras=getIntent().getExtras();
        if(extras!=null){
            Boolean resetPassword=extras.getBoolean(MainActivity.ResetPasspoints);
            if(resetPassword){
                pointCollector.setListener(this);
                showSetPasspointsPrompt();
            }
            Boolean resetImg=extras.getBoolean(MainActivity.ResetImage);
            if(resetImg){
//                   storemImage();
                getGallaryImage();
                SharedPreferences.Editor editor = prefImage.edit();
                editor.putBoolean(IMAGE_SET,true);
                editor.commit();
                showSetPasspointsPrompt();

            }
            Boolean resetDefImg=extras.getBoolean(MainActivity.SetDefault);
            if(resetDefImg){
                Bitmap bit;
                bit= decodeResource(getResources(),R.mipmap.mainimage);
                ImageView imageView=findViewById(R.id.touchImage);
                imageView.setImageBitmap(bit);
                SharedPreferences.Editor editor = prefImage.edit();
                editor.putBoolean(IMAGE_SET,false);
                editor.commit();
                showSetPasspointsPrompt();


            }

        }

        pointCollector.setListener(this);
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        Boolean passpointsSet = prefs.getBoolean(PASSWORD_SET, false);

        if (!passpointsSet) {
            showSetPasspointsPrompt();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if( requestCode==1){
            ImageView imageView=findViewById(R.id.touchImage);
            String[] column={MediaStore.Images.Media.DATA};
            Uri imageUri=data.getData();
           Cursor cursor= getContentResolver().query(imageUri,column,null,null,null);
           cursor.moveToFirst();

           int columnIndex=cursor.getColumnIndex(column[0]);
           String str=cursor.getString(columnIndex);
           cursor.close();
           Uri ImageUri;
           ImageUri=Uri.parse(str);
           imageView.setImageURI(ImageUri);


        }

    }

    public void getGallaryImage(){
                Intent i=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                i.setType("image/*");
                startActivityForResult(i,1);
    }

    private void showSetPasspointsPrompt() {
        AlertDialog.Builder builder = new Builder(this);
        builder.setPositiveButton("OK", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

            }
        });
        builder.setTitle("Create your passpoint sequence");
        builder.setMessage("Touch four points to set pass sequence");
        AlertDialog dlg = builder.create();
        dlg.show();
    }
    private void showRestartAppPrompt() {
        AlertDialog.Builder builder = new Builder(this);
        builder.setPositiveButton("OK", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

            }
        });
        builder.setTitle("Restart");
        builder.setMessage("Restart the App to apply changes");
        AlertDialog dlg = builder.create();
        dlg.show();
    }

    private void addTouchListener() {
        ImageView image = (ImageView) findViewById(R.id.touchImage);
        image.setOnTouchListener(pointCollector);
    }



    private void savePasspoints(final List<Point> points) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("storing...");

        final AlertDialog dlg = builder.create();
        dlg.show();

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                db.storePoints(points);
                Log.d(MainActivity.DEBUGTAG, "Points saved: " + points.size());
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {

                SharedPreferences prefs = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(PASSWORD_SET, true);
                editor.commit();

                pointCollector.clear();
                dlg.dismiss();
            }

        };
        task.execute();

    }

    private void verifyPasspoints(final List<Point> touchedPoints) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Checking passpoints ...");
        final AlertDialog dlg = builder.create();
        dlg.show();

        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {

                List<Point> savedPoints = db.getPoint();

                Log.d(MainActivity.DEBUGTAG,
                        "Loaded points: " + savedPoints.size());

                if (savedPoints.size() != PointCollector.NUM_POINTS
                        || touchedPoints.size() != PointCollector.NUM_POINTS) {

                    return false;
                }


                for(int i=0; i < PointCollector.NUM_POINTS; i++) {
                    Point savedPoint = savedPoints.get(i);
                    Point touchedPoint = touchedPoints.get(i);

                    int xDiff = savedPoint.x - touchedPoint.x;
                    int yDiff = savedPoint.y - touchedPoint.y;

                    int distSquared = xDiff*xDiff + yDiff*yDiff;

                    Log.d(MainActivity.DEBUGTAG, "Distance squared: " + distSquared);

                    if(distSquared > POINT_CLOSENESS*POINT_CLOSENESS) {
                        return false;
                    }
                }


                return true;
            }

            @Override
            protected void onPostExecute(Boolean pass) {
                dlg.dismiss();
                pointCollector.clear();

                if (pass == true) {
                    Intent i = new Intent(ImageActivity.this,
                            MainActivity.class);
                    startActivity(i);
                } else {
                    Toast.makeText(ImageActivity.this, "Access Denied",
                            Toast.LENGTH_LONG).show();
                }
            }

        };

        task.execute();
    }

    @Override
    public void pointCollected(final List<Point> points) {

        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        Boolean passpointsSet = prefs.getBoolean(PASSWORD_SET, false);


        Bundle extras=getIntent().getExtras();
        if(extras!=null){
            Boolean resetPassword=extras.getBoolean(MainActivity.ResetPasspoints);
            if(resetPassword){
              passpointsSet=false;
              showRestartAppPrompt();
            }
            Boolean resetImg=extras.getBoolean(MainActivity.ResetImage);
            if(resetImg){


                passpointsSet=false;
                showRestartAppPrompt();

            }
            Boolean resetDefImg=extras.getBoolean(MainActivity.SetDefault);
            if(resetDefImg){
                passpointsSet=false;
                showRestartAppPrompt();
            }
        }

        if (!passpointsSet) {
            Log.d(MainActivity.DEBUGTAG, "Saving passpoints...");
            savePasspoints(points);
        } else {
            Log.d(MainActivity.DEBUGTAG, "Verifying passpoints...");
            verifyPasspoints(points);
        }

    }

    public void setDefaultImage(){
      
        Bitmap bit;
        bit= decodeResource(getResources(),R.mipmap.mainimage);
        ImageView imageView=findViewById(R.id.touchImage);
	   imageView.setImageBitmap(bit);
	   

    }
//    public void storemImage(){
//        ImageDatabase db=new ImageDatabase(this);
//        Bitmap bit;
//        bit= decodeResource(getResources(),R.mipmap.replaceimage);
//        db.insertImg(1,bit);
//
//    }
//    public void getImage(int i){
//
//        ImageView imageView=findViewById(R.id.touchImage);
//        ImageDatabase db=new ImageDatabase(this);
//        Bitmap bit;
//        bit=db.getImage(i);
//        imageView.setImageBitmap(bit);
//    }

}
