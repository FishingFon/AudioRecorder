package com.bacon.corey.audiotimeshift;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import libs.SlidingUpPanelLayout;

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
    SlidingUpPanelLayout slidingUpPanelLayout;
    int beforeScrollPosition;

    // Methods
    public static FilesListFragment newInstance(int num, Context context) {

        FRAGMENT_COLOR = context.getResources().getColor(R.color.c5);

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
        //getFiles(Environment.getExternalStorageDirectory() + File.separator + Constants.DIRECTORY);
        new UpdateDataSet().execute();

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
        View v = inflater.inflate(R.layout.fragment_files, container, false);


        //fab = mainActivity.getFab();

        listView = (ListView) v.findViewById(R.id.recordingListView);
        rAdapter = new RecordingListAdapter(getActivity(), R.layout.row_layout, recordingsList);
        listView.setAdapter(rAdapter);
        rAdapter.notifyDataSetChanged();
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int color = rAdapter.getRowColor(position);
                int pos = position;
                final int actionBarColor = getColor();
                actionBarBackground = new ColorDrawable();



                getActivity().getActionBar().setBackgroundDrawable(actionBarBackground);
                slidingUpPanelLayout = (SlidingUpPanelLayout) getActivity().findViewById(R.id.sliding_layout);
               // slidingUpPanelLayout.setPanelHeight(0);
                beforeScrollPosition = -listView.getChildAt(0).getTop() + listView.getFirstVisiblePosition() * listView.getChildAt(0).getHeight();

                listView.smoothScrollToPositionFromTop(position, 0, 200);
                slidingUpPanelLayout.showPanel();
                slidingUpPanelLayout.expandPanel();

                slidingUpPanelLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {

                    @Override
                    public void onPanelCollapsed(View panel) {
                        //listView.smoothScrollBy(beforeScrollPosition, 100);
                        //  fab.hide(false);

                    }

                    @Override
                    public void onPanelSlide(View panel, float slideOffset) {
                        final int blended = blendColors(color, actionBarColor, slideOffset);

                        actionBarBackground.setColor(blended);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            getActivity().getWindow().setStatusBarColor(MainActivity.darkenColorRGB(blended));

                        }

                    }


                    @Override
                    public void onPanelExpanded(View panel) {
                        //fab.hide(true);
                    }

                    @Override
                    public void onPanelAnchored(View panel) {

                    }

                    @Override
                    public void onPanelHidden(View panel) {

                    }
                });


                //Intent playIntent = new Intent(getActivity(), PlayService.class);
                //playIntent.putExtra("action", Constants.PLAY);
                //playIntent.putExtra("playItemPosition", position);
                //playIntent.putExtra("playItem", recordingsList.get(position));

                //getActivity().startService(playIntent);
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

    public ListView getListView() {
        return listView;
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
public void hideFabMenu(FloatingActionsMenu fabMenu){
    TranslateAnimation anim = new TranslateAnimation(0, -100, 100, 100);
    anim.setDuration(1000);
    fabMenu.startActionMode((android.view.ActionMode.Callback) anim);
}

}