package com.bacon.corey.audiotimeshift;

import android.app.IntentService;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Record extends IntentService {
    public File directory;
    AudioRecord audioRecord;
    public Record() {
        super("Record");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.v("Record", "Record called");

        record();
    }

    public void record() {
        Log.v("Record", "Record started");
        int frequency = 44100;
        int channelConfiguration = AudioFormat.CHANNEL_IN_MONO;
        int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
        File file;

        directory = new File(Environment.getExternalStorageDirectory() + File.separator + Constants.DIRECTORY);
        if (directory.isDirectory()) {
            file = new File(Environment.getExternalStorageDirectory() + File.separator + "TimeShiftRecorder" + File.separator + "Recording.pcm");
        }
        else{
            directory.mkdir();
            file = new File(Environment.getExternalStorageDirectory() + File.separator + "TimeShiftRecorder" + File.separator + "Recording.pcm");

        }

// Delete any previous recording.
        if (file.exists())
            file.delete();


// Create the new file.
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create " + file.toString());
        }
        Log.v("Record", "Log 1");
        try {
// Create a DataOuputStream to write the audio data into the saved file.
            OutputStream os = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(os);
            DataOutputStream dos = new DataOutputStream(bos);
            Log.v("Record", "Log 2");
// Create a new AudioRecord object to record the audio.
            int bufferSize = AudioRecord.getMinBufferSize(frequency, channelConfiguration, audioEncoding)*2;
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    frequency, channelConfiguration,
                    audioEncoding, bufferSize);
            Log.v("Record", "Log 3");
            short[] buffer = new short[bufferSize];
            audioRecord.startRecording();
            Log.v("Record", "Log 4");

            while (audioRecord.getRecordingState() == audioRecord.RECORDSTATE_RECORDING) {
                //Log.v("Record", "Log 5");
                int bufferReadResult = audioRecord.read(buffer, 0, bufferSize);
                for (int i = 0; i < bufferReadResult; i++)
                    dos.writeShort(buffer[i]);
                    //Log.v("Record", "Log 6");
            }


            audioRecord.stop();
            dos.close();

        } catch (Throwable t) {
            Log.e("AudioRecord", "Recording Failed");
        }
        Log.v("Record", "Record stopped");

    }

    public void stop(){
        audioRecord.stop();
    }
    public void pause(){
    }
    public void onDestroy(){
        audioRecord.stop();
        Log.v("Record", "onDestroy called, Record stopped");

    }

}