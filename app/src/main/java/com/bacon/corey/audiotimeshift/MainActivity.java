package com.bacon.corey.audiotimeshift;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();}
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

    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            return rootView;
        }
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

        Intent intent = new Intent(this, Record.class);
        startService(intent);
    }
    public void stop(View view){
        Toast.makeText(this, "stop", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, Record.class);
        stopService(intent);
    }





}
