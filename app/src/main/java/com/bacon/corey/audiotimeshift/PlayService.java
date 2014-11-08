package com.bacon.corey.audiotimeshift;

import android.app.IntentService;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;


public class PlayService extends IntentService {

    public PlayService() {
        super("PlayService");
    }
    AudioTrack audioTrack;
    @Override
    protected void onHandleIntent(Intent intent) {

        if(intent.getIntExtra("action", Constants.PLAY) == Constants.PLAY){
            Log.v("playservice", "play called");
            play(AudioFormat.CHANNEL_OUT_MONO, 44100, AudioFormat.ENCODING_PCM_16BIT, "Recording.pcm");
        }
        else if(intent.getIntExtra("action", Constants.PLAY) == Constants.STOP) {
            stop();
        }
        else if(intent.getIntExtra("action", Constants.PLAY) == Constants.PAUSE){
            //pause();
        }
    }



    public void play(int CHANNEL_OUT_MODE, int frequency, int ENCODING_FORMAT, String fileName) {

        File file = new File(Environment.getExternalStorageDirectory() + File.separator + Constants.DIRECTORY + File.separator + fileName);
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
            int bite = 64;
            int remainder = musicLength % bite;
            short[] music = new short[bite];

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


