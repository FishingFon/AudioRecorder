package com.bacon.corey.audiotimeshift;

import java.io.File;
import java.util.Date;

public class Recording {
    private File recording;
    private Date date;
    private String fileFormat;
    private int fileLength;
    private int fileSize;

    public Recording(File recording, Date date){
        this.recording = recording;
        this.date = date;
    }

    public String getTitleString(int position){
        return recording.getName();
    }
    public String getDateString(int position){
        return date.toString();
    }
    public String getLengthString(int position){
        return Integer.toString(fileLength);
    }
    public String getSizeString(int position){
        return Integer.toString(fileSize);
    }

}

