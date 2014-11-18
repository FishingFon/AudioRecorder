package com.bacon.corey.audiotimeshift;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import libs.SlidingUpPanelLayout;

public class MainActivity extends FragmentActivity implements PlayFragment.OnFragmentInteractionListener {
    static final int ITEMS = 3;
    ViewPagerAdapter vAdapter;
    ViewPager vPager;
    List<File> files = new ArrayList<File>();
    //FloatingActionButton fab;
    Boolean fabSecVisible = false;
    SlidingUpPanelLayout slidingUpPanelLayout;
    ColorDrawable actionBarBackground;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //fabSec.hide(true);
         //fab = (FloatingActionButton)findViewById(R.id.fabtwo);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getActionBar().setElevation(0);
        }
        //fab = (FloatingActionButton) findViewById(R.id.fabtwo);
        //fab.setOnClickListener(new View.OnClickListener() {
            //@Override
            //public void onClick(View v) {
               // Intent recordActivityIntent = new Intent(v.getContext(), RecordActivity.class);
              //  startActivity(recordActivityIntent);


           // }
        //});
        slidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        slidingUpPanelLayout.hidePanel();
        vAdapter = new ViewPagerAdapter(getSupportFragmentManager(), ITEMS, this);
        vPager = (ViewPager) findViewById(R.id.viewPager);
        vPager.setAdapter(vAdapter);


        vPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Log.v("page sele cted", "page selected");
                sendMessage();
            }

            //TODO add action bar color changing...

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if(position >= vAdapter.getCount()-1){
                    return;
                }
                actionBarBackground = new ColorDrawable();
                getActionBar().setBackgroundDrawable(actionBarBackground);
                ColorFragment from = (ColorFragment) vAdapter.getItem(position);
                ColorFragment to = (ColorFragment) vAdapter.getItem(position + 1);
                final int blended = blendColors(to.getColor(), from.getColor(), positionOffset);
                actionBarBackground.setColor(blended);
                //fab.mColorNormal = blended;
                //fab.mColorNormal = blended;
                //fab.updateBackground();
                //fab.setColor(blended);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setStatusBarColor(darkenColorRGB(blended));
                }


            }

        });
    }
    public static int blendColors(int from, int to, float ratio) {
        final float inverseRation = 1f - ratio;
        final float r = Color.red(from) * ratio + Color.red(to) * inverseRation;
        final float g = Color.green(from) * ratio + Color.green(to) * inverseRation;
        final float b = Color.blue(from) * ratio + Color.blue(to) * inverseRation;

        return Color.rgb((int) r, (int) g, (int) b);
    }
    @Override
    protected void onResume() {
        super.onResume();

        final FrameLayout mainLayout =(FrameLayout) findViewById(R.id.recordingListMainLayout);
        mainLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightPX = mainLayout.getHeight();

                DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                int heightOffsetPX = (int) ((80*displayMetrics.density)+0.5);

                FrameLayout fragmentContainer = (FrameLayout) findViewById(R.id.playFragmentContainer);
                fragmentContainer.getLayoutParams().height = heightPX-heightOffsetPX;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mainLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                    else{
                        mainLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                }
        });
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
    ExtAudioRecorder test;
    public void record(View view){
        Toast.makeText(this, "record", Toast.LENGTH_SHORT).show();
        test = ExtAudioRecorder.getInstanse(false);
        test.setOutputFile(Environment.getExternalStorageDirectory() + File.separator + "TimeShiftRecorder" + File.separator + "testRecording" + ".wav");
        test.prepare();
        test.start();
        Log.v("Record", "record called");

        //Intent intent = new Intent(this, RecordService.class);
        //startService(intent);

    }
    public void stop(View view){
        Toast.makeText(this, "stop", Toast.LENGTH_SHORT).show();
        //Intent intent = new Intent(this, RecordService.class);
        //stopService(intent);
        test.stop();
        test = null;

    }

    public void sendMessage(){
        Log.v("broadcast sender", "message");
        Intent intent = new Intent(Constants.UPDATE_FILE_DATASET);
        intent.putExtra("message","refresh_data_set");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    //public FloatingActionButton getFab() {
        //return fab;
    //}

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public static String darkenColorHex(int color, float percent){
        int num = Integer.parseInt(Integer.toString(color), 16);

        long amt = Math.round(2.55*percent);
        long R = (num >> 16) + amt;
        long G = (num >> 8 & 0x00FF) + amt;
        long B = (num & 0x0000FF) + amt;
        String x =  Long.toString((0x1000000 + (R<255?R<1?0:R:255)*0x10000 + (G<255?G<1?0:G:255)*0x100 + (B<255?B<1?0:B:255)), 16).substring(1);
        return x;
    }
    public static int darkenColorRGB(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color,hsv);
        hsv[2] *= 0.8f;
        return Color.HSVToColor(hsv);
    }


    }
