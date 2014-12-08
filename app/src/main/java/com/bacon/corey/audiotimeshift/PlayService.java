package com.bacon.corey.audiotimeshift;

import android.app.IntentService;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;


public class PlayService extends Service {
    AudioTrack audioTrack;
    Notification notifaction;
    MediaPlayer mediaPlayer;
    Recording recording;
    int position;
    public static boolean serviceIsRunning = false;
    boolean currentlyPlaying;
    public final IBinder mBinder = new LocalBinder();
    public class LocalBinder extends Binder {
        PlayService getService() {
            // Return this instance of LocalService so clients can call public methods
            return PlayService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.v("playservice", "play called");
        //final Intent notificationIntent = new Intent(this, MainActivity.class);
        //final PendingIntent pendingIntent = new PendingIntent.getActivity(this, 101, notificationIntent, 101);
        try {
            recording = (Recording) intent.getSerializableExtra("playItem");
            position = intent.getIntExtra("playItemPosition", -1);
            //playNewFile();
        }catch (Exception e){

        }
        return mBinder;
    }

    public PlayService() {
        //super("PlayService");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        serviceIsRunning = false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        serviceIsRunning = true;
    }

    /*
        @Override
        protected void onHandleIntent(Intent intent) {
            if(intent.getIntExtra("playItemPosition", -1) != -1){
                Log.v("playservice", "play called");
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_play).setContentText("Recording Playing").setContentText("content text blah blah");
                //final Intent notificationIntent = new Intent(this, MainActivity.class);
                //final PendingIntent pendingIntent = new PendingIntent.getActivity(this, 101, notificationIntent, 101);
                notifaction = notificationBuilder.build();
                startForeground(Constants.FOREGROUND_ID, notifaction);
                recording = (Recording)intent.getSerializableExtra("playItem");
                playMediaPlayer(intent.getIntExtra("playItemPosition", -1), recording);
                stopForeground(true);
            }
            else if(intent.getIntExtra("action", -1) == Constants.STOP) {
                stopMediaPlayer();
            }
            else if(intent.getIntExtra("action", -1) == Constants.PAUSE){
                pauseMediaPlayer();
            }
        }

    */
    public void playNewFile(int pos, Recording rec){
        playMediaPlayer(pos, rec);

    }
    public void playMediaPlayer(int pos, Recording rec){
        try {

            mediaPlayer = new MediaPlayer();

            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(rec.getFilePathString());
            mediaPlayer.prepare();
            mediaPlayer.start();
            currentlyPlaying = true;

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
            {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    //mediaPlayer.release();
                    currentlyPlaying = false;

                }
            });
        }catch (IOException e){
            Log.e("playMediaPlayer()", "Error: " + e);
        }
    }
    public void pauseMediaPlayer(){
        mediaPlayer.pause();
    }
    public void stopMediaPlayer(){
        mediaPlayer.stop();
        mediaPlayer.release();
        currentlyPlaying = false;

    }
    /*
    public void play(int CHANNEL_OUT_MODE, int frequency, int ENCODING_FORMAT, int position, Recording recording) {
          File file = recording.getFile();
        //File file = new File(Environment.getExternalStorageDirectory() + File.separator + Constants.DIRECTORY + File.separator + recording.getTitleString());
        int musicLength = (int)(file.length()/2);

        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                frequency,
                CHANNEL_OUT_MODE,
                ENCODING_FORMAT,
                AudioTrack.getMinBufferSize(frequency, CHANNEL_OUT_MODE, ENCODING_FORMAT),
                AudioTrack.MODE_STREAM);
        try {

            InputStream bis = new BufferedInputStream(new FileInputStream(file));
            DataInputStream dis = new DataInputStream(bis);

            int bite = 44;
            int remainder = musicLength % bite;
            short[] music = new short[bite];
            int headerByteCount = 0;
            while (dis.available() >= bite) {
                for (int j = 0; j < bite; j++) {
                    music[j] = dis.readShort();
                }
                audioTrack.play();
                audioTrack.write(music, 0, bite);
            }

            if(remainder != 0) {
                while (dis.available() > 0) {
                    bite = dis.available();
                    for (int i = 0; i < bite; i++) {
                        music = new short[bite];
                        music[i] = dis.readShort();
                    }
                    audioTrack.write(music, 0, bite);
                }
            }
            audioTrack.stop();

            dis.close();

        } catch (Throwable t) {
            Log.e("AudioTrack", "Playback Failed");
        }
    }
    public void stop(){
        audioTrack.stop();
    }
    public void pause(){
        audioTrack.pause();
    }
    */
    public MediaPlayer getMediaPlayer(){
       return mediaPlayer;
    }
    public void setMediaPlayerVolume(int volume){
        mediaPlayer.setVolume(volume, volume);

    }

}


