package com.bacon.corey.audiotimeshift;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FilesListFragment extends Fragment{
    List<Recording> recordingsList = new ArrayList<Recording>();
    List<File> files = new ArrayList<File>();
    RecordingListAdapter rAdapter;
    int mNum;
    FileObserver fObserver;
    UpdateDataSet updateDataSet;

    // Methods
    static FilesListFragment newInstance(int num) {
        FilesListFragment f = new FilesListFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
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
        ListView rListView = (ListView) v.findViewById(R.id.recordingListView);
        rAdapter = new RecordingListAdapter(getActivity(), R.layout.row_layout, recordingsList);
        rListView.setAdapter(rAdapter);
        rAdapter.notifyDataSetChanged();
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onDestroy() {
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

        public void getFiles(String directoryName){
            File directory = new File(directoryName);
            Log.v("FilesListFragment", "getFiles();");
            File[] fileList = directory.listFiles();

            for(File file: fileList){
                if(file.isFile() && !files.contains(file)){
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

}