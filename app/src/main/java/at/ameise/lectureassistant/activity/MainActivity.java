package at.ameise.lectureassistant.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.microsoft.cognitiveservices.speechrecognition.ISpeechRecognitionServerEvents;
import com.microsoft.cognitiveservices.speechrecognition.MicrophoneRecognitionClient;
import com.microsoft.cognitiveservices.speechrecognition.RecognitionResult;
import com.microsoft.cognitiveservices.speechrecognition.RecognitionStatus;
import com.microsoft.cognitiveservices.speechrecognition.SpeechRecognitionMode;
import com.microsoft.cognitiveservices.speechrecognition.SpeechRecognitionServiceFactory;

import java.util.Stack;

import at.ameise.lectureassistant.R;
import at.ameise.lectureassistant.content.adapter.TimelineEntryAdapter;
import at.ameise.lectureassistant.content.database.LectureAssistantDatabaseHelper;
import at.ameise.lectureassistant.content.database.TimelineEntryDAO;
import at.ameise.lectureassistant.content.model.TimelineEntry;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements ISpeechRecognitionServerEvents {

    private static final int PERMISSION_REQUEST_RECORD_AUDIO = 123;

    MicrophoneRecognitionClient micClient = null;

    private LectureAssistantDatabaseHelper mDbHelper;
    private SQLiteDatabase db;

    private boolean isRecognizing;

    @BindView(R.id.activity_main_results)
    RecyclerView rvResults;
    @BindView(R.id.activity_main_button_start_stop)
    FloatingActionButton bStartStop;
    private Stack<TimelineEntry> results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.wtf("wtf", "wtf");
        setContentView(R.layout.activity_main);

        mDbHelper = new LectureAssistantDatabaseHelper(this);
        db = mDbHelper.getWritableDatabase();

        ButterKnife.bind(this);

        results = new Stack<>();

        rvResults.setLayoutManager(new LinearLayoutManager(this));
        rvResults.setAdapter(new TimelineEntryAdapter(results));

        results.addAll(TimelineEntryDAO.load(db));
        rvResults.getAdapter().notifyDataSetChanged();

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

            if(isRecognizing) {

                if (null != micClient) {
                    micClient.endMicAndRecognition();
                }
                bStartStop.setImageResource(R.drawable.ic_mic_black_24dp);
                isRecognizing = false;

            } else {

                bStartStop.setImageResource(R.drawable.ic_mic_off_black_24dp);

                if (this.micClient == null) {

                    this.micClient = SpeechRecognitionServiceFactory.createMicrophoneClient(
                            this,
                            SpeechRecognitionMode.LongDictation,
                            "en-us",
                            this,
                            getString(R.string.primary_key));
                }

                micClient.startMicAndRecognition();

                isRecognizing = true;
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
            Log.d("stt", "RecognitionStatus: "+recognitionResult.RecognitionStatus.getValue());
            Log.d("stt", "Results: ");
            for (int i = 0; i < recognitionResult.Results.length; i++) {
                Log.d("stt", "[" + i + "]" + " Confidence=" + recognitionResult.Results[i].Confidence + " Text=\"" + recognitionResult.Results[i].DisplayText + "\"");
            }

            if(recognitionResult.RecognitionStatus == RecognitionStatus.RecognitionSuccess) {

                TimelineEntry newEntry = new TimelineEntry();
                newEntry.setText(recognitionResult.Results[0].DisplayText);
                this.results.push(newEntry);
                TimelineEntryDAO.insert(db, newEntry);
                rvResults.getAdapter().notifyDataSetChanged();
                rvResults.smoothScrollToPosition(rvResults.getAdapter().getItemCount() - 1);
            }
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

        if (!recording) {
            if (null != micClient) {
                micClient.endMicAndRecognition();
            }
            isRecognizing = false;
        }
    }
}
