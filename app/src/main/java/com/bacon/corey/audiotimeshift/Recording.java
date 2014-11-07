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


}

