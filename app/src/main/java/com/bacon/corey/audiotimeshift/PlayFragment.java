package com.bacon.corey.audiotimeshift;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ScaleDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.DropBoxManager;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.github.mikephil.charting.*;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.XLabels;

import libs.CircleButton;

public class PlayFragment extends Fragment {
    Bundle bundle;
    com.bacon.corey.audiotimeshift.Recording recording;
    Boolean clicked = new Boolean(false);
    int recPosition;
    Intent playIntent;
    private OnFragmentInteractionListener mListener;
    boolean mBound;
    int currentFilePosition;
    ColorDrawable actionBarBackground;
    com.bacon.corey.audiotimeshift.MainActivity mainActivity;
    com.bacon.corey.audiotimeshift.PlayService playService;
    Toolbar toolbar;
    List <com.bacon.corey.audiotimeshift.Recording> recordingsList = new ArrayList<com.bacon.corey.audiotimeshift.Recording>();
    boolean playButtonShowing = false;
    CircleButton playButton;
    CircleButton backButton;
    CircleButton forwardButton;
    CircleButton deleteButton;
    CircleButton waveToggleButton;
    SeekBar seekbar;
    int currentColor;
    TextView currentPlaybackPosition;
    TextView maxPlaybackLength;
    boolean seekbarProgresssChangeInProgress = false;
    boolean seekbarIsItTouch = false;
    int lastTime = -1;
    AlphaAnimation end;
    AlphaAnimation start;
    LineChart mLineChart;

    // TODO: Rename and change types and number of parameters
    public static PlayFragment newInstance() {
        PlayFragment fragment = new PlayFragment();
        return fragment;
    }

    public PlayFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        updateSeekProgress(); // TODO

        start = new AlphaAnimation(1.0f, 0.5f);
        start.setDuration(100);

        end = new AlphaAnimation(0.5f, 1.0f);
        end.setDuration(100);

        bundle = getArguments();
        recordingsList = com.bacon.corey.audiotimeshift.RecordingOptionsCalculator.getExistingFiles(Environment.getExternalStorageDirectory() + File.separator + com.bacon.corey.audiotimeshift.Constants.DIRECTORY);
        if(bundle != null) {
            recPosition = bundle.getInt("playItemPosition");
            currentFilePosition = recPosition;
            //recording = (Recording) bundle.getSerializable("recording");
            recording = recordingsList.get(recPosition);

        }
        toolbar = mainActivity.getToolbar();

    }

    @Override
    public void onStart() {
        super.onStart();

        playIntent = new Intent(getActivity(), com.bacon.corey.audiotimeshift.PlayService.class);
        playIntent.putExtra("action", com.bacon.corey.audiotimeshift.Constants.PLAY);
        playIntent.putExtra("playItemPosition", recPosition);
        playIntent.putExtra("playItem", recording);
        getActivity().bindService(playIntent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            getActivity().unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //TODO add color settings to buttons
        View view = inflater.inflate(R.layout.fragment_play, container, false);
        playButton = (CircleButton) view.findViewById(R.id.playButton);
        backButton = (CircleButton) view.findViewById(R.id.previousButton);
        forwardButton = (CircleButton) view.findViewById(R.id.nextButton);
        deleteButton = (CircleButton) view.findViewById(R.id.deleteButton);
        waveToggleButton = (CircleButton) view.findViewById(R.id.waveToggleButton);
        seekbar = (SeekBar) view.findViewById(R.id.playFragmentSeekbar);
        currentPlaybackPosition = (TextView) view.findViewById(R.id.currentPlaybackTime);
        maxPlaybackLength = (TextView) view.findViewById(R.id.maxPlaybackLength);
        mLineChart = (LineChart) view.findViewById(R.id.lineChart);


        final int color = bundle.getInt("color");
        actionBarBackground = mainActivity.getActionBarDrawable();


        seekbar.setMax((int) recording.getAudioLengthInSeconds());
        maxPlaybackLength.setText(recording.getAudioLengthInHMSFormat());

        if(bundle != null) {
            playButton.setDefaultColor(color);
        }
        backButton.getDrawable().setColorFilter(color, PorterDuff.Mode.MULTIPLY); // TODO
        forwardButton.getDrawable().setColorFilter(color, PorterDuff.Mode.MULTIPLY); // TODO
        waveToggleButton.getDrawable().setColorFilter(color, PorterDuff.Mode.MULTIPLY); // TODO
        deleteButton.getDrawable().setColorFilter(color, PorterDuff.Mode.MULTIPLY); // TODO
        setSeekbarColour(color);

        waveToggleButton.setTag(clicked);
        playButton.setTag(clicked);

        buildAudioWaveData(recording);
        //buildChartData();
        //TODO add delete function
/*
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecordingOptionsCalculator.deleteFile(recording.getFile());
                recordingsList.remove(currentFilePosition);
                mainActivity.getListAdapter().notifyDataSetChanged();
                if(currentFilePosition + 1 < recordingsList.size()) {
                    playNextFile(currentFilePosition++);
                }
                else{
                    mainActivity.getSlidingUpPanelLayout().collapsePanel();
                }
            }
        });
*/
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(seekbarIsItTouch) {
                    seekbarProgresssChangeInProgress = true;
                    currentPlaybackPosition.setText(getAudioLengthInHMSFormat(progress));
                    mLineChart.centerViewPort(seekBar.getProgress(), 1f);

                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekbarIsItTouch = true;
                if (!playService.getMediaPlayer().isPlaying()) {
                    playService.getMediaPlayer().start();

                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekbarIsItTouch = false;
                seekbarProgresssChangeInProgress = false;
                playService.getMediaPlayer().seekTo(seekBar.getProgress() * 1000);
                //seekUpdation();

            }
        });

        waveToggleButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                start.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }
                   @Override
                    public void onAnimationEnd(Animation animation) {
                        if(!((Boolean) waveToggleButton.getTag())) {
                            waveToggleButton.setImageResource(R.drawable.ic_wave_toggle_two);
                            waveToggleButton.getDrawable().setColorFilter(mainActivity.getListAdapter().getRowColor(currentFilePosition), PorterDuff.Mode.MULTIPLY);
                            waveToggleButton.setTag(new Boolean(true));
                        }
                        else if((Boolean) waveToggleButton.getTag()) {
                            waveToggleButton.setImageResource(R.drawable.ic_wave_toggle_one);
                            waveToggleButton.getDrawable().setColorFilter(mainActivity.getListAdapter().getRowColor(currentFilePosition), PorterDuff.Mode.MULTIPLY);
                            waveToggleButton.setTag(new Boolean(false));
                        }
                        waveToggleButton.startAnimation(end);
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {
                   }
                });
              waveToggleButton.startAnimation(start);

            }
        });


        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    playService.getMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            togglePlayPauseButton(false);
                            if(currentFilePosition >= recordingsList.size() || currentFilePosition == 0){
                                releaseMediaPlayer(); // TODO change and check works
                                togglePlayPauseButton(true);
                            }
                        }
                    });

                    if (!playService.getMediaPlayer().isPlaying()) {
                        startPlayback();
                    } else if (playService.getMediaPlayer().isPlaying()) {
                        pausePlayback();
                    }

                }catch (IllegalStateException e){
                    playService.getMediaPlayer().release();
                }
                if((Boolean)playButton.getTag() == false){
                    togglePlayPauseButton(true);
                }
                else{
                    togglePlayPauseButton(false);
                }


            }
        });

        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBound && (currentFilePosition)+1 < recordingsList.size()) {
                    currentFilePosition++;
                    releaseMediaPlayer();
                    playNextFile(currentFilePosition);


                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBound && (currentFilePosition) -1 > 0) {
                    currentFilePosition--;
                    releaseMediaPlayer();
                    playPreviousFile(currentFilePosition);

                }
            }
        });
        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }



    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }




    public Intent getPlayIntent(){
        return playIntent;

    }

    public ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            com.bacon.corey.audiotimeshift.PlayService.LocalBinder binder = (com.bacon.corey.audiotimeshift.PlayService.LocalBinder) service;
            playService = binder.getService();
            playService.setNewFileForPlayback(recPosition, recording);
            playService.playMediaPlayer();

            mBound = true;
            if(playService.getMediaPlayer().isPlaying()){
                //seekUpdation();

            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    public void setVolume (float volume){
        try {
            playService.getMediaPlayer().setVolume(volume, volume);
        }catch (Exception e){

        }
    }
    public void releaseMediaPlayer (){
        try {
            playService.getMediaPlayer().release();
        }catch (Exception e){

        }
    }
    public void playNextFile(final int position){
        final Handler handler = new Handler();
        final boolean[] threadCompleted = new boolean[1];
        threadCompleted[0] = false;
        (new Thread(){
            @Override
            public void run(){
                //int i = 1;
                float i;
                final int fromColor = mainActivity.getListAdapter().getRowColor(position-1);
                final int toColor = mainActivity.getListAdapter().getRowColor(position);
                final int midWayBlended = mainActivity.blendColors(toColor, fromColor, 0.05f*11);
                for(i = 0.01f; i<1f; i = i + 0.05f){
                    final float j = i;

                    mainActivity.setActionbarColorRef(toColor);

                    handler.post(new Runnable(){
                        public void run(){
                            int blended = mainActivity.blendColors(toColor, fromColor, j);
                            actionBarBackground.setColor(blended);
                            setSeekbarColour(blended);

                            int max = seekbar.getProgress();
                            int a = Math.round(max / (max *j));
                            if(a < max) {
                                seekbar.setProgress(a);
                            }
                            currentPlaybackPosition.setText(getAudioLengthInHMSFormat(a));

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                getActivity().getWindow().setStatusBarColor(com.bacon.corey.audiotimeshift.MainActivity.darkenColorRGB(blended));
                            }
                            if(j < 0.5f){

                                toolbar.setTitleTextColor(mainActivity.blendColors(midWayBlended, Color.WHITE, j*2));
                                toolbar.setSubtitleTextColor(mainActivity.blendColors(midWayBlended, Color.WHITE, j*2));

                            }
                            else if(j > 0.5f && j < 0.56f){
                                toolbar.setTitle(((com.bacon.corey.audiotimeshift.Recording)recordingsList.get(position)).getTitleString());
                                toolbar.setSubtitle(((com.bacon.corey.audiotimeshift.Recording)recordingsList.get(position)).getDateString());

                            }
                            else if(j > 0.55f){
                                toolbar.setTitleTextColor(mainActivity.blendColors(Color.WHITE, midWayBlended, (j  - 0.5f)*2));
                                toolbar.setSubtitleTextColor(mainActivity.blendColors(Color.WHITE, midWayBlended, (j  - 0.5f)*2));
                            }
                            if(bundle != null) {
                                playButton.setDefaultColor(blended);
                            }
                            backButton.getDrawable().setColorFilter(blended, PorterDuff.Mode.MULTIPLY); // TODO
                            forwardButton.getDrawable().setColorFilter(blended, PorterDuff.Mode.MULTIPLY); // TODO
                            waveToggleButton.getDrawable().setColorFilter(blended, PorterDuff.Mode.MULTIPLY); // TODO
                            deleteButton.getDrawable().setColorFilter(blended, PorterDuff.Mode.MULTIPLY); // TODO

                            if(j >= 0.96){
                                seekbar.setProgress(0);
                                if(currentPlaybackPosition.getText()!= "0:00") {
                                    currentPlaybackPosition.setText("0:00");
                                }
                                playService.setNewFileForPlayback(position, recordingsList.get(position));
                                startPlayback();
                            }
                        }
                    });
                    // next will pause the thread for some time
                    try{ sleep(30); }
                    catch(InterruptedException e){ break; }
                }
            }
        }).start();

        seekbar.setMax((int) recordingsList.get(position).getAudioLengthInSeconds());
        maxPlaybackLength.setText(recordingsList.get(position).getAudioLengthInHMSFormat());
        currentColor = mainActivity.getListAdapter().getRowColor(position);
        if(threadCompleted[0]){
            //seekUpdation();

        }
    }

    public void playPreviousFile(final int position){
        final Handler handler = new Handler();
        final int fromColor = mainActivity.getListAdapter().getRowColor(position+1);
        final int toColor = mainActivity.getListAdapter().getRowColor(position);

        (new Thread(){
            @Override
            public void run(){
                //int i = 1;
                float i;

                final int midWayBlended = mainActivity.blendColors(toColor, fromColor, 0.05f*11);
                for(i = 0.01f; i<1f; i = i + 0.05f){
                    final float j = i;

                    mainActivity.setActionbarColorRef(toColor);


                    handler.post(new Runnable(){
                        public void run(){
                            int blended = mainActivity.blendColors(toColor, fromColor, j);
                            actionBarBackground.setColor(blended);
                            setSeekbarColour(blended);

                            int max = seekbar.getProgress();
                            int a = Math.round(max / (max *j));
                            if(a < max) {
                                if(a <= 1){
                                    seekbar.setProgress(0);

                                }
                                seekbar.setProgress(a);

                            }
                            currentPlaybackPosition.setText(getAudioLengthInHMSFormat(a));

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                getActivity().getWindow().setStatusBarColor(com.bacon.corey.audiotimeshift.MainActivity.darkenColorRGB(blended));
                            }
                            if(j < 0.5f){
                                toolbar.setTitleTextColor(mainActivity.blendColors(midWayBlended, Color.WHITE, j*2));
                                toolbar.setSubtitleTextColor(mainActivity.blendColors(midWayBlended, Color.WHITE, j*2));

                            }
                            else if(j > 0.5f && j < 0.56f){
                                toolbar.setTitle(((com.bacon.corey.audiotimeshift.Recording)recordingsList.get(position)).getTitleString());
                                toolbar.setSubtitle(((com.bacon.corey.audiotimeshift.Recording)recordingsList.get(position)).getDateString());

                            }
                            else if(j > 0.55f){
                                toolbar.setTitleTextColor(mainActivity.blendColors(Color.WHITE, midWayBlended, (j  - 0.5f)*2));
                                toolbar.setSubtitleTextColor(mainActivity.blendColors(Color.WHITE, midWayBlended, (j  - 0.5f)*2));
                            }
                            if(bundle != null) {
                                playButton.setDefaultColor(blended);
                            }

                            backButton.getDrawable().setColorFilter(blended, PorterDuff.Mode.MULTIPLY); // TODO
                            forwardButton.getDrawable().setColorFilter(blended, PorterDuff.Mode.MULTIPLY); // TODO
                            waveToggleButton.getDrawable().setColorFilter(blended, PorterDuff.Mode.MULTIPLY); // TODO
                            deleteButton.getDrawable().setColorFilter(blended, PorterDuff.Mode.MULTIPLY); // TODO

                            if(j >= 0.96){
                                seekbar.setProgress(0);
                                if(currentPlaybackPosition.getText()!= "0:00") {
                                    currentPlaybackPosition.setText("0:00");
                                }
                                playService.setNewFileForPlayback(position, recordingsList.get(position));
                                startPlayback();

                            }
                        }
                    });
                    // next will pause the thread for some time
                    try{ sleep(30); }
                    catch(InterruptedException e){ break; }
                }
            }
        }).start();

        seekbar.setMax((int) recordingsList.get(position).getAudioLengthInSeconds());
        maxPlaybackLength.setText(recordingsList.get(position).getAudioLengthInHMSFormat());
        currentColor = mainActivity.getListAdapter().getRowColor(position);


    }
    public void stopPlayback(){
        playService.getMediaPlayer().stop();
        // TODO Dummy thread for copying
/*
        final Handler handler = new Handler();

        (new Thread(){
            @Override
            public void run(){

                handler.post(new Runnable(){
                    public void run() {
                    }
                    });
                }
            }).start();
            */
        }

    public void startPlayback(){
        playService.playMediaPlayer();
        //seekUpdation();
        togglePlayPauseButton(false);


    }
    public void pausePlayback(){
        playService.getMediaPlayer().pause();
        togglePlayPauseButton(true);
    }

    private void setSeekbarColour(int colour){
        LayerDrawable ld = (LayerDrawable)seekbar.getProgressDrawable().getCurrent();
        ScaleDrawable d = (ScaleDrawable) ld.findDrawableByLayerId(android.R.id.progress);
        Drawable thumnb = seekbar.getThumb();
        thumnb.setColorFilter(colour, PorterDuff.Mode.SRC_IN);
        d.setColorFilter(colour, PorterDuff.Mode.SRC_IN);
    }
    public void seekUpdation() {
        //Handler seekHandler = new Handler();
        try {
            if (playService.getMediaPlayer().isPlaying() && !seekbarProgresssChangeInProgress) {
                int length = playService.getMediaPlayer().getCurrentPosition() / 1000;
                if(lastTime != -1){
                    if(length != lastTime){
                        seekbar.setProgress(length);
                        Log.i("PlayFragment", "SeekUpdation called - Time updated");

                    }
                }
                else if (lastTime == -1){
                    seekbar.setProgress(length);

                }
                currentPlaybackPosition.setText(getAudioLengthInHMSFormat(length));
                //seekHandler.postDelayed(run, 1000);
                lastTime = length;
            }
        }catch (IllegalStateException e){

        }
    }

    public String getAudioLengthInHMSFormat(int seconds){

        long timeUnParsed = seconds;
        int hours = 0;
        int mins = 0;
        int secs = 0;
        String hoursString = "";
        String minsString = "";
        String secString = "";

        String formattedString;
        while(timeUnParsed > 0){
            if(timeUnParsed >= 3600){
                while(timeUnParsed >= 3600){
                    hours++;
                    timeUnParsed = timeUnParsed - 3600;
                }
            }
            else if(timeUnParsed >= 60 && timeUnParsed < 3600){
                while(timeUnParsed >= 60){
                    mins++;
                    timeUnParsed = timeUnParsed - 60;
                }
            }
            else if(timeUnParsed < 60){
                secs = secs + (int)timeUnParsed;
                timeUnParsed = 0;
            }
        }
        if(hours != 0){
            hoursString = Integer.toString(hours) + ":";
        }
        minsString = Integer.toString(mins);
        secString = Integer.toString(secs);
        if(secString.length() < 2){
            secString = "0" + secs;
        }


        formattedString = hoursString + minsString + ":" + secString;

        return formattedString;
    }

    public void updateSeekProgress(){
        final Handler handler = new Handler();

        (new Thread(){
            @Override
            public void run() {
                while (true) {
                    handler.post(new Runnable() {
                        public void run() {
                            if(mBound) {
                                try{
                                    if (playService.getMediaPlayer().isPlaying()) {
                                        seekUpdation();

                                    }
                                }catch (IllegalStateException e){

                                }
                            }
                        }
                    });
                    try{ sleep(1000); }
                    catch(InterruptedException e){ }
                }
            }
        }).start();
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        mainActivity = (com.bacon.corey.audiotimeshift.MainActivity)activity;
    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    public void togglePlayPauseButton(boolean setPlay){
        final boolean mSetPlaying = setPlay;
    if(mSetPlaying && (Boolean) playButton.getTag() == false || !mSetPlaying && (Boolean) playButton.getTag() == true) {
        start.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                boolean test = (Boolean) playButton.getTag();
                if (mSetPlaying && (Boolean) playButton.getTag() == false) {
                    playButtonShowing = true;
                    playButton.setImageResource(R.drawable.ic_play);
                    playButton.setTag(new Boolean(true));

                } else if (!mSetPlaying && (Boolean) playButton.getTag() == true) {
                    playButtonShowing = false;
                    playButton.setImageResource(R.drawable.ic_pause);
                    playButton.setTag(new Boolean(false));

                }

                playButton.startAnimation(end);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        playButton.startAnimation(start);
    }
    }
    public void setUpChart(ArrayList <Integer> audioData){
        mLineChart.setDrawYValues(false);
        mLineChart.setDescription("");
        mLineChart.setDrawLegend(false);
        mLineChart.setDrawBorder(false);
        mLineChart.setDrawUnitsInChart(false);
        mLineChart.setNoDataTextDescription("No wave data is available for this audio file.");
        //mLineChart.setDrawXLabels(false);
        mLineChart.setPinchZoom(false);
        mLineChart.setBackgroundColor(Color.WHITE);
        mLineChart.setGridColor(Color.GRAY);
        mLineChart.setDrawVerticalGrid(false);
        mLineChart.setDrawGridBackground(false);
        mLineChart.getYLabels().setLabelCount(0);
        mLineChart.getYLabels().setDrawTopYLabelEntry(false);
        mLineChart.setStartAtZero(false);
        mLineChart.setPadding(0,0,0,0);
        //mLineChart.setDragOffsetX(40f); sets white space at the sides of graph
        //mLineChart.setDoubleTapToZoomEnabled(false);
        mLineChart.setHighlightEnabled(false);

        mLineChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add onclick data here
            }
        });

        //mLineChart.getXLabels().setSpaceBetweenLabels(10);
        mLineChart.invalidate();


        ArrayList<Entry> vals = new ArrayList<Entry>();
        ArrayList<String> xVals = new ArrayList<String>();
/*        Random rand = new Random();

        for(int i = 0; i < 100; i++) {
            vals.add(new Entry((rand.nextInt(10+1)), i));


            xVals.add(Integer.toString(i));

        }
*/
        int i = 0;
        for (int temp: audioData){
            vals.add(new Entry(temp, i));
            xVals.add(Integer.toString(i));
            i++;
        }
        LineDataSet lineDataSet = new LineDataSet(vals, "Random  Numbers");
        lineDataSet.setLineWidth(1.5f);

        lineDataSet.setColor(mainActivity.getListAdapter().getRowColor(currentFilePosition));
        lineDataSet.setDrawCircles(false);
        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(lineDataSet);

        LineData data = new LineData(xVals, dataSets);
        mLineChart.setData(data);
        Log.i("status", "chart set data");


    }
    public ArrayList <Integer> buildAudioWaveData(Recording recording){
        final Recording finalRecording = recording;
        final Handler handler = new Handler();
        (new Thread(){
            @Override
            public void run(){
                File recFile = finalRecording.getFile();
                final ArrayList <Integer> dataSeries = new ArrayList<Integer>();

                try {
                    InputStream bis = new BufferedInputStream(new FileInputStream(recFile));
                    DataInputStream dis = new DataInputStream(bis);

                    long sampleRate = finalRecording.getSampleRate(new RandomAccessFile(recFile, "rw"));
                    int samplesPerDatum = (int)sampleRate; // One sample for every 1000 ms.
                    long fileLengthInBytes = recFile.length();
                    long fileDataRemaining = fileLengthInBytes / 2; // 16 bit wave file = 2 bytes per sample.
                    int max = 0;

                    while(fileDataRemaining > 0){
                        if(fileDataRemaining > samplesPerDatum) {

                                short temp = dis.readShort();
                                //if (temp > max) {
                                //    max = temp;
                                //}


                            dataSeries.add((int)temp);
                            max = 0;
                            dis.skipBytes(samplesPerDatum);
                        }
                        fileDataRemaining -= samplesPerDatum;
                    }
                    Log.i("status", "finished building data");
                    handler.post(new Runnable() {
                        public void run() {
                            setUpChart(dataSeries);

                        }
                    });
                }catch(Exception e){

                }
            }
        }).start();


        return null;
    }

}

