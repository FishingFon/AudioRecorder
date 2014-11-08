package com.bacon.corey.audiotimeshift;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends FragmentActivity {
    static final int ITEMS = 3;
    ViewPagerAdapter vAdapter;
    ViewPager vPager;
    List<File> files = new ArrayList<File>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vAdapter = new ViewPagerAdapter(getSupportFragmentManager(), ITEMS);
        vPager = (ViewPager) findViewById(R.id.viewPager);
        vPager.setAdapter(vAdapter);

        //getFiles(Environment.getExternalStorageDirectory() + File.separator + Constants.DIRECTORY);




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void play(View view){
        Toast.makeText(this, "play", Toast.LENGTH_SHORT).show();

        Intent playIntent = new Intent(this, PlayService.class);
        playIntent.putExtra("action", Constants.PLAY);
        startService(playIntent);


    }
    public void record(View view){
        Toast.makeText(this, "record", Toast.LENGTH_SHORT).show();

        Log.v("Record", "OnCreate called");

        Intent intent = new Intent(this, RecordService.class);
        startService(intent);
    }
    public void stop(View view){
        Toast.makeText(this, "stop", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, RecordService.class);
        stopService(intent);
    }


}
