package com.bacon.corey.audiotimeshift;


import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
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
    RecordingListAdapter rAdapter;
    int mNum;

        /**
         * Create a new instance of CountingFragment, providing "num"
         * as an argument.
         */
        static FilesListFragment newInstance(int num) {
            FilesListFragment f = new FilesListFragment();

            // Supply num input as an argument.
            Bundle args = new Bundle();
            args.putInt("num", num);
            f.setArguments(args);

            return f;
        }

        /**
         * When creating, retrieve this instance's number from its arguments.
         */
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mNum = getArguments() != null ? getArguments().getInt("num") : 1;

            getFiles(Environment.getExternalStorageDirectory() + File.separator + Constants.DIRECTORY);
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_files, container, false);
            ListView rListView = (ListView) v.findViewById(R.id.recordingListView);
            rAdapter = new RecordingListAdapter(getActivity(), R.layout.row_layout, recordingsList);
            rListView.setAdapter(rAdapter);
            return v;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

        }

    public void getFiles(String directoryName){
        File directory = new File(directoryName);

        File[] fileList = directory.listFiles();
        for(File file: fileList){
            if(file.isFile()){
                //files.add(file);
                recordingsList.add(new Recording(file, new Date(file.lastModified())));

            }
            else if(file.isDirectory()){
                //listf(file.getAbsoluteFile(), false);
            }
        }
    }
    }

