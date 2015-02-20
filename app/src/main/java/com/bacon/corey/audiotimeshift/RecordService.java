package com.bacon.corey.audiotimeshift;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.io.IOException;

public class RecordService extends Service {
    public final IBinder mBinder = new LocalBinder();
    ExtAudioRecorder audioRecorder;
    public class LocalBinder extends Binder {
        RecordService getService() {
            // Return this instance of LocalService so clients can call public methods
            return RecordService.this;
        }
    }

    public RecordService() {
    }

    @Override
    public IBinder onBind(Intent intent) {

        return mBinder;
    }

    public void instantiateAudioRecorder(int channels, int sampleRate, boolean uncompressed, int bits, int audioSource){
        int test = channels;
        audioRecorder = new ExtAudioRecorder(uncompressed, audioSource, sampleRate, channels, bits);
    }
    public ExtAudioRecorder getAudioRecorder() throws Exception {
        if (audioRecorder != null){
            return audioRecorder;

        }
        else {
            throw new Exception("AudioRecorder is a null object reference - Call instantiateAudioRecorder before getAudioRecorder");
        }
    }


}
