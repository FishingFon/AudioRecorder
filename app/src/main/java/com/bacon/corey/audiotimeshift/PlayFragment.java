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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.DropBoxManager;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.github.mikephil.charting.*;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.OnChartGestureListener;
import com.github.mikephil.charting.interfaces.OnChartValueSelectedListener;
import com.github.mikephil.charting.interfaces.OnDrawListener;
import com.github.mikephil.charting.listener.BarLineChartTouchListener;
import com.github.mikephil.charting.utils.LimitLine;
import com.github.mikephil.charting.utils.PointD;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.XLabels;
import com.github.mikephil.charting.utils.YLabels;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;


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
    float gLastTime = -1;
    AlphaAnimation end;
    AlphaAnimation start;
    LineChart mLineChart;
    float graphDatums;
    float graphWidth;
    boolean isChartBeingTouched = false;
    int xValsPerSecond = 10;
    int deviceWidthPX;
    boolean updateGraphCenter = true;
    LineDataSet lineDataSet;
    boolean chartAnimationFinished = false;

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
        updateGraphCenter();

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
    public void onResume() {
        super.onResume();


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

        deviceWidthPX = getActivity().getResources().getDisplayMetrics().widthPixels;

        waveToggleButton.setTag(clicked);
        playButton.setTag(clicked);

        buildAudioWaveData(recording);
        //buildChartData();
        //TODO add delete function



        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mLineChart.centerViewPort(0,0);
                Log.i("scaleX", Float.toString(mLineChart.getScaleX()));


                //Toast.makeText(getActivity(),Double.toString(x2.x), Toast.LENGTH_LONG).show();


            //mLineChart.animateYToZero(500);
                /*
                RecordingOptionsCalculator.deleteFile(recording.getFile());
                recordingsList.remove(currentFilePosition);
                mainActivity.getListAdapter().notifyDataSetChanged();
                if(currentFilePosition + 1 < recordingsList.size()) {
                    playNextFile(currentFilePosition++);
                }
                else{
                    mainActivity.getSlidingUpPanelLayout().collapsePanel();
                }

            */
            }
        });

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(seekbarIsItTouch) {
                    seekbarProgresssChangeInProgress = true;
                    currentPlaybackPosition.setText(getAudioLengthInHMSFormat(progress));
                    int i = seekBar.getProgress();
                    mLineChart.centerViewPort(seekBar.getProgress() * xValsPerSecond, 1f);


                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekbarIsItTouch = true;
                updateGraphCenter = false;
                if (!playService.getMediaPlayer().isPlaying()) {
                    playService.getMediaPlayer().start();

                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekbarIsItTouch = false;
                updateGraphCenter = true;
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
        final ObjectAnimator mObjectAnimator = mLineChart.getAnimateYToZeroAnimator(300);
        //buildAudioWaveDataAsynTask(recordingsList.get(position));

        setChartAnimationFinished(false);

        mObjectAnimator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                setChartAnimationFinished(false);
                //updateGraphCenter = false;

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //lineDataSet.setColor(mainActivity.getListAdapter().getRowColor(position));
                setChartAnimationFinished(true);
                //mLineChart.centerViewPort(0,0);
                buildAudioWaveDataAsynTask(recordingsList.get(position));


                ///updateGraphCenter = true;

            }

            @Override
            public void onAnimationCancel(Animator animation) {
                setChartAnimationFinished(true);

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mObjectAnimator.start();

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
        //mLineChart.animateYToZero(600);

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



    public void updateSeekProgress() {
        final Handler handler = new Handler();

        (new Thread(){
            @Override
            public void run() {
                while (true) {
                    try {
                        if (mBound) {
                            if (playService.getMediaPlayer().isPlaying() && !seekbarProgresssChangeInProgress) {
                                final int currentPosition = playService.getMediaPlayer().getCurrentPosition() / 1000;

                                handler.post(new Runnable() {
                                    public void run() {
                                        try {
                                            if (currentPosition != lastTime && !mainActivity.getSlidingUpPanelLayout().getPanelSlideListener().isPanelSliding()) {
                                                Log.i("PlayFragment", "SeekUpdation called - Time updated");

                                                updatePlaybackUIElements(currentPosition);
                                            }


                                            lastTime = currentPosition;

                                        } catch (IllegalStateException e) {

                                        }

                                    }
                                });
                            }
                        }
                    }catch (Exception e){

                    }
                    try{ sleep(1000); }
                    catch(InterruptedException e){ }
                }
            }
        }).start();
    }
    public void updateGraphCenter() {
        final Handler handler = new Handler();

        (new Thread(){
            @Override
            public void run() {

                while (true) {
                    try {
                        if (mBound) {
                            if (playService.getMediaPlayer().isPlaying()) {
                                final float currentPosition = playService.getMediaPlayer().getCurrentPosition() / 1000f;
                                Log.i("test", Float.toString(currentPosition));
                                handler.post(new Runnable() {
                                    public void run() {
                                        try {

                                            if (currentPosition != lastTime && !mainActivity.getSlidingUpPanelLayout().getPanelSlideListener().isPanelSliding() && updateGraphCenter) {
                                                mLineChart.centerViewPort(currentPosition * xValsPerSecond, 1f);
                                                Log.i("PlayFragment", "graphUpdation called - Time updated");

                                            }


                                            gLastTime = currentPosition;

                                        } catch (IllegalStateException e) {

                                        }

                                    }
                                });
                            }
                        }
                    }catch (Exception e){

                    }
                    try{ sleep(calculateGraphUpdateFrequency()); }
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

    public void togglePlayPauseButton(boolean setPaused){
        final boolean mSetPlaying = setPaused;
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
    public void setUpChart(ArrayList <Float> audioData){
        mLineChart.centerViewPort(0,0);
        //mLineChart.setOffsets(0,50,0,50);
        mLineChart.setDrawYValues(false);
        mLineChart.setDescription("");
        mLineChart.setDrawLegend(false);
        mLineChart.setDrawBorder(false);
        mLineChart.setDrawUnitsInChart(false);
        // mLineChart.setNoDataTextDescription("No wave data is available for this audio file.");
        mLineChart.setNoDataText("No wave data is available for this audio file.");
        //mLineChart.setDrawXLabels(false);
        mLineChart.setPinchZoom(false);
        mLineChart.setBackgroundColor(Color.WHITE);
        mLineChart.setGridColor(Color.GRAY);
        mLineChart.setDrawVerticalGrid(false);
        mLineChart.setDrawGridBackground(false);
        mLineChart.getYLabels().setLabelCount(0);
        mLineChart.getYLabels().setDrawTopYLabelEntry(false);
        mLineChart.setStartAtZero(false);
        //mLineChart.setScaleMinima(); TODO

        mLineChart.setHighlightEnabled(false);
        mLineChart.setHighlightIndicatorEnabled(false);

        //mLineChart.setMaximumZoom(calculateMaximumZoom());

        mLineChart.setDragEnabled(true);
        //mLineChart.setDragOffsetX(40f); sets white space at the sides of graph when scrolling
        //mLineChart.setDoubleTapToZoomEnabled(false);
        mLineChart.setHighlightEnabled(false);
        mLineChart.setDragOffsetX((mLineChart.getMeasuredWidth() / 2) / getActivity().getResources().getDisplayMetrics().density);
        mLineChart.setDrawXLabels(false);

       // mLineChart.zoom(calculateOptimalZoomLevel(), 1f, mLineChart.getMeasuredWidth() / 2, mLineChart.getMeasuredHeight() / 2);





        mLineChart.setOnChartGestureListener(new OnChartGestureListener() {
            boolean chartBeenDragged = false;
            @Override
            public void onChartTouched(MotionEvent me) {
                try {
                    if (playService.getMediaPlayer().isPlaying()){
                        playService.getMediaPlayer().pause();
                        if ((Boolean) playButton.getTag() == false) {
                            playButtonShowing = true;
                            playButton.setImageResource(R.drawable.ic_play);
                            playButton.setTag(new Boolean(true));

                        }
                    }
                    isChartBeingTouched = true;
                }catch (Exception e){
                    Log.e("error: ", "" + e);
                }
            }

            @Override
            public void onChartTouchReleased(MotionEvent me) {
                if(chartBeenDragged ){
                    try {
                        Log.i("gesture touch", "drag released");
                        chartBeenDragged = false;
                        double x = getXAtCenter();

                        playService.getMediaPlayer().seekTo((int) Math.round(x * 1000 / xValsPerSecond));
                        if(!playService.getMediaPlayer().isPlaying()){
                            playService.getMediaPlayer().start();

                            if ((Boolean) playButton.getTag() == true) {
                                playButtonShowing = false;
                                playButton.setImageResource(R.drawable.ic_pause);
                                playButton.setTag(new Boolean(false));

                            }
                        }
                        isChartBeingTouched = false;
                    }catch (Exception e){
                        Log.e("error: ", "" + e);
                    }
                }

            }

            @Override
            public void onChartZoomed(MotionEvent me) {

            }

            @Override
            public void onChartLongPressed(MotionEvent me) {

            }

            @Override
            public void onChartDoubleTapped(MotionEvent me) {

            }

            @Override
            public void onChartSingleTapped(MotionEvent me) {
                Log.i("gesture", "tapped");
            }

            @Override
            public void onChartDragged(MotionEvent me) {
                chartBeenDragged = true;
                Log.i("gesture", "dragged");
                double x = getXAtCenter();
                int progress = seekbar.getProgress();
                int xValRounded = (int) Math.floor(x / xValsPerSecond);

                if(xValRounded != progress){
                    updatePlaybackUIElements(xValRounded);

                }

            }

            @Override
            public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
                Log.i("gesture", "flinged");

            }
        });

        //mLineChart.setOnTouchListener();
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
        int j = 0;
        for (float temp: audioData){
            vals.add(new Entry(temp, j));
            xVals.add(Integer.toString(j));

            j++;
        }
        lineDataSet = new LineDataSet(vals, "Random  Numbers");
        lineDataSet.setLineWidth(1.5f);
        lineDataSet.setCircleColor(Color.RED);
        lineDataSet.setCircleSize(4);


        lineDataSet.setColor(mainActivity.getListAdapter().getRowColor(currentFilePosition));
        lineDataSet.setDrawCircles(false);
        lineDataSet.setDrawCubic(true);

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(lineDataSet);

        LineData data = new LineData(xVals, dataSets);
        mLineChart.setData(data);
        //mLineChart.animateYFromZero(600);


        mLineChart.zoom(calculateOptimalZoomLevel(), 1f, mLineChart.getMeasuredWidth() / 2, mLineChart.getMeasuredHeight() / 2);

        Log.i("status", "chart data set");
    }

    public ArrayList <Float> buildAudioWaveData(Recording recording){
        final Recording finalRecording = recording;
        final Handler handler = new Handler();
        final ArrayList<Float> dataSeries = new ArrayList<Float>();

        (new Thread(){
            @Override
            public void run(){
                File recFile = finalRecording.getFile();

                try {
                    InputStream bis = new BufferedInputStream(new FileInputStream(recFile));
                    DataInputStream dis = new DataInputStream(bis);
                    RandomAccessFile ra = new RandomAccessFile(recFile, "rw");

                    long sampleRate = finalRecording.getSampleRate(ra);
                    int channels = finalRecording.getNumChannels(ra);
                    int bits = finalRecording.getBitsPerSample(ra);
                    long audioLength = finalRecording.getAudioLengthInSeconds();

                    long numberOfDatumsToCompute = audioLength * xValsPerSecond; // One sample for every 1000 ms.
                    long fileLengthInBytes = recFile.length() - 44;
                    long fileSamplesRemaining = fileLengthInBytes /( (bits / 8) * channels)  ; // 16 bit wave file = 2 bytes per sample.
                    float max = 0;
                    int numberCountsPerCycle = 50;
                    int samplesPerDatum = (int) sampleRate / xValsPerSecond;
                    long totalBytesSkipped = 0;
                    int cycleBytesSkipped = 0;
                    dis.skipBytes(44);

                    if(channels == 1){
                        for(long j = 0; j < numberOfDatumsToCompute && fileSamplesRemaining > samplesPerDatum; j++){
                            for(int i = 0; i < numberCountsPerCycle; i++) {
                                short tempShort = Short.reverseBytes(dis.readShort());
                                float temp = normaliseDatum(tempShort);
                                max = Math.max(max, Math.abs(temp));


                                dis.skipBytes(samplesPerDatum / numberCountsPerCycle * 2 - 2);
                                cycleBytesSkipped += samplesPerDatum / numberCountsPerCycle * 2 - 2;
                                totalBytesSkipped += samplesPerDatum / numberCountsPerCycle * 2 - 2;
                            }
                            cycleBytesSkipped = 0;
                            fileSamplesRemaining -= samplesPerDatum;

                            dataSeries.add(max);
                            //dataSeries.add(avgCount / 5);
                            // avgCount = 0;
                            max = 0;
                            //dis.skipBytes(samplesPerDatum);
                            //fileSamplesRemaining -= samplesPerDatum;


                        }

                    }
                    else if(channels == 2){
                        for(long h = 0; h < numberOfDatumsToCompute && fileSamplesRemaining > samplesPerDatum; h++){
                            for(int i = 0; i < numberCountsPerCycle; i++) {

                                short tempShort = Short.reverseBytes(dis.readShort());
                                float temp = normaliseDatum(tempShort);

                                max = Math.max(max, Math.abs(temp));

                                cycleBytesSkipped += samplesPerDatum / numberCountsPerCycle * 4 - 2;
                                totalBytesSkipped += samplesPerDatum / numberCountsPerCycle * 4 - 2;

                                dis.skipBytes(samplesPerDatum / numberCountsPerCycle * 4 - 2);
                            }
                            cycleBytesSkipped = 0;
                            fileSamplesRemaining -= samplesPerDatum / numberCountsPerCycle * 2;

                            dataSeries.add(max);
                            max = 0;
                            //dis.skipBytes(samplesPerDatum);

                            //fileSamplesRemaining -= samplesPerDatum;
                        }
                    }
                    Log.i("status", "finished building data");
                    handler.post(new Runnable() {
                        public void run() {
                            Log.i("status", "setting chart data");
                            setUpChart(dataSeries);
                        }
                    });
                }catch(IOException e){
                    Log.e("exception", "error: "  + e);
                    setUpChart(dataSeries);

                }
            }
        }).start();


        return dataSeries;
    }

    public float calculateOptimalZoomLevel(){
        int graphWidthPX = mLineChart.getMeasuredWidth();
        //int graphHeightPX = mLineChart.getMeasuredHeight();
        float graphWidthDP = Utils.convertPixelsToDp(graphWidthPX);
        float numberOfSecondsToShow = graphWidthDP / 70f;
        //int recordingLength = playService.getMediaPlayer().getDuration() / 1000; // in seconds
        long recordingLength = recording.getAudioLengthInSeconds(); // in seconds

        float zoomLevel = recordingLength / numberOfSecondsToShow;
        return zoomLevel;
    }
    public void calculateOnDemandDataSets(final float overflow, final float visibleRange, final int currentRecordingPosition, final Recording recording){

        final Recording finalRecording = recording;
        final Handler handler = new Handler();
        final ArrayList <Float> dataSeries = new ArrayList<Float>();

        (new Thread(){
            @Override
            public void run(){
                File recFile = finalRecording.getFile();
                try {
                    InputStream bis = new BufferedInputStream(new FileInputStream(recFile));
                    DataInputStream dis = new DataInputStream(bis);
                    RandomAccessFile randomAccessFile =  new RandomAccessFile(recFile, "rw");
                    long sampleRate = finalRecording.getSampleRate(randomAccessFile);
                    int numChanels = finalRecording.getNumChannels(randomAccessFile);
                    int samplesPerDatum = (int)sampleRate / 100; // One sample for every 1000 ms. TODO
                    long fileLengthInBytes = recFile.length();
                    long fileDataRemaining = fileLengthInBytes / numChanels; // 16 bit wave file = 2 bytes per sample.
                    int max = 0;
                    short temp = 0;
                    //long sampleReadCount = 0;
                    long skipBytes = Math.round((currentRecordingPosition * sampleRate) - ((visibleRange / 2)*sampleRate)+visibleRange * overflow * sampleRate);
                    float rangeToCompute = visibleRange + overflow * visibleRange + (skipBytes / sampleRate);
                    int endOfReadingBytes = Math.round(skipBytes + rangeToCompute * sampleRate);
                    long totalSkipped = 0;
                    if(skipBytes > 0) {
                        dis.skip(skipBytes);
                    }
                    else{
                    }
                    for(long sampleReadCount = 0; sampleReadCount < rangeToCompute; sampleReadCount++ ){
                        if(fileDataRemaining > samplesPerDatum) {
                            for(int i = 0; i < sampleRate / samplesPerDatum; i++){
                                temp = dis.readShort();
                                if (temp > max) {
                                    max = temp;
                                }
                                dis.skip(sampleRate / samplesPerDatum);
                                totalSkipped+= sampleRate / samplesPerDatum;
                            }
                            dataSeries.add((float)max);
                            max = 0;
                        }
                        fileDataRemaining -= samplesPerDatum;
                    }

                    Log.i("status", "finished building data");
                    handler.post(new Runnable() {
                        public void run() {
                            Log.i("status", "setting chart data");
                            setUpChart(dataSeries);
                        }
                    });
                }catch(IOException e){
                    Log.e("exception", "error: "  + e);
                }
            }
        }).start();

    }
    private float normaliseDatum(short datum){
        return datum / 32768f;
    }
    private long calculateGraphUpdateFrequency(){
        try {
            graphWidth = Utils.convertPixelsToDp(mLineChart.getMeasuredWidth());

            float domainCount = mLineChart.getVisibleDomainCount();
                if(graphWidth != 0) {
                    long result = Math.round(domainCount / (graphWidth + 1) / 2f * 200f);

                    if(result > 0){
                        return result;
                    }else{
                        return 200;
                    }
            }
            else{
                    return 200;
                }
        }catch (NullPointerException e){
            return 100;
        }
    }
    private float calculateMaximumZoom(){
        long seconds = recording.getAudioLengthInSeconds();
        float graphWidth = Utils.convertPixelsToDp(getActivity().getResources().getDisplayMetrics().widthPixels);
        float result = seconds / (graphWidth / 60);
        Toast.makeText(getActivity(), Float.toString(result), Toast.LENGTH_LONG).show();

        return result;
    }
    public double getXAtCenter(){
        PointD p =  mLineChart.getValuesByTouchPoint(mLineChart.getMeasuredWidth() / 2, mLineChart.getMeasuredHeight() / 2);

        return p.x;
    }
    private void updatePlaybackUIElements(int currentPlaybackTime){

        seekbar.setProgress(currentPlaybackTime);
        currentPlaybackPosition.setText(getAudioLengthInHMSFormat(currentPlaybackTime));
    }

    private void setChartAnimationFinished(boolean trueOrFalse){
        chartAnimationFinished = trueOrFalse;
    }

    public ArrayList <Float> buildAudioWaveDataAsynTask(Recording recording){

        class calculateAudioWaveData extends AsyncTask<Recording, Integer, ArrayList<Float>>{
            Recording finalRecording;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(ArrayList<Float> floats) {
                ArrayList<Float> floatss = floats;
                setUpChart(floats);
                mLineChart.animateYFromZero(500);
                super.onPostExecute(floats);
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
            }

            @Override
            protected ArrayList<Float> doInBackground(Recording... params) {
                finalRecording = params[0];
                File recFile = finalRecording.getFile();
                final ArrayList<Float> mDataSeries = new ArrayList<Float>();


                try {
                    InputStream bis = new BufferedInputStream(new FileInputStream(recFile));
                    DataInputStream dis = new DataInputStream(bis);
                    RandomAccessFile ra = new RandomAccessFile(recFile, "rw");

                    long sampleRate = finalRecording.getSampleRate(ra);
                    int channels = finalRecording.getNumChannels(ra);
                    int bits = finalRecording.getBitsPerSample(ra);
                    long audioLength = finalRecording.getAudioLengthInSeconds();

                    long numberOfDatumsToCompute = audioLength * xValsPerSecond; // One sample for every 1000 ms.
                    long fileLengthInBytes = recFile.length() - 44;
                    long fileSamplesRemaining = fileLengthInBytes / ((bits / 8) * channels); // 16 bit wave file = 2 bytes per sample.
                    float max = 0;
                    int numberCountsPerCycle = 50;
                    int samplesPerDatum = (int) sampleRate / xValsPerSecond;
                    long totalBytesSkipped = 0;
                    int cycleBytesSkipped = 0;
                    dis.skipBytes(44);

                    if (channels == 1) {
                        for (long j = 0; j < numberOfDatumsToCompute && fileSamplesRemaining > samplesPerDatum; j++) {
                            for (int i = 0; i < numberCountsPerCycle; i++) {
                                short tempShort = Short.reverseBytes(dis.readShort());
                                float temp = normaliseDatum(tempShort);
                                max = Math.max(max, Math.abs(temp));


                                dis.skipBytes(samplesPerDatum / numberCountsPerCycle * 2 - 2);
                                cycleBytesSkipped += samplesPerDatum / numberCountsPerCycle * 2 - 2;
                                totalBytesSkipped += samplesPerDatum / numberCountsPerCycle * 2 - 2;
                            }
                            cycleBytesSkipped = 0;
                            fileSamplesRemaining -= samplesPerDatum;

                            mDataSeries.add(max);
                            //dataSeries.add(avgCount / 5);
                            // avgCount = 0;
                            max = 0;
                            //dis.skipBytes(samplesPerDatum);
                            //fileSamplesRemaining -= samplesPerDatum;


                        }

                    }
                    else if(channels == 2){
                        for(long h = 0; h < numberOfDatumsToCompute && fileSamplesRemaining > samplesPerDatum; h++){
                            for(int i = 0; i < numberCountsPerCycle; i++) {

                                short tempShort = Short.reverseBytes(dis.readShort());
                                float temp = normaliseDatum(tempShort);

                                max = Math.max(max, Math.abs(temp));

                                cycleBytesSkipped += samplesPerDatum / numberCountsPerCycle * 4 - 2;
                                totalBytesSkipped += samplesPerDatum / numberCountsPerCycle * 4 - 2;

                                dis.skipBytes(samplesPerDatum / numberCountsPerCycle * 4 - 2);
                            }
                            cycleBytesSkipped = 0;
                            fileSamplesRemaining -= samplesPerDatum / numberCountsPerCycle * 2;

                            mDataSeries.add(max);
                            max = 0;
                            //dis.skipBytes(samplesPerDatum);

                            //fileSamplesRemaining -= samplesPerDatum;
                        }
                    }

                    Log.i("status", "setting chart data");
                }catch (Exception e){
                    Log.e("error", "error: " + e);
                    e.printStackTrace();

                }
                return mDataSeries;

            }
        }

        new calculateAudioWaveData().execute(recording);


    return null;

    }

    public void calculateDatasetSubset(float scale, final float visibleRange, float graphCenterXVal, final float overflowRange){
        final Recording finalRecording = recording;
        final Handler handler = new Handler();
        final ArrayList<Float> dataSeries = new ArrayList<Float>();

        (new Thread(){
            @Override
            public void run(){
                File recFile = finalRecording.getFile();

                try {
                    InputStream bis = new BufferedInputStream(new FileInputStream(recFile));
                    DataInputStream dis = new DataInputStream(bis);
                    RandomAccessFile ra = new RandomAccessFile(recFile, "rw");

                    long sampleRate = finalRecording.getSampleRate(ra);
                    int channels = finalRecording.getNumChannels(ra);
                    int bits = finalRecording.getBitsPerSample(ra);
                    long audioLength = finalRecording.getAudioLengthInSeconds();

                    long numberOfDatumsToCompute = audioLength * xValsPerSecond; // One sample for every 1000 ms.
                    long fileLengthInBytes = recFile.length() - 44;
                    long fileSamplesRemaining = fileLengthInBytes /( (bits / 8) * channels)  ; // 16 bit wave file = 2 bytes per sample.
                    float max = 0;
                    int numberCountsPerCycle = 50;
                    int samplesPerDatum = (int) sampleRate / xValsPerSecond;
                    long totalBytesSkipped = 0;
                     int cycleBytesSkipped = 0;
                 // dis.skipBytes(44 + (visibleRange * sampleRate * 2 * channels - sampleRate * 2 * overflowRange * visibleRange));

                    if(channels == 1){
                        for(long j = 0; j < numberOfDatumsToCompute && fileSamplesRemaining > samplesPerDatum; j++){
                            for(int i = 0; i < numberCountsPerCycle; i++) {
                                short tempShort = Short.reverseBytes(dis.readShort());
                                float temp = normaliseDatum(tempShort);
                                max = Math.max(max, Math.abs(temp));


                                dis.skipBytes(samplesPerDatum / numberCountsPerCycle * 2 - 2);
                                cycleBytesSkipped += samplesPerDatum / numberCountsPerCycle * 2 - 2;
                                totalBytesSkipped += samplesPerDatum / numberCountsPerCycle * 2 - 2;
                            }
                            cycleBytesSkipped = 0;
                            fileSamplesRemaining -= samplesPerDatum;

                            dataSeries.add(max);
                            //dataSeries.add(avgCount / 5);
                            // avgCount = 0;
                            max = 0;
                            //dis.skipBytes(samplesPerDatum);
                            //fileSamplesRemaining -= samplesPerDatum;


                        }

                    }
                    else if(channels == 2){
                        for(long h = 0; h < numberOfDatumsToCompute && fileSamplesRemaining > samplesPerDatum; h++){
                            for(int i = 0; i < numberCountsPerCycle; i++) {

                                short tempShort = Short.reverseBytes(dis.readShort());
                                float temp = normaliseDatum(tempShort);

                                max = Math.max(max, Math.abs(temp));

                                cycleBytesSkipped += samplesPerDatum / numberCountsPerCycle * 4 - 2;
                                totalBytesSkipped += samplesPerDatum / numberCountsPerCycle * 4 - 2;

                                dis.skipBytes(samplesPerDatum / numberCountsPerCycle * 4 - 2);
                            }
                            cycleBytesSkipped = 0;
                            fileSamplesRemaining -= samplesPerDatum / numberCountsPerCycle * 2;

                            dataSeries.add(max);
                            max = 0;
                            //dis.skipBytes(samplesPerDatum);

                            //fileSamplesRemaining -= samplesPerDatum;
                        }
                    }
                    Log.i("status", "finished building data");
                    handler.post(new Runnable() {
                        public void run() {
                            Log.i("status", "setting chart data");
                            setUpChart(dataSeries);
                        }
                    });
                }catch(IOException e){
                    Log.e("exception", "error: "  + e);
                    setUpChart(dataSeries);

                }
            }
        }).start();

    }

}

