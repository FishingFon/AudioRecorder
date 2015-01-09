package com.bacon.corey.audiotimeshift;


import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FilesListFragment extends com.bacon.corey.audiotimeshift.ColorFragment {
    public static int FRAGMENT_COLOR;
    List<com.bacon.corey.audiotimeshift.Recording> recordingsList = new ArrayList<com.bacon.corey.audiotimeshift.Recording>();
    RecordingListAdapter rAdapter;
    int mNum;
    ColorDrawable actionBarBackground;
    FileObserver fObserver;
    ListView listView;
    com.bacon.corey.audiotimeshift.MainActivity mainActivity;
    boolean fabExpanded;
    com.bacon.corey.audiotimeshift.SlidingUpPanelLayout slidingUpPanelLayout;
    int defaultColor;
    int actionBarColor;
    int color;
    boolean fabMainTextVisible;
    boolean toolbarOverLayed;
    AlphaAnimation mainTextAlphaAnimFadeIn;
    AlphaAnimation mainTextAlphaAnimFadeOut;
    int positionClicked;
    View v;
    FrameLayout fabMainText;
    int mColor;
    boolean expanding = true;
    boolean takeOneTaken = false;
    float takeOne;
    float takeTwo;
    boolean appTitleShowing = true;
    float slideOffsetDifference;
    String appTitle;
    String expandedTitle;
    String expandedSubtitle;
    boolean firstRun = true;
    Toolbar toolbar;
    com.bacon.corey.audiotimeshift.PlayFragment mPlayFragment;
    boolean statePlaying;
    boolean toolbarDrawerToggleVisible = true;
    // Methods
    public static FilesListFragment newInstance(int num, Context context) {

        FRAGMENT_COLOR = context.getResources().getColor(R.color.c52);

        FilesListFragment f = new FilesListFragment();
        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (com.bacon.corey.audiotimeshift.MainActivity)activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mNum = getArguments() != null ? getArguments().getInt("num") : 1;
        positionClicked = -1;
        recordingsList = com.bacon.corey.audiotimeshift.RecordingOptionsCalculator.getFiles(Environment.getExternalStorageDirectory() + File.separator + com.bacon.corey.audiotimeshift.Constants.DIRECTORY);
        //new UpdateDataSet().execute();
        setHasOptionsMenu(true);
        //getFiles(Environment.getExternalStorageDirectory() + File.separator + Constants.DIRECTORY);
        defaultColor = -1;
        fabExpanded = false;
        toolbarOverLayed = false;
        fabMainTextVisible = true;
        fObserver = new FileObserver(Environment.getExternalStorageDirectory() + File.separator + com.bacon.corey.audiotimeshift.Constants.DIRECTORY) {
            @Override
            public void onEvent(int event, String path) {
                if(event == FileObserver.CREATE || event == FileObserver.DELETE){
                    //getFiles(Environment.getExternalStorageDirectory() + File.separator + Constants.DIRECTORY);
                    recordingsList = com.bacon.corey.audiotimeshift.RecordingOptionsCalculator.getFiles(Environment.getExternalStorageDirectory() + File.separator + com.bacon.corey.audiotimeshift.Constants.DIRECTORY);

                }
            }
        };
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver, new IntentFilter(com.bacon.corey.audiotimeshift.Constants.UPDATE_FILE_DATASET));

        super.onCreate(savedInstanceState);

    }



    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("action");
            Log.v("broadcast receiver ", "message: " + message);
            //getFiles(Environment.getExternalStorageDirectory() + File.separator + Constants.DIRECTORY);
            recordingsList = com.bacon.corey.audiotimeshift.RecordingOptionsCalculator.getFiles(Environment.getExternalStorageDirectory() + File.separator + com.bacon.corey.audiotimeshift.Constants.DIRECTORY);


        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_files, container, false);


        if(savedInstanceState != null) {

        }

        actionBarBackground = mainActivity.getActionBarDrawable();
        actionBarColor = getResources().getColor(R.color.c52);
        fabMainText = (FrameLayout) getActivity().findViewById(R.id.fabMainText);
        final com.bacon.corey.audiotimeshift.FloatingActionsMenu fabMenu = (com.bacon.corey.audiotimeshift.FloatingActionsMenu)getActivity().findViewById(R.id.fabMenu);
        final com.bacon.corey.audiotimeshift.FloatingActionButton fabMain = (com.bacon.corey.audiotimeshift.FloatingActionButton)getActivity().findViewById(R.id.fab_expand_menu_button);

        if(!fabMenu.isExpanded()){
            fabMainText.setVisibility(View.GONE);
        }
        mainTextAlphaAnimFadeIn = new AlphaAnimation(0f, 1f);
        mainTextAlphaAnimFadeOut = new AlphaAnimation(1f, 0f);
        mainTextAlphaAnimFadeIn.setDuration(200);
        mainTextAlphaAnimFadeOut.setDuration(200);

        //fab = mainActivity.getFab();
        slidingUpPanelLayout = (com.bacon.corey.audiotimeshift.SlidingUpPanelLayout) getActivity().findViewById(R.id.sliding_layout);
        listView = (ListView) v.findViewById(R.id.recordingListView);
        rAdapter = new com.bacon.corey.audiotimeshift.RecordingListAdapter(getActivity(), R.layout.row_layout, recordingsList);
        listView.setAdapter(rAdapter);
        rAdapter.notifyDataSetChanged();
        // Add onclick events to fab menu buttons

        fabMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Handler handler = new Handler();

                (new Thread(){
                    @Override
                    public void run(){

                        handler.post(new Runnable(){
                            public void run() {
                                if(!fabMenu.isExpanded()) {
                                    fabMainText.setVisibility(View.VISIBLE);
                                    fabMainText.startAnimation(mainTextAlphaAnimFadeIn);
                                }

                                fabExpanded = fabMenu.toggle();

                                mainActivity.getDimShadowDrop().setForeground(getResources().getDrawable(R.drawable.dim_shadow_shape_light));
                                mainActivity.getDimShadowDrop().getForeground().setAlpha(0);
                                ObjectAnimator anim1 =  ObjectAnimator.ofInt(mainActivity.getDimShadowDrop().getForeground(), "alpha", 0, 180);

                                if(!toolbarOverLayed) {
                                    ObjectAnimator anim2 = ObjectAnimator.ofInt(mainActivity.getToolbarDimShadowDrop().getForeground(), "alpha", 0, 180);
                                    anim2.setDuration(200);
                                    anim2.start();
                                    toolbarOverLayed = true;
                                }


                                anim1.setDuration(200);
                                anim1.start();

                                //mainActivity.getDimShadowDrop().getForeground().setAlpha(180);
                                if (fabExpanded){
                                    ((com.bacon.corey.audiotimeshift.MainActivity)getActivity()).replaceFragment(new com.bacon.corey.audiotimeshift.RecordFragment(), R.id.slideUpPanel, false);
                                    //slidingUpPanelLayout.expandPanel();
                                    defaultColor = getResources().getColor(R.color.recordDefaultColor);


                                }
                            }
                        });
                    }
                }).start();



            }
        });

        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final MotionEvent e = event;

                if(fabMenu.isExpanded() && e.getAction() == MotionEvent.ACTION_UP) {
                    hideFabMaintext();
                    fabExpanded = false;
                    fabMenu.collapse();

                }
                else if(!fabMenu.isExpanded()){
                    return false;
                }





                //mainActivity.getDimShadowDrop().getForeground().setAlpha(0);

                return true;
            }
        });
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final int pos = position;
                final Handler handler = new Handler();

                (new Thread(){
                    @Override
                    public void run(){

                        handler.post(new Runnable(){
                            public void run() {
                                positionClicked = pos;
                                Bundle bundle = new Bundle();
                                //bundle.putSerializable("recording", rAdapter.getItem(position));

                                bundle.putInt("color", rAdapter.getRowColor(pos));
                                bundle.putInt("playItemPosition", pos);
                                mPlayFragment = new com.bacon.corey.audiotimeshift.PlayFragment(){
                                    @Override
                                    public void onResume() {
                                        final Handler handler = new Handler();

                                        (new Thread(){
                                            @Override
                                            public void run(){
                                                try{ sleep(100); }
                                                catch(InterruptedException e){}
                                                handler.post(new Runnable(){
                                                    public void run() {
                                                        slidingUpPanelLayout.expandPanel();

                                                    }
                                                });

                                            }
                                        }).start();
                                        super.onResume();

                                    }
                                };
                                mPlayFragment.setArguments(bundle);
                                ((com.bacon.corey.audiotimeshift.MainActivity)getActivity()).replaceFragment(mPlayFragment, R.id.slideUpPanel, false);


                                color = rAdapter.getRowColor(pos);


                            }
                        });
                    }
                }).start();


            }
        });
        slidingUpPanelLayout.setPanelSlideListener(new com.bacon.corey.audiotimeshift.SlidingUpPanelLayout.PanelSlideListener() {
            boolean isPanelSliding = false;
            @Override
            public boolean isPanelSliding() {
                return isPanelSliding;
            }

            @Override
            public void onPanelCollapsed(View panel) {

                final Handler handler = new Handler();

                (new Thread(){
                    @Override
                    public void run(){
                        if(isPanelSliding) isPanelSliding = false;
                        handler.post(new Runnable(){
                            public void run() {
                                defaultColor = -1;
                                mainActivity.getDimShadowDrop().getForeground().setAlpha(0);
                                firstRun = true;
                                if(statePlaying){
                                    mPlayFragment.releaseMediaPlayer();
                                    statePlaying = false;

                                }
                            }
                        });
                    }
                }).start();

            }

            @Override
            public void onPanelSlide(View panel, float slideOffset) {

                final float sOffset = slideOffset;
                final Handler handler = new Handler();

                (new Thread(){
                    @Override
                    public void run(){

                        handler.post(new Runnable(){
                            public void run() {
                                if(!takeOneTaken) {
                                    takeOne = sOffset;
                                    takeOneTaken = true;
                                    if(!isPanelSliding) isPanelSliding = true;
                                }
                                else if(takeOneTaken){
                                    takeTwo = sOffset;
                                    takeOneTaken = false;
                                    if(takeOne < takeTwo){
                                        expanding = true;
                                        slideOffsetDifference = takeTwo - takeOne;

                                    }
                                    else{
                                        expanding = false;
                                        slideOffsetDifference = takeOne - takeTwo;
                                    }
                                }
                                try{
                                    if(firstRun){
                                        if(expanding) {
                                            mainActivity.getDimShadowDrop().setForeground(getResources().getDrawable(R.drawable.dim_shadow_shape_dark));
                                        }
                                        appTitle = getResources().getString(R.string.app_name);
                                        if(recordingsList.get(positionClicked).getTitleString() != null && positionClicked != -1){
                                            expandedTitle = recordingsList.get(positionClicked).getTitleString();
                                            statePlaying = true;
                                        }
                                        else{
                                            expandedTitle = "Recording";
                                            statePlaying = false;

                                        }
                                        if(recordingsList.get(positionClicked).getDateString() != null && positionClicked != -1){
                                            expandedSubtitle = recordingsList.get(positionClicked).getDateString();
                                        }
                                        else{
                                            expandedSubtitle = "";
                                        }
                                        firstRun = false;
                                    }
                                }catch (IndexOutOfBoundsException e){
                                    expandedTitle = "Recording";
                                    expandedSubtitle = "";
                                    appTitle = getResources().getString(R.string.app_name);
                                    statePlaying = false;


                                }


                                fabMenu.collapse();
                                toolbar = mainActivity.getToolbar();
                                if(toolbarOverLayed){
                                    ObjectAnimator anim2 =  ObjectAnimator.ofInt(mainActivity.getToolbarDimShadowDrop().getForeground(), "alpha", 180, 0);
                                    anim2.setDuration(200);
                                    anim2.start();
                                    toolbarOverLayed = false;
                                }
                                if(defaultColor != -1){
                                    mColor = defaultColor;
                                }
                                else {
                                    mColor = color;
                                }
                                final int midwayBlended = blendColors(mColor, actionBarColor, 0.5f);
                                final int blended = blendColors(mColor, actionBarColor, sOffset);


                                if(sOffset < 0.5f){
                                    if (!toolbarDrawerToggleVisible){
                                        mainActivity.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
                                        toolbarDrawerToggleVisible = true;
                                    }
                                    toolbar.setTitleTextColor(blendColors(midwayBlended, getResources().getColor(R.color.white), sOffset * 2));
                                    toolbar.getNavigationIcon().setAlpha(Math.round(255 - sOffset * 2 * 255));
                                    toolbar.getNavigationIcon().invalidateSelf();

                                    if(!appTitleShowing && !expanding){
                                        toolbar.setTitle(appTitle);
                                        toolbar.setSubtitle("");
                                        toolbar.setTitleTextAppearance(getActivity(), R.style.TitleTheme);
                                        appTitleShowing = true;
                                    }


                                }
                                else if(sOffset >= 0.5f){
                                    if (toolbarDrawerToggleVisible){
                                        mainActivity.getActionBarDrawerToggle().setDrawerIndicatorEnabled(false);
                                        toolbarDrawerToggleVisible = false;
                                    }
                                    if(appTitleShowing && expanding){
                                        toolbar.setTitle(expandedTitle);
                                        toolbar.setSubtitle(expandedSubtitle);
                                        toolbar.setTitleTextAppearance(getActivity(), R.style.TitleRecordingTheme);
                                        positionClicked = -1;
                                        appTitleShowing = false;
                                    }

                                    toolbar.setTitleTextColor(blendColors(getResources().getColor(R.color.white), midwayBlended, (sOffset - 0.5f)*2));
                                    toolbar.setSubtitleTextColor(blendColors(getResources().getColor(R.color.white), midwayBlended, (sOffset - 0.5f)*2));
                                }

                                mainActivity.getDimShadowDrop().getForeground().setAlpha(Math.round(sOffset * 140));
                                actionBarBackground.setColor(blended);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    getActivity().getWindow().setStatusBarColor(com.bacon.corey.audiotimeshift.MainActivity.darkenColorRGB(blended));

                                }
                                if(statePlaying){
                                    mPlayFragment.setVolume(sOffset );

                                }
                            }
                        });
                    }
                }).start();

            }


            @Override
            public void onPanelExpanded(View panel) {

                final Handler handler = new Handler();

                (new Thread(){
                    @Override
                    public void run(){
                        if(isPanelSliding) isPanelSliding = false;
                        handler.post(new Runnable(){
                            public void run() {
                                //fab.hide(true);
                                fabMainText.setVisibility(View.GONE);
                                //toolbar.getNavigationIcon().setVisible(false, false);
                                if(statePlaying){
                                    mPlayFragment.setVolume(1f);

                                }
                            }
                        });
                    }
                }).start();

            }

            @Override
            public void onPanelAnchored(View panel) {
                if(isPanelSliding) isPanelSliding = false;
            }

            @Override
            public void onPanelHidden(View panel) {
                if(isPanelSliding) isPanelSliding = false;

                mainActivity.getDimShadowDrop().getForeground().setAlpha(0);

            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                //fab.listenTo(view);
            }
        });
        return v;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }



    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        // TODO add saved instance state to save application color.
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putInt("color", mColor);
        outState.putString("title", expandedTitle);
        outState.putString("subtitle", expandedSubtitle);
        outState.putInt("positionClicked", positionClicked);
        super.onSaveInstanceState(outState);
    }

    @Override
    public int getColor() {
        return FRAGMENT_COLOR;
    }
    public static int blendColors(int to, int from, float ratio) {
        // TODO put onto separate thread.
        final int f = to;
        final int t = from;
        final float rat = ratio;

        final float inverseRation = 1f - rat;
        final float r = Color.red(f) * rat + Color.red(t) * inverseRation;
        final float g = Color.green(f) * rat + Color.green(t) * inverseRation;
        final float b = Color.blue(f) * rat + Color.blue(t) * inverseRation;

        return Color.rgb((int) r, (int) g, (int) b);
    }
    /*
        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            super.onCreateOptionsMenu(menu, inflater);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            switch (id){
                case R.id.record_options:
                    mainActivity.replaceFragment(new RecordOptionsFragment(), R.id.slideUpPanel, false);
                    defaultColor = Color.GRAY;
                    slidingUpPanelLayout.expandPanel();
                    return true;
                case R.id.action_settings:
                    Intent intent = new Intent(getActivity(), SettingsActivity.class);
                    startActivity(intent);
                default: return super.onOptionsItemSelected(item);

            }
        }
    */
    public void toggleFabMenu(){
        final FrameLayout fabMainText = (FrameLayout) v.findViewById(R.id.fabMainText);
        fabMainText.setVisibility(View.GONE);
    }
    public void hideFabMaintext(){
        ObjectAnimator anim = ObjectAnimator.ofInt(mainActivity.getDimShadowDrop().getForeground(), "alpha", 180, 0);
        ObjectAnimator anim2 =  ObjectAnimator.ofInt(mainActivity.getToolbarDimShadowDrop().getForeground(), "alpha", 180, 0);

        anim.setDuration(200);
        anim.start();
        anim2.setDuration(200);
        anim2.start();
        toolbarOverLayed = false;
        fabMainText.startAnimation(mainTextAlphaAnimFadeOut);
        fabMainText.setVisibility(View.GONE);
    }
    public void setActionBarColorRef(int color){
        defaultColor = color;
    }

}