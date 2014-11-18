package com.bacon.corey.audiotimeshift;

import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


public class PlayService extends IntentService {

    public PlayService() {
        super("PlayService");
    }
    AudioTrack audioTrack;
    Notification notifaction;
    MediaPlayer mediaPlayer;
    @Override
    protected void onHandleIntent(Intent intent) {

        if(intent.getIntExtra("playItemPosition", -1) != -1){
            Log.v("playservice", "play called");
            //play(AudioFormat.CHANNEL_OUT_MONO, 44100, AudioFormat.ENCODING_PCM_16BIT, intent.getIntExtra("playItemPosition", -1), (Recording)intent.getSerializableExtra("playItem"));
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_play).setContentText("Recording Playing").setContentText("content text blah blah");
            //final Intent notificationIntent = new Intent(this, MainActivity.class);
            //final PendingIntent pendingIntent = new PendingIntent.getActivity(this, 101, notificationIntent, 101);
            notifaction = notificationBuilder.build();
            startForeground(Constants.FOREGROUND_ID, notifaction);
            playMediaPlayer(intent.getIntExtra("playItemPosition", -1), (Recording)intent.getSerializableExtra("playItem"));
            stopForeground(true);
        }
        else if(intent.getIntExtra("action", -1) == Constants.STOP) {
            stopMediaPlayer();
        }
        else if(intent.getIntExtra("action", -1) == Constants.PAUSE){
            pauseMediaPlayer();
        }
    }


    public void playMediaPlayer(int position, Recording recording){
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(recording.getFilePathString());
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer.release();
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
    }
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
            int headerByteCount = 0;  // TODO add later
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

}


