package com.bacon.corey.audiotimeshift;

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

public class Recording implements Comparable<Recording>, Serializable{
    private File recording;
    private Date date;
    private String fileFormat;
    private int fileLength;
    private int fileSize;

    public Recording(File recording, Date date){
        this.recording = recording;
        this.date = date;
    }

    public String getTitleString(){
        return recording.getName();
    }
    public String getDateString(){
        return date.toString();
    }
    public Date getDate(){
        return date;
    }

    public String getLengthString(){
        return Integer.toString(fileLength);
    }
    public String getSizeString(){
        Long mb = recording.length()/(1024*1024);
        Long kb = recording.length()/(1024);
        if(mb < 0.1){
            return kb.toString() + " KB";
        }
        else{
            return mb.toString() + " MB";
        }
    }
    public File getFile(){
        return recording;
    }
    public String getFilePathString(){
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

}

