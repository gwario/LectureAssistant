package at.ameise.lectureassistant;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final int REQ_CODE_RECORD = 123;
    private TextView tvResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        tvResults = (TextView) findViewById(R.id.activity_main_textview_results);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //TODO bind RecgnitionService
    }

    @Override
    protected void onPause() {
        super.onPause();

        //TODO stop recording/undbind
    }

    public void onClickStart(View v) {

        /*TODO service.onStartRecording(..., new RecognitionService.Callback() {
        1) void	readyForSpeech(Bundle params)
        2) tell the user that recognition is starting from now on
        3) void	partialResults(Bundle partialResults) display/process/whatever partial results
        ...


        1) void	readyForSpeech(Bundle params)
        2) tell the user that recognition is starting from now on
        3) void	results(Bundle results)
        ...

        });*/

        //Toast.makeText(this, "Start clicked", Toast.LENGTH_SHORT).show();
        /*tvResults.setText("");

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Lecture is recording at this very moment!!");

        try {

            startActivityForResult(intent, REQ_CODE_RECORD);

        } catch (ActivityNotFoundException a) {

            Toast.makeText(this, "Oh nooooo. Error!", Toast.LENGTH_SHORT).show();
        }*/
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQ_CODE_RECORD) {

            Log.v("results", "Recording stopped.");

            if(resultCode == RESULT_OK && data != null) {

                if(data.hasExtra(RecognizerIntent.EXTRA_RESULTS)) {

                    Log.d("results", data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).toString());
                    for(String result : data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS))
                    tvResults.setText(result+"\n");

                } else {

                    Log.e("results", "No result strings!");
                }

            } else {

                Log.e("results", "Result not ok!");
            }
        }
    }*/
}
