package com.bacon.corey.audiotimeshift;


import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FilesListFragment extends ColorFragment{
    public static int FRAGMENT_COLOR;
    List<Recording> recordingsList = new ArrayList<Recording>();
    List<File> files = new ArrayList<File>();
    RecordingListAdapter rAdapter;
    int mNum;
    ColorDrawable actionBarBackground;
    FileObserver fObserver;
    ListView listView;
    FloatingActionsMenu fabMenu;
    MainActivity mainActivity;
    boolean fabExpanded;
    SlidingUpPanelLayout slidingUpPanelLayout;
    int beforeScrollPosition;
    int defaultColor;
    int actionBarColor;
    int color;
    boolean fabMainTextVisible;
    
    AlphaAnimation mainTextAlphaAnimFadeIn;
    AlphaAnimation mainTextAlphaAnimFadeOut;

    View v;


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
        mainActivity = (MainActivity)activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum = getArguments() != null ? getArguments().getInt("num") : 1;
        setHasOptionsMenu(true);
        //getFiles(Environment.getExternalStorageDirectory() + File.separator + Constants.DIRECTORY);
        new UpdateDataSet().execute();
        defaultColor = -1;
        fabExpanded = false;
        fabMainTextVisible = true;
        fObserver = new FileObserver(Environment.getExternalStorageDirectory() + File.separator + Constants.DIRECTORY) {
            @Override
            public void onEvent(int event, String path) {
                if(event == FileObserver.CREATE || event == FileObserver.DELETE){
                    //getFiles(Environment.getExternalStorageDirectory() + File.separator + Constants.DIRECTORY);
                    new UpdateDataSet().execute();

                }
            }
        };
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver, new IntentFilter(Constants.UPDATE_FILE_DATASET));


    }



    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("action");
            Log.v("broadcast receiver ", "message: " + message);
            //getFiles(Environment.getExternalStorageDirectory() + File.separator + Constants.DIRECTORY);
            new UpdateDataSet().execute();


        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_files, container, false);
        actionBarBackground = mainActivity.getActionBarDrawable();
        actionBarColor = getResources().getColor(R.color.c52);
        final FrameLayout fabMainText = (FrameLayout) getActivity().findViewById(R.id.fabMainText);
        final FloatingActionsMenu fabMenu = (FloatingActionsMenu)getActivity().findViewById(R.id.fabMenu);
        final AddFloatingActionButton fabMain = (AddFloatingActionButton)getActivity().findViewById(R.id.fab_expand_menu_button);
        if(!fabMenu.isExpanded()){
            fabMainText.setVisibility(View.GONE);
        }
        mainTextAlphaAnimFadeIn = new AlphaAnimation(0f, 1f);
        mainTextAlphaAnimFadeOut = new AlphaAnimation(1f, 0f);
        mainTextAlphaAnimFadeIn.setDuration(200);
        mainTextAlphaAnimFadeOut.setDuration(200);

        //fab = mainActivity.getFab();
        slidingUpPanelLayout = (SlidingUpPanelLayout) getActivity().findViewById(R.id.sliding_layout);
        listView = (ListView) v.findViewById(R.id.recordingListView);
        rAdapter = new RecordingListAdapter(getActivity(), R.layout.row_layout, recordingsList);
        listView.setAdapter(rAdapter);
        rAdapter.notifyDataSetChanged();
        // Add onclick events to fab menu buttons

        fabMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(!fabMenu.isExpanded()) {
                    fabMainText.setVisibility(View.VISIBLE);
                    fabMainText.startAnimation(mainTextAlphaAnimFadeIn);
                }
                boolean expanded = fabMenu.toggle();
                mainActivity.getDimShadowDrop().setForeground(getResources().getDrawable(R.drawable.dim_shadow_shape_light));
                mainActivity.getDimShadowDrop().getForeground().setAlpha(0);

                ObjectAnimator anim =  ObjectAnimator.ofInt(mainActivity.getDimShadowDrop().getForeground(), "alpha", 0, 180);
                anim.setDuration(200);
                anim.start();
                //mainActivity.getDimShadowDrop().getForeground().setAlpha(180);
                if (expanded){
                    ((MainActivity)getActivity()).replaceFragment(new RecordFragment(), R.id.slideUpPanel, false);
                    slidingUpPanelLayout.expandPanel();
                    defaultColor = getResources().getColor(R.color.recordDefaultColor);


                }


            }
        });

        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(fabMenu.isExpanded()) {
                    ObjectAnimator anim = ObjectAnimator.ofInt(mainActivity.getDimShadowDrop().getForeground(), "alpha", 180, 0);
                    anim.setDuration(200);
                    anim.start();


                }
                if(fabMenu.isExpanded()) {
                    fabMainText.startAnimation(mainTextAlphaAnimFadeOut);
                    fabMainText.setVisibility(View.GONE);

                }
                fabMenu.collapse();

                //mainActivity.getDimShadowDrop().getForeground().setAlpha(0);
                fabExpanded = false;

                return false;
            }
        });
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("recording", rAdapter.getItem(position));
                bundle.putInt("color", rAdapter.getRowColor(position));

                mainActivity.getDimShadowDrop().setForeground(getResources().getDrawable(R.drawable.dim_shadow_shape_dark));

                PlayFragment mPlayFragment = new PlayFragment();

                mPlayFragment.setArguments(bundle);
                ((MainActivity)getActivity()).replaceFragment(mPlayFragment, R.id.slideUpPanel, false);

                color = rAdapter.getRowColor(position);

                slidingUpPanelLayout.expandPanel();
            }
        });
        slidingUpPanelLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            int mColor;
            @Override
            public void onPanelCollapsed(View panel) {
                defaultColor = -1;
                mainActivity.getDimShadowDrop().getForeground().setAlpha(0);


            }

            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                fabMenu.collapse();
                if(defaultColor != -1){
                    mColor = defaultColor;
                }
                else {
                    mColor = color;
                }
                final int blended = blendColors(mColor, actionBarColor, slideOffset);
                mainActivity.getDimShadowDrop().getForeground().setAlpha(Math.round(slideOffset * 140));
                actionBarBackground.setColor(blended);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getActivity().getWindow().setStatusBarColor(MainActivity.darkenColorRGB(blended));

                }

            }


            @Override
            public void onPanelExpanded(View panel) {
                //fab.hide(true);
                    fabMainText.setVisibility(View.GONE);


            }

            @Override
            public void onPanelAnchored(View panel) {

            }

            @Override
            public void onPanelHidden(View panel) {
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
    public void onDestroyView() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }


    public class UpdateDataSet extends AsyncTask<List, String, List> {

        @Override
        protected List doInBackground(List... params) {
            getFiles(Environment.getExternalStorageDirectory() + File.separator + Constants.DIRECTORY);
            return null;

        }

        @Override
        protected void onPostExecute(List recordingList) {
            super.onPostExecute(recordingList);
            rAdapter.notifyDataSetChanged();

        }

        public void getFiles(String directoryName) {
            File directory = new File(directoryName);
            Log.v("FilesListFragment", "getFiles();");
            if (directory.exists()) {
                File[] fileList = directory.listFiles();

                    for (File file : fileList) {
                        if (file.isFile() && !files.contains(file)) {
                            files.add(file);
                        }
                /*
                else if(file.isDirectory()){
                    files(file.getAbsoluteFile(), false);
                }
                */
                    }
                    updateRecordingsList();

            }
        }

        public void updateRecordingsList(){

            for(int i = 0; i < files.size(); i++) {
                boolean fileFound = false;
                for (int j = 0; j < recordingsList.size(); j++) {
                    if (recordingsList.get(j).getFile().equals(files.get(i))) {
                        fileFound = true;
                    }
                }
                if(fileFound != true){
                    recordingsList.add(new Recording(files.get(i), new Date(files.get(i).lastModified())));
                }
            }
            if(rAdapter != null) {
                Log.i("FileListFragment", "notifyDataSetChanged()");
            }
            //Collections.sort(recordingsList);


        }
    }


    @Override
    public int getColor() {
        return FRAGMENT_COLOR;
    }
    public static int blendColors(int from, int to, float ratio) {
        int f = from;
        int t = to;

        final float inverseRation = 1f - ratio;
        final float r = Color.red(from) * ratio + Color.red(to) * inverseRation;
        final float g = Color.green(from) * ratio + Color.green(to) * inverseRation;
        final float b = Color.blue(from) * ratio + Color.blue(to) * inverseRation;

        return Color.rgb((int) r, (int) g, (int) b);
    }
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

    public void toggleFabMenu(){
        final FrameLayout fabMainText = (FrameLayout) v.findViewById(R.id.fabMainText);
        fabMainText.setVisibility(View.GONE);
    }
}