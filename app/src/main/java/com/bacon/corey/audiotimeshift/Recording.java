package com.bacon.corey.audiotimeshift;

import android.media.MediaPlayer;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import java.util.Random;

public class Recording implements Comparable<Recording>, Serializable {
    private File recording;
    private Date date;
    private String fileFormat;
    private int fileLength;
    private int fileSize;
    private int color = 1;

    public Recording(File recording, Date date) {
        this.recording = recording;
        this.date = date;
        findColor();
    }

    public String getTitleString() {
        return recording.getName();
    }

    public String getDateString() {
        return date.toString();
    }

    public Date getDate() {
        return date;
    }

    public long getFileSize() {
        long fileSize = recording.length();
        return recording.length();
    }

    public String getFileSizeString() {
        Long mb = recording.length() / (1024 * 1024);
        Long kb = recording.length() / (1024);
        if (mb < 0.1) {
            return kb.toString() + " KB";
        } else {
            return mb.toString() + " MB";
        }
    }

    public File getFile() {
        return recording;
    }

    public String getFilePathString() {
        return recording.getPath();
    }

    public static Comparator<Recording> recDateComp = new Comparator<Recording>() {
        @Override
        public int compare(Recording recording1, Recording recording2) {
            return 0;
        }
    };

    @Override
    public int compareTo(Recording rec) {
        return rec.getDate().compareTo(getDate());
    }

    public int getColor() {
        return color; // TODO

    }

    private int findColor() {
        color = new Random().nextInt(16 - 1) + 1;
        return color; // TODO
    }


    public int getNumChannels(RandomAccessFile randomAccessFile) {
        int channels = -1;
        try {
            byte[] bytes = new byte[2];
            randomAccessFile.seek(22);
            randomAccessFile.read(bytes);
            //channels =  (bytes[1] << 8) + bytes[0];

            channels = ((0xff & bytes[1]) << 8) |
                    ((0xff & bytes[0]));
        } catch (IOException e) {
            Log.e("Recording", "Error: " + e);
        }
        return channels;
    }

    public long getSampleRate(RandomAccessFile randomAccessFile) {
        long sampleRate = -1;
        try {
            byte[] bytes = new byte[4];
            randomAccessFile.seek(24);
            randomAccessFile.read(bytes);
            //sampleRate =  (bytes[3] << 24) + (bytes[2] << 16) + (bytes[1] << 8) + bytes[0];

            sampleRate = ((0xff & bytes[3]) << 24) |
                    ((0xff & bytes[2]) << 16) |
                    ((0xff & bytes[1]) << 8) |
                    ((0xff & bytes[0]));

        } catch (IOException e) {
            Log.e("Recording", "Error: " + e);
        }
        return sampleRate;

    }

    public int getBitsPerSample(RandomAccessFile randomAccessFile) {
        int bitsPerSample = -1;
        try {
            byte[] bytes = new byte[2];
            randomAccessFile.seek(34);
            randomAccessFile.read(bytes);

            bitsPerSample =
                    ((0xff & bytes[1]) << 8) |
                            ((0xff & bytes[0]));

        } catch (IOException e) {
            Log.e("Recording", "Error: " + e);
        }
        return bitsPerSample;
    }

    public long getByteRate() {
        long byteRate = -1;
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(recording, "rw");
            byteRate = getNumChannels(randomAccessFile) * getSampleRate(randomAccessFile) * getBitsPerSample(randomAccessFile) / 8;

        } catch (IOException e) {
            Log.e("recording", "Error: " + e);
        }
        return byteRate;
    }

    public long getAudioLengthInSeconds() {
        long audioLength;
        try {
            long fSize = getFileSize();
            long fBR = getByteRate();
            audioLength = (fSize - 44) / fBR;

            if (audioLength != 0) {
                return audioLength;

            } else {

                    return -1;

            }
        }
         catch (ArithmeticException e) {
             return -1;

         }

    }
    public int getAudioLengthInBestFormat(){
        long recordingLen = getAudioLengthInSeconds();
        if (recordingLen < 60){
            return (int) Math.round((double) recordingLen);
        }
        else if (recordingLen >= 60 && recordingLen < 3600){
            return (int) Math.round((double) recordingLen)/60;

        }
        else if (recordingLen >= 3600){
            return (int) Math.round((double) recordingLen)/3600;

        }
        else{
            return -1;
        }
    }
    public String getAudioLengthInHMSFormat(){
        long recordingLen = getAudioLengthInSeconds();
        long timeUnParsed = recordingLen;
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

}

