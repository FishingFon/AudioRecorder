package com.bacon.corey.audiotimeshift;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioFormat;
import android.media.MediaRecorder;

import java.lang.reflect.Array;
import java.util.ArrayList;

public abstract class RecordingOptionsCalculator {



    public static ArrayList getSupportedSampleRates(){
        ExtAudioRecorder result;
        int[] sampleRates = {48000, 44100, 32000, 22050, 16000, 11025, 8000};
        ArrayList <Integer> suitableSampleRates = new ArrayList<Integer>();
        int i=0;
        do
        {

            result = new ExtAudioRecorder(	true,
                    MediaRecorder.AudioSource.MIC,
                    sampleRates[i],
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);

            if((result.getState() == ExtAudioRecorder.State.INITIALIZING)){
                suitableSampleRates.add(sampleRates[i]);
            }
        } while(++i < sampleRates.length);
        return suitableSampleRates;
    }
    public static ArrayList getSupportedSampleRateStrings(){
        ArrayList <String> suitableSampleRateStrings = new ArrayList<String>();
        ArrayList <Integer> sampleRates = getSupportedSampleRates();
        for (Integer temp: sampleRates){
            suitableSampleRateStrings.add(Integer.toString(temp) + " Hz");
        }
        return suitableSampleRateStrings;
    }
    public static int getHighestSampleRate(){
        ArrayList<Integer> sampleRates = getSupportedSampleRates();

        if(sampleRates.get(0) != null){
            return sampleRates.get(0);
        }
        else
            return -1;
    }
    public static int getLowestSampleRate(){
        ArrayList<Integer> sampleRates = getSupportedSampleRates();

        if(sampleRates.size() >= 1){
            return sampleRates.get(sampleRates.size() - 1);
        }
        else
            return -1;
    }

    public static ArrayList getSupportedChannels(){
        ArrayList<Integer> supportedChannels = new ArrayList<Integer>();
        ExtAudioRecorder result;
        int[] channelList = {AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_STEREO};
        int i=0;
        do
        {

            result = new ExtAudioRecorder(	true,
                    MediaRecorder.AudioSource.MIC,
                    getLowestSampleRate(),
                    channelList[i],
                    AudioFormat.ENCODING_PCM_16BIT);

            if((result.getState() == ExtAudioRecorder.State.INITIALIZING)){
                supportedChannels.add(channelList[i]);
            }
        } while(++i < channelList.length);
        return supportedChannels;
    }

    public static int getLowestSupportedChannel(){
        ArrayList<Integer> supportedChannels = getSupportedChannels();

        if(supportedChannels.get(0) != null){
            return supportedChannels.get(0);
        }
        else
            return -1;
    }

    public static int getHighestSupportedChannel(){
        ArrayList<Integer> supportedChannels = getSupportedChannels();

        if(supportedChannels.size() >= 1){
            return supportedChannels.get(supportedChannels.size() - 1);
        }
        else
            return -1;
    }
    public static void writeDeviceSupportedOptionsToDiskInt(Context context, String key, int datum){
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.recording_options_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, datum);
    }
    public static void writeDeviceSupportedOptionsToDiskString(Context context, String key, String datum){
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.recording_options_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, datum);
    }

}
