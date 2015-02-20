package com.bacon.corey.audiotimeshift;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class RecordingOptionsCalculator {

    static int[] possibleSampleRates = {48000, 44100, 32000, 22050, 16000, 11025, 8000};

    static List<Recording> recordingsList = new ArrayList<Recording>();
    static List<File> files = new ArrayList<File>();

    public static ArrayList getSupportedSampleRates(){
        ExtAudioRecorder result;
        ArrayList <Integer> suitableSampleRates = new ArrayList<Integer>();
        int i=0;
        do
        {

            result = new ExtAudioRecorder(	true,
                    MediaRecorder.AudioSource.MIC,
                    possibleSampleRates[i],
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);

            if((result.getState() == ExtAudioRecorder.State.INITIALIZING)){
                suitableSampleRates.add(possibleSampleRates[i]);
            }
        } while(++i < possibleSampleRates.length);
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
    public static ArrayList getSupportedSampleRateDescriptions(){
        ArrayList <String> sampleRateDescriptions = new ArrayList<String>();
        ArrayList <Integer> sampleRates = getSupportedSampleRates();
        for (Integer temp: sampleRates){
            switch (temp){
                case 48000:
                    sampleRateDescriptions.add("Professional quality (48 kHz)");
                    break;
                case 44100:
                    sampleRateDescriptions.add("Audio CD quality (44 kHz)");
                    break;
                case 32000:
                    sampleRateDescriptions.add("FM Radio quality (32 kHz)");
                    break;
                case 22050:
                    sampleRateDescriptions.add("AM Radio quality (22 kHz)");
                    break;
                case 16000:
                    sampleRateDescriptions.add("VoIP quality (16 kHz)");
                    break;
                case 11025:
                    sampleRateDescriptions.add("Lower quality (11 kHz)");
                    break;
                case 8000:
                    sampleRateDescriptions.add("Telephone quality (8 kHz)");
                    break;
            }
        }
        return sampleRateDescriptions;
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
    public static int getDefaultSampleRate(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String result = sharedPreferences.getString("pref_sampleRates", "");
        if(result != "" && result != null){
            return Integer.parseInt(result);
        }
        else{
            return getHighestSampleRate();
        }

    }
    public static int getDefaultChannels(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String result = sharedPreferences.getString("pref_channels", "");
        if(result != "" && result != null){
            return Integer.parseInt(result);
        }
        else{
            return getHighestSupportedChannel();
        }

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
    public static List getFiles(String directoryNameArg) {
        final String directoryName = directoryNameArg;
        new Thread(new Runnable() {
            @Override
            public void run() {
                File directory = new File(directoryName);
                Log.v("FilesListFragment", "getFiles();");
                if (directory.exists()) {
                    File[] fileList = directory.listFiles();

                    for (File file : fileList) {
                        if (file.isFile() && !files.contains(file)) {
                            files.add(file);
                        }
                /*
                else if(file.isDirectory()){
                    files(file.getAbsoluteFile(), false);
                }
                */
                    }
                    recordingsList = updateRecordingsList();

                }
            }
        }).start();

        return recordingsList;
    }
    public static List getExistingFiles(String directoryNameArg) {
        final String directoryName = directoryNameArg;
        if(recordingsList != null){
            return recordingsList;
        }
        else{
            return getFiles(directoryNameArg);
        }

    }
    public static List updateRecordingsList(){
        new Thread(new Runnable() {
            @Override
            public void run() {
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

                //Collections.sort(recordingsList); // TODO sort items in the list.
            }
        }).start();


        return recordingsList;
    }
    public static void deleteFile(File file){
        final File mFile = file;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mFile.exists()) {
                    mFile.delete();
                }
            }
        }).start();
    }
    public static void deleteFile(String filePath){
        final String fileName = filePath;
        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = new File(fileName);
                if (file.exists()) {
                    file.delete();

                }
            }
        }).start();

    }
}

