package com.bacon.corey.audiotimeshift;

import android.app.Fragment;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RecordFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RecordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecordFragment extends Fragment {
    public File directory;
    AudioRecord audioRecord;
    int sampleRate = 44100;
    int channelConfiguration = AudioFormat.CHANNEL_IN_MONO;
    int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    File file;
    short channels = 1;
    int bitsPerSample = 16;
    ExtAudioRecorder extAudioRecord;

    // TODO: Rename and change types and number of parameters
    public static RecordFragment newInstance() {
        RecordFragment fragment = new RecordFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public RecordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_record, container, false);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(extAudioRecord != null){
            extAudioRecord.stop();
        }
        //extAudioRecord.release();
        //audioRecord.stop();
        Log.v("Record", "onDestroy called, Record stopped");
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

        public void record() {

            Log.v("Record", "Record called");
            extAudioRecord = ExtAudioRecorder.getInstanse(true);
            extAudioRecord.setOutputFile(Environment.getExternalStorageDirectory() + File.separator + "TimeShiftRecorder" + File.separator + DateFormat.getDateTimeInstance().format(new Date()) + ".wav");
            extAudioRecord.prepare();
            extAudioRecord.start();
            //record();
        }
        public void recordManual() {
            Log.v("Record", "Record started");


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
                int bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfiguration, audioEncoding)*2;
                audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                        sampleRate, channelConfiguration,
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

            writeWavHeader();

        }
        public void stop(){
            audioRecord.stop();

        }
        public void pause(){

        }
        public void sendMessage(){
            Log.v("broadcast sender", "message");
            Intent intent = new Intent(Constants.UPDATE_FILE_DATASET);
            intent.putExtra("message","refresh_data_set");
            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
        }
        public void writeWavHeader(){
            try {
                File pcmFile = new File(Environment.getExternalStorageDirectory() + File.separator + "TimeShiftRecorder" + File.separator + "Recording.pcm");
                File wavFile = new File(Environment.getExternalStorageDirectory() + File.separator + "TimeShiftRecorder" + File.separator + "Recording_test.wav");
                OutputStream os = new FileOutputStream(wavFile);
                BufferedOutputStream bos = new BufferedOutputStream(os);
                DataOutputStream out = new DataOutputStream(bos);
                FileInputStream fis = new FileInputStream(pcmFile);
                InputStream bis = new BufferedInputStream(fis);
                DataInputStream dis = new DataInputStream(bis);

                short mBitsPerSample = 16;
                long audioLength = fis.getChannel().size();
                long byteRate = sampleRate * bitsPerSample * channels/8;
                short format = 1;
                long totalDataLen = audioLength + 36;
                long longSampleRate = sampleRate;
                byte byteBitsPerSample = (byte) bitsPerSample;
                byte[] header = new byte[44];

                header[0] = 'R';  // RIFF/WAVE header
                header[1] = 'I';
                header[2] = 'F';
                header[3] = 'F';
                header[4] = (byte) (totalDataLen & 0xff);
                header[5] = (byte) ((totalDataLen >> 8) & 0xff);
                header[6] = (byte) ((totalDataLen >> 16) & 0xff);
                header[7] = (byte) ((totalDataLen >> 24) & 0xff);
                header[8] = 'W';
                header[9] = 'A';
                header[10] = 'V';
                header[11] = 'E';
                header[12] = 'f';  // 'fmt ' chunk
                header[13] = 'm';
                header[14] = 't';
                header[15] = ' ';
                header[16] = 16;  // 4 bytes: size of 'fmt ' chunk
                header[17] = 0;
                header[18] = 0;
                header[19] = 0;
                header[20] = 1;  // format = 1
                header[21] = 0;
                header[22] = (byte) channels;
                header[23] = 0;
                header[24] = (byte) (longSampleRate & 0xff);
                header[25] = (byte) ((longSampleRate >> 8) & 0xff);
                header[26] = (byte) ((longSampleRate >> 16) & 0xff);
                header[27] = (byte) ((longSampleRate >> 24) & 0xff);
                header[28] = (byte) (byteRate & 0xff);
                header[29] = (byte) ((byteRate >> 8) & 0xff);
                header[30] = (byte) ((byteRate >> 16) & 0xff);
                header[31] = (byte) ((byteRate >> 24) & 0xff);
                header[32] = (byte) (bitsPerSample/8*channels);  // block align
                header[33] = 0;
                header[34] = byteBitsPerSample;  // bits per sample
                header[35] = 0;
                header[36] = 'd';
                header[37] = 'a';
                header[38] = 't';
                header[39] = 'a';
                header[40] = (byte) (audioLength & 0xff);
                header[41] = (byte) ((audioLength >> 8) & 0xff);
                header[42] = (byte) ((audioLength >> 16) & 0xff);
                header[43] = (byte) ((audioLength >> 24) & 0xff);
                out.write(header, 0, 44);


                while (dis.available() > 0) {
                    out.write(dis.read());
                }
                dis.close();
                out.close();

            }catch(IOException e){
                Log.v("recordService", "IOException");
            }

        }
        public File getOutputFile(String fileName){

            directory = new File(Environment.getExternalStorageDirectory() + File.separator + Constants.DIRECTORY);

            if (directory.isDirectory()) {
                return file = new File(Environment.getExternalStorageDirectory() + File.separator + "TimeShiftRecorder" + File.separator + fileName + ".wav");
            }
            else{
                directory.mkdir();
                return file = new File(Environment.getExternalStorageDirectory() + File.separator + "TimeShiftRecorder" + File.separator + fileName + ".wav");

            }
        }




    }


