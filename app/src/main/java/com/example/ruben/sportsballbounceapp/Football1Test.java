package com.example.ruben.sportsballbounceapp;

import android.content.pm.PackageManager;

import android.graphics.Color;

import java.io.IOException;
import java.io.FileWriter;

import android.media.MediaPlayer;
import android.media.MediaRecorder;

import android.os.Environment;
import android.os.Bundle;
import android.os.Handler;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.view.View;

import android.widget.Button;
import android.widget.Toast;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

//TODO Zoek de tijd waarop piek 1 zich voordoet adhv piek detectie
//TODO Zoek de tijd waarop piek 2 zich voordoet adhv piek detectie
//TODO dropdown voor beton/gras
//TODO dropdown Meting 1-5
//TODO opslaan van de metingen adhv de juiste naam (Datum (J-M-D) - B/G - M1/M2/M3/M4/M5)


public class Football1Test extends AppCompatActivity {

    Button buttonStart, buttonStop, buttonPlayLastRecordAudio, buttonStopPlayingRecording, buttonUpdate ;
    String AudioSavePathInDevice    = "SportsballsBounceApp/";
    String csvFile = "SportsballsBounceApp/file/test.txt";
    String RandomAudioFileName      = "FootBallTest";
    MediaRecorder   mediaRecorder ;
    MediaPlayer     mediaPlayer ;
    Random random ;
    public static final int RequestPermissionCode = 1;
    private PointsGraphSeries<DataPoint> series;
    private LineGraphSeries<DataPoint> series2;

    int Amplitude =0;
    int intTime = 0;
    long longTime = 0;
    long startTime = 0;
    List<Integer> listAmplitude = new ArrayList<Integer>();
    List<Integer> listTime = new ArrayList<Integer>();

    Handler handler= new Handler();
    boolean SoundRecording=false;

    public Football1Test() throws IOException {
    }

    public void MediaRecorderReady(){
        mediaRecorder=new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_football1_test);

        buttonStart = (Button) findViewById(R.id.buttonStart);
        buttonStop = (Button) findViewById(R.id.buttonStop);
        buttonUpdate = (Button) findViewById(R.id.buttonUpdate);

        buttonPlayLastRecordAudio = (Button) findViewById(R.id.button3);
        buttonStopPlayingRecording = (Button)findViewById(R.id.button4);

        TextView textTime = (TextView) findViewById(R.id.TextTime);
        TextView textAmplitude = (TextView) findViewById(R.id.TextAmplitude);

        buttonStop.setEnabled(false);
        buttonPlayLastRecordAudio.setEnabled(false);
        buttonStopPlayingRecording.setEnabled(false);

        random = new Random();

        final Runnable updateSoundLevel = new Runnable() {
            @Override
            public void run() {
                if (SoundRecording==true){
                    handler.postDelayed(this, 0);

                    Amplitude = mediaRecorder.getMaxAmplitude();
                    longTime= System.currentTimeMillis()-startTime;
                    int intTime=(int) longTime;

//TODO Store time and amplitude data in file
                    listAmplitude.add(Amplitude);
                    listTime.add(intTime);

                    //textTime.setText("Amplitude:  "+String.valueOf(Amplitude));
                    //textAmplitude.setText("Time:  "+String.valueOf(intTime)+"ms");

                    addEntry(intTime,Amplitude);

                }
                else{
                    //intTime=0;
                    //Amplitude =0;
                }
            }
        };

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                GraphView graph = (GraphView) findViewById(R.id.graph);

                graph.removeAllSeries();
                // customize a little bit viewport
                Viewport viewport = graph.getViewport();
                viewport.setYAxisBoundsManual(true);
                viewport.setXAxisBoundsManual(true);
                viewport.setMinY(0);
                viewport.setMaxY(20000);
                viewport.setMinX(0);
                viewport.setMaxX(4000);
                viewport.setScrollable(true);
                graph.getViewport().setScalable(true); // enables horizontal zooming and scrolling
                graph.getViewport().setScalableY(false); // enables vertical zooming and scrolling

                // data pointgraph
                series = new PointsGraphSeries<DataPoint>();
                graph.addSeries(series);
                series.setShape(PointsGraphSeries.Shape.POINT);
                series.setSize(4);
                series.setColor(Color.RED);
                PointsGraphSeries<DataPoint> series = new PointsGraphSeries<>(new DataPoint[] {
                        new DataPoint(0, 0)
                });

                // data linegraph
                series2 = new LineGraphSeries<DataPoint>();
                graph.addSeries(series2);
                series2.setThickness(2);
                LineGraphSeries<DataPoint> series2 = new LineGraphSeries<>(new DataPoint[] {
                        new DataPoint(0, 0)
                });

                if(checkPermission())
                {
                    AudioSavePathInDevice = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + CreateRandomAudioFileName(5) + "AudioRecording.3gp";
                    MediaRecorderReady();
                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    startTime= System.currentTimeMillis();
                    buttonStart.setEnabled(false);
                    buttonStop.setEnabled(true);
                    Toast.makeText(Football1Test.this, "Recording started",Toast.LENGTH_LONG).show();
                    SoundRecording=true;
                }
                else
                {
                    requestPermission();
                }

                handler.postDelayed(updateSoundLevel, 0);

            }
        });

        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaRecorder.stop();
                buttonStop.setEnabled(false);
                buttonPlayLastRecordAudio.setEnabled(true);
                buttonStart.setEnabled(true);
                buttonStopPlayingRecording.setEnabled(false);
                intTime=0;
                Amplitude =0;

                Toast.makeText(Football1Test.this, "Stop Recording", Toast.LENGTH_LONG).show();
                SoundRecording=false;

            }
        });

        /*buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TextView t = (TextView) findViewById(R.id.textView2);
                int Amplitude = mediaRecorder.getMaxAmplitude();
                t.setText("The sound level is:  "+String.valueOf(Amplitude));

            }
        });*/

        buttonPlayLastRecordAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) throws IllegalArgumentException,
                    SecurityException, IllegalStateException {

                buttonStop.setEnabled(false);
                buttonStart.setEnabled(false);
                buttonStopPlayingRecording.setEnabled(true);

                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(AudioSavePathInDevice);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaPlayer.start();
                Toast.makeText(Football1Test.this, "Recording Playing", Toast.LENGTH_LONG).show();

            }
        });
        buttonStopPlayingRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonStop.setEnabled(false);
                buttonStart.setEnabled(true);
                buttonStopPlayingRecording.setEnabled(false);
                buttonPlayLastRecordAudio.setEnabled(true);

                if(mediaPlayer != null){
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    MediaRecorderReady();
                }
            }
        });

    }

    private void addEntry(int a, int b) {
        // here, we choose to display max 100 points on the viewport and we scroll to end
        series.appendData(new DataPoint(a, b), true, 500000);
        series2.appendData(new DataPoint(a, b), true, 500000);
    }

    public String CreateRandomAudioFileName(int string){
        StringBuilder stringBuilder = new StringBuilder( string );
        int i = 0 ;
        while(i < string )
        {
            stringBuilder.append(RandomAudioFileName.charAt(random.nextInt(RandomAudioFileName.length())));
            i++ ;
        }
        return stringBuilder.toString();
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(Football1Test.this, new String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length> 0) {
                    boolean StoragePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(Football1Test.this, "Permission Granted",Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(Football1Test.this,"Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }
}


