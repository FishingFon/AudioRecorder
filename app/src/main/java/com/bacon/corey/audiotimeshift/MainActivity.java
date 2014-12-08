package com.bacon.corey.audiotimeshift;

import android.animation.ObjectAnimator;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;
import java.io.File;


public class MainActivity extends ActionBarActivity implements PlayFragment.OnFragmentInteractionListener {
    //ViewPagerAdapter vAdapter;
    //ViewPager vPager;
    //FloatingActionButton fab;

    SlidingUpPanelLayout slidingUpPanelLayout;
    ColorDrawable actionBarBackground;
    FrameLayout dimShadowDrop;
    FloatingActionsMenu fabMenu;
    ActionBarDrawerToggle actionBarDrawerToggle;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    FilesListFragment fileListFragment;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fileListFragment = new FilesListFragment();

        replaceFragment(fileListFragment, R.id.mainLayoutContainer, false);


        // Action bar setup
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.draw_open, R.string.draw_closed);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        getToolbarDimShadowDrop().getForeground().setAlpha(0);
        //toolbar.setSubtitle(R.string.test);
        //toolbar.setSubtitleTextAppearance(this, R.style.Test);
        toolbar.setTitleTextAppearance(this, R.style.TitleTheme);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        actionBarDrawerToggle.syncState();
        toolbar.setSubtitleTextAppearance(this, R.style.SubTitleRecordingTheme);

        fabMenu = (FloatingActionsMenu)findViewById(R.id.fabMenu);
        dimShadowDrop = (FrameLayout) findViewById(R.id.recordingListMainLayout);
        dimShadowDrop.getForeground().setAlpha(0);
        actionBarBackground = new ColorDrawable();
        actionBarBackground.setColor(getResources().getColor(R.color.c52));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            toolbar.setBackground(actionBarBackground);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(darkenColorRGB(getResources().getColor(R.color.c52)));
            }
        }
        else {
            toolbar.setBackgroundDrawable(actionBarBackground);
        }
        //getActionBar().setBackgroundDrawable(actionBarBackground);

        //if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            //getActionBar().setElevation(8);
        //}

        slidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        //vAdapter = new ViewPagerAdapter(getSupportFragmentManager(), ITEMS, this);
       // vPager = (ViewPager) findViewById(R.id.viewPager);
        //vPager.setAdapter(vAdapter);



        // Consume touch event for panel so that touch does not get transfered to child views.
        FrameLayout mainContainer = (FrameLayout) findViewById(R.id.slideUpPanel);
        mainContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        /*vPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Log.v("page sele cted", "page selected");
                sendMessage();
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if(position >= vAdapter.getCount()-1){
                    return;
                }

                ColorFragment from =  vAdapter.getItem(position);
                ColorFragment to =  vAdapter.getItem(position + 1);
                final int blended = blendColors(to.getColor(), from.getColor(), positionOffset);
                actionBarBackground.setColor(blended);

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setStatusBarColor(darkenColorRGB(blended));
                }


            }

        });*/
    }

    @Override
    protected void onResume() {
        super.onResume();

        //final FrameLayout mainLayout =(FrameLayout) findViewById(R.id.recordingListMainLayout);
        //mainLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
           // @Override
            //public void onGlobalLayout() {
                //int heightPX = mainLayout.getHeight();

               // DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                //int heightOffsetPX = (int) ((80*displayMetrics.density)+0.5);

                //FrameLayout fragmentContainer = (FrameLayout) findViewById(R.id.playFragmentContainer);
                //fragmentContainer.getLayoutParams().height = heightPX-heightOffsetPX;

              //  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                //    mainLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
               // }
                  //  else{
                   //     mainLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                  //  }
            //    }
      //  });
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
8*/
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
    public void replaceFragment(Fragment fragment, int id, boolean addToBackStack){
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(id, fragment);

        if(addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }
    public ColorDrawable getActionBarDrawable(){
        return actionBarBackground;
    }
    public FrameLayout getDimShadowDrop(){
        return dimShadowDrop;
    }
    public FrameLayout getToolbarDimShadowDrop(){
        FrameLayout toolbarShadow = (FrameLayout)findViewById(R.id.toolbarShadow);
        return toolbarShadow;
    }
    public Toolbar getToolbar(){
        return toolbar;
    }

    @Override
    public void onBackPressed() {
        if (slidingUpPanelLayout.isPanelExpanded()){
            slidingUpPanelLayout.collapsePanel();
        }
        else if(fabMenu.isExpanded()){
            fabMenu.collapse();
            fileListFragment.hideFabMaintext();

            ObjectAnimator anim =  ObjectAnimator.ofInt(getDimShadowDrop().getForeground(), "alpha", 180, 0);
            anim.setDuration(200);
            anim.start();
        }
        else if(!slidingUpPanelLayout.isPanelExpanded() && !fabMenu.isExpanded()) {
            super.onBackPressed();

        }

    }
    public ActionBarDrawerToggle getActionBarDrawerToggle(){
        return actionBarDrawerToggle;
    }
    public void setDrawIndicatiorEnabled(boolean enabled){
        actionBarDrawerToggle.setDrawerIndicatorEnabled(enabled);

    }

}
