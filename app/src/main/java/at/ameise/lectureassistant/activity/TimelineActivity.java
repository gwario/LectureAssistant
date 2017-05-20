package at.ameise.lectureassistant.activity;

import android.Manifest;
import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.microsoft.cognitiveservices.speechrecognition.DataRecognitionClient;
import com.microsoft.cognitiveservices.speechrecognition.ISpeechRecognitionServerEvents;
import com.microsoft.cognitiveservices.speechrecognition.RecognitionResult;
import com.microsoft.cognitiveservices.speechrecognition.RecognitionStatus;
import com.microsoft.cognitiveservices.speechrecognition.SpeechAudioFormat;
import com.microsoft.cognitiveservices.speechrecognition.SpeechRecognitionMode;
import com.microsoft.cognitiveservices.speechrecognition.SpeechRecognitionServiceFactory;

import at.ameise.lectureassistant.R;
import at.ameise.lectureassistant.content.adapter.TimelineEntryAdapter;
import at.ameise.lectureassistant.content.database.LectureAssistantDatabase;
import at.ameise.lectureassistant.content.model.TimelineEntry;
import at.ameise.lectureassistant.content.viewmodel.TimelineViewModel;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TimelineActivity extends LifecycleActivity implements ISpeechRecognitionServerEvents {

    private static final int PERMISSION_REQUEST_RECORD_AUDIO = 123;

    private DataRecognitionClient dataRecognitionClient = null;
    private AudioRecord mRecorder;

    private LectureAssistantDatabase db;

    private TimelineViewModel model;
    private TimelineEntryAdapter adapter;

    private boolean isRecgrding;
    @BindView(R.id.activity_main_results)
    RecyclerView rvResults;

    @BindView(R.id.activity_main_button_start_stop)
    FloatingActionButton bStartStop;
    private int minBuffSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.wtf("wtf", "wtf");
        setContentView(R.layout.activity_timeline);

        ButterKnife.bind(this);

        adapter = new TimelineEntryAdapter();

        rvResults.setLayoutManager(new LinearLayoutManager(this));
        rvResults.setAdapter(adapter);

        model = ViewModelProviders.of(this).get(TimelineViewModel.class);
        model.getTimelineEntries(this).observe(this, timelineEntries -> {
            adapter.setData(timelineEntries);
        });

        //TODO create a microphone recording service

        this.dataRecognitionClient = SpeechRecognitionServiceFactory.createDataClient(
                this,
                SpeechRecognitionMode.LongDictation,
                "en-gb",
                this,
                getString(R.string.primary_key));

        SpeechAudioFormat format = SpeechAudioFormat.create16BitPCMFormat(16000);
        format.ChannelCount = 1;
        dataRecognitionClient.sendAudioFormat(format);

        minBuffSize = AudioRecord.getMinBufferSize(16000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

        bStartStop.setOnClickListener(v -> {
            onClickStartStop(v);
        });

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                //TODO

            } else {

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_REQUEST_RECORD_AUDIO);
            }
        }
    }

    public void onClickStartStop(View v) {

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                //TODO

            } else {

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_REQUEST_RECORD_AUDIO);
            }

        } else {

            if(mRecorder == null || mRecorder.getRecordingState() == AudioRecord.RECORDSTATE_STOPPED) {

                mRecorder = new AudioRecord.Builder()
                        .setAudioSource(MediaRecorder.AudioSource.MIC)
                        .setAudioFormat(new AudioFormat.Builder()
                                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                                .setSampleRate(16000)
                                .setChannelMask(AudioFormat.CHANNEL_IN_MONO)
                                .build())
                        .setBufferSizeInBytes(2*minBuffSize)
                        .build();

                mRecorder.startRecording();
                isRecgrding = true;

                //TODO make async task
                //improve code quality
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        byte[] bug = new byte[minBuffSize];

                        while (isRecgrding) {

                            int count = mRecorder.read(bug, 0, minBuffSize);

                            if(count >= 0)
                                dataRecognitionClient.sendAudio(bug, count);
                            else
                                Log.e("rec", "Error code: "+count);
                        }
                        dataRecognitionClient.endAudio();
                        mRecorder.stop();
                        mRecorder.release();
                        mRecorder = null;
                    }
                }).start();

            } else {

                isRecgrding = false;
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == PERMISSION_REQUEST_RECORD_AUDIO) {

            for(int i = 0; i < permissions.length; i++) {

                if(permissions[i].equals(Manifest.permission.RECORD_AUDIO) && grantResults[i] == PackageManager.PERMISSION_DENIED) {

                    Toast.makeText(this, "The audio recording permission is mandatory for this app to function!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onPartialResponseReceived(final String response) {
        Log.d("stt","onPartialResponseReceived: response="+response);
    }

    @Override
    public void onFinalResponseReceived(RecognitionResult recognitionResult) {
        Log.d("stt","onFinalResponseReceived");

        Log.d("stt", "RecognitionStatus="+recognitionResult.RecognitionStatus.name());

        if (recognitionResult.RecognitionStatus != RecognitionStatus.EndOfDictation) {
            Log.d("stt", "RecognitionStatus: " + recognitionResult.RecognitionStatus.getValue());
            Log.d("stt", "Results: ");
            for (int i = 0; i < recognitionResult.Results.length; i++) {
                Log.d("stt", "[" + i + "]" + " Confidence=" + recognitionResult.Results[i].Confidence + " Text=\"" + recognitionResult.Results[i].DisplayText + "\"");
            }

            TimelineEntry newEntry = new TimelineEntry();
            //TODO Results does not always have >0 elements so npe here
            newEntry.setText(recognitionResult.Results[0].DisplayText);
            model.storeTimelineEntries(this, newEntry);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    rvResults.getAdapter().notifyDataSetChanged();
                    //TODO do this after the dataset change was loaded
                    rvResults.smoothScrollToPosition(Math.max(0, rvResults.getAdapter().getItemCount() - 1));
                }
            });
        }
    }

    @Override
    public void onIntentReceived(String payload) {
        Log.d("stt","onIntentReceived: payload="+payload);
    }

    @Override
    public void onError(final int errorCode, final String response) {
        Log.e("stt","onError: errorCode="+errorCode+", response="+response);
    }

    @Override
    public void onAudioEvent(boolean recording) {
        Log.d("stt","onAudioEvent: recording="+recording);
    }
}
