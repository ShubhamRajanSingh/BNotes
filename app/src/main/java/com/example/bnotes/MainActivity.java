package com.example.bnotes;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

import static android.graphics.BitmapFactory.decodeResource;

public class MainActivity extends AppCompatActivity {
    public static final String DEBUGTAG = "SRS";
    public static final String textFile = "NoteSquirrel";
    public static final String fileSaved = "FileSaved";
    public static final String ResetPasspoints = "Reset PassPoints";
    public static final String ResetImage = "Reset Image";
    public static final String SetDefault = "Set Default";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addSaveButtonListener();
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        boolean filesaved = prefs.getBoolean(fileSaved, false);
        if (filesaved) {
            loadSavedFile();
        }
    }

    private void loadSavedFile() {
        try {
            FileInputStream fis = openFileInput(textFile);//reads a file
            BufferedReader reader = new BufferedReader(new InputStreamReader(new DataInputStream(fis)));
            String line;
            EditText editText = (EditText) findViewById(R.id.text);
            while ((line = reader.readLine()) != null) {
                editText.append(line);
                editText.append("\n");

            }

        } catch (Exception e) {
            Toast.makeText(MainActivity.this, getString(R.string.toast_cant_load), Toast.LENGTH_LONG).show();
            Log.d(DEBUGTAG, "Unable to read file");
        }
    }

    private void addSaveButtonListener() {
        Button saveBtn = (Button) findViewById(R.id.Save);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText) findViewById(R.id.text);//getting text typed on phone
                String text = editText.getText().toString();
                try {
                    FileOutputStream fos = openFileOutput(textFile, Context.MODE_PRIVATE);//only application can read
                    fos.write(text.getBytes());
                    fos.close();
                    SharedPreferences prefs = getPreferences(MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean(fileSaved, true);
                    editor.commit();

                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, getString(R.string.toast_cant_save), Toast.LENGTH_LONG).show();
                    Log.d(DEBUGTAG, "Unable to save  file");// print to log

                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_passpoints_reset:
                Intent i = new Intent(this, ImageActivity.class);
                i.putExtra(ResetPasspoints, true);
                startActivity(i);
                return true;
            case R.id.Default_image:
                Intent in = new Intent(this, ImageActivity.class);
                in.putExtra(SetDefault, true);
                startActivity(in);
                return true;

            case R.id.set_Image:
                Intent intent = new Intent(this, ImageActivity.class);
                intent.putExtra(ResetImage, true);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.image, menu);

        return true;
    }

}
