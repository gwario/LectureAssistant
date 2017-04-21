package at.ameise.lectureassistant.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
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

import java.util.Locale;
import java.util.Stack;

import at.ameise.lectureassistant.R;
import at.ameise.lectureassistant.content.adapter.TimelineEntryAdapter;
import at.ameise.lectureassistant.content.database.LectureAssistantDatabaseHelper;
import at.ameise.lectureassistant.content.database.TimelineEntryDAO;
import at.ameise.lectureassistant.content.model.TimelineEntry;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.speech.RecognizerIntent.EXTRA_LANGUAGE;
import static android.speech.RecognizerIntent.EXTRA_LANGUAGE_MODEL;
import static android.speech.RecognizerIntent.LANGUAGE_MODEL_FREE_FORM;
import static android.speech.SpeechRecognizer.ERROR_AUDIO;
import static android.speech.SpeechRecognizer.ERROR_CLIENT;
import static android.speech.SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS;
import static android.speech.SpeechRecognizer.ERROR_NETWORK;
import static android.speech.SpeechRecognizer.ERROR_NETWORK_TIMEOUT;
import static android.speech.SpeechRecognizer.ERROR_NO_MATCH;
import static android.speech.SpeechRecognizer.ERROR_RECOGNIZER_BUSY;
import static android.speech.SpeechRecognizer.ERROR_SERVER;
import static android.speech.SpeechRecognizer.ERROR_SPEECH_TIMEOUT;

public class MainActivity extends AppCompatActivity implements RecognitionListener {

    private static final int PERMISSION_REQUEST_RECORD_AUDIO = 123;

    private LectureAssistantDatabaseHelper mDbHelper;
    private SQLiteDatabase db;

    private SpeechRecognizer speechRecognizer;
    private boolean isRecognizing;

    private boolean shouldStopRecognizing;

    @BindView(R.id.activity_main_results)
    RecyclerView rvResults;
    @BindView(R.id.activity_main_button_start_stop)
    FloatingActionButton bStartStop;
    private Stack<TimelineEntry> results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mDbHelper = new LectureAssistantDatabaseHelper(this);
        db = mDbHelper.getWritableDatabase();

        ButterKnife.bind(this);

        results = new Stack<>();

        rvResults.setLayoutManager(new LinearLayoutManager(this));
        rvResults.setAdapter(new TimelineEntryAdapter(results));

        results.addAll(TimelineEntryDAO.load(db));
        rvResults.getAdapter().notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(SpeechRecognizer.isRecognitionAvailable(this)) {

            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
            speechRecognizer.setRecognitionListener(this);

        } else {

            Toast.makeText(this, "Speech to text is not available on your system!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        speechRecognizer.stopListening();
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

                shouldStopRecognizing = true;

            } else {

                shouldStopRecognizing = false;
                bStartStop.setImageResource(R.drawable.ic_mic_off_black_24dp);
                isRecognizing = true;
                startListening();

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

    /**
     * Start recognizer...
     */
    private void startListening() {

        speechRecognizer.startListening(new Intent()
                .setAction(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                .putExtra(EXTRA_LANGUAGE, Locale.getDefault())
                .putExtra(EXTRA_LANGUAGE_MODEL, LANGUAGE_MODEL_FREE_FORM));
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.d("stt","onReadyForSpeech");
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.d("stt","onBeginningOfSpeech");
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        //Log.d("stt","onRmsChanged");
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.d("stt","onBufferReceived");
    }

    @Override
    public void onEndOfSpeech() {
        Log.d("stt","onEndOfSpeech");
    }

    @Override
    public void onError(int error) {
        Log.d("stt","onError");
        switch (error) {
            case ERROR_AUDIO:
                Log.e("stt", "Audio recording error.");
                break;
            case ERROR_CLIENT:
                Log.e("stt", "Other client side errors.");
                break;
            case ERROR_INSUFFICIENT_PERMISSIONS:
                Log.e("stt", "Insufficient permissions.");
                break;
            case ERROR_NETWORK:
                Log.e("stt", "Other network related errors.");
                break;
            case ERROR_NETWORK_TIMEOUT:
                Log.e("stt", "Network operation timed out.");
                break;
            case ERROR_NO_MATCH:
                Log.e("stt", "No recognition result matched.");
                repeaatListeningOrCancel();
                break;
            case ERROR_RECOGNIZER_BUSY:
                Log.e("stt", "RecognitionService busy.");
                break;
            case ERROR_SERVER:
                Log.e("stt", "Server sends error status.");
                break;
            case ERROR_SPEECH_TIMEOUT:
                Log.e("stt", "No speech input.");
                break;
            default:
                Log.e("stt", "Undefined error code.");
        }
    }

    @Override
    public void onResults(Bundle results) {
        Log.d("stt","onResults");

        if(results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) != null
        && !results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).isEmpty()) {

            String recognitionResultString = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0);

            Log.d("stt", "Adding result: "+recognitionResultString);
            TimelineEntry newEntry = new TimelineEntry();
            newEntry.setText(recognitionResultString);
            this.results.push(newEntry);
            TimelineEntryDAO.insert(db, newEntry);
            rvResults.getAdapter().notifyDataSetChanged();
            rvResults.smoothScrollToPosition(rvResults.getAdapter().getItemCount() - 1);
        }

        repeaatListeningOrCancel();
    }

    private void repeaatListeningOrCancel() {
        if(shouldStopRecognizing) {

            Log.d("stt", "Breaking recognition loop.");
            speechRecognizer.cancel();
            isRecognizing = false;
            bStartStop.setImageResource(R.drawable.ic_mic_black_24dp);

        } else {

            continueListening();
        }
    }

    private void continueListening() {
        Log.v("stt", "Locale.getDefault():"+ Locale.getDefault());
        startListening();
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        Log.d("stt","onPartialResults");
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        Log.d("stt","onEvent");
    }

    @Override
    protected void onDestroy() {

        mDbHelper.close();

        if(speechRecognizer != null) {
            speechRecognizer.destroy();
            speechRecognizer = null;
        }

        super.onDestroy();
    }
}
