package ee.ut.phon.android.vova;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpPost;
import com.koushikdutta.async.http.AsyncHttpResponse;
import com.koushikdutta.async.http.body.MultipartFormDataBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import ee.ioc.phon.android.k6neleservice.R;
import ee.ioc.phon.android.k6neleservice.service.WebSocketRecognitionService;

public class MainActivity extends AppCompatActivity {

    TextView instructionalTextView;
    ImageButton recordImageButton;

    SpeechRecognizer sr;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        askForNecessaryPermissions();

        // Initialise GUI components.
        instructionalTextView = findViewById(R.id.instructionalTextView);
        recordImageButton = findViewById(R.id.imageButton);

        // Configure media player for our application
        mediaPlayer = new MediaPlayer();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mediaPlayer.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .setUsage(AudioAttributes.USAGE_ASSISTANT)
                            .build()
            );
        }

        // Configure speech recognizer for the Estonian language.
        sr = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        sr.setRecognitionListener(new SpeechInputRecognitionListener());
        Intent speechRecognition = new Intent(getApplicationContext(), WebSocketRecognitionService.class);
        speechRecognition.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognition.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "et-EE");

        recordImageButton.setOnClickListener(view -> {
            if (mediaPlayer!=null) {
                mediaPlayer.stop();
                mediaPlayer.reset();
            }
            if (sr!= null) {
                sr.cancel();
            }
            // Start speech recognition.
            sr.startListening(speechRecognition);
            // Change GUI text
            instructionalTextView.setText("Kuulan...");
            // Disable button and haptic feedback until users command is processed.
            recordImageButton.setHapticFeedbackEnabled(false);
            recordImageButton.setEnabled(false);
        });

        instructionalTextView.setText("Kuidas saan Teid aidata?");

        setGUIInitialState();
    }

    private void askForNecessaryPermissions() {
        String[] PERMISSIONS = {
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.INTERNET
        };

        boolean necessaryPermissionsMissing = false;
        for (String PERMISSION : PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), PERMISSION) == PackageManager.PERMISSION_GRANTED) {
                necessaryPermissionsMissing=true;
                break;
            }
        }

        if (necessaryPermissionsMissing) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PackageManager.PERMISSION_GRANTED);
        }
    }

    @Override
    protected void onStart() {
        init();
        setGUIInitialState();
        super.onStart();
    }

    @Override
    protected void onResume() {
        init();
        setGUIInitialState();
        super.onResume();
    }

    @Override
    protected void onPause() {
        cleanup();
        super.onPause();
    }

    @Override
    protected void onStop() {
        cleanup();
        super.onStop();
    }

    private void init() {
        if (mediaPlayer==null) {
            // Configure media player for our application
            mediaPlayer = new MediaPlayer();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mediaPlayer.setAudioAttributes(
                        new AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                                .setUsage(AudioAttributes.USAGE_ASSISTANT)
                                .build()
                );
            }
        }
        if (sr==null) {
            // Configure speech recognizer for the Estonian language.
            sr = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
            sr.setRecognitionListener(new SpeechInputRecognitionListener());
            Intent speechRecognition = new Intent(getApplicationContext(), WebSocketRecognitionService.class);
            speechRecognition.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            speechRecognition.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "et-EE");
        }
        setGUIInitialState();
    }

    private void cleanup() {
        if (mediaPlayer!=null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer=null;
        }
        if (sr!=null) {
            sr.destroy();
            sr=null;
        }
    }

    private void setGUIInitialState() {
        recordImageButton.setHapticFeedbackEnabled(true);
        recordImageButton.setEnabled(true);
    }

    protected void determineAction(String speechAsText) {
        // https://www.baeldung.com/httpurlconnection-post
        try {
            JSONObject json = new JSONObject();
            json.put("speech", speechAsText);

            URL url = new URL(getString(R.string.speech_analyzer_api_url));
            //url = new URL(url.getProtocol(), url.getHost(), 8000, url.getFile());
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);
            con.setDoInput(true);

            OutputStream os = con.getOutputStream();
            os.write(json.toString().getBytes("UTF-8"));
            os.close();

            try(BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                JSONObject json2 = new JSONObject(response.toString());
                System.out.println(json2);
                executeAction(json2);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            giveFeedback(Feedback.getSpeechAnalysisServerResponseErrorFeedback());
        }
    }

    protected void executeAction(JSONObject json) {
        try {
            if (json.get("action")==null) {
                giveFeedback(Feedback.getUnknownCommandFeedback());
            } else {
                switch (json.getString("action")) {
                    case "start_media":
                        giveFeedback(Feedback.getStartMediaCommandSuccessfulFeedback());
                        break;
                    case "stop_media":
                        giveFeedback(Feedback.getStopMediaCommandSuccessfulFeedback());
                        break;
                    case "search":
                        giveFeedback(Feedback.getSearchCommandSuccessful(json.getString("query")));
                        break;
                    case "reminder":
                        giveFeedback(Feedback.getReminderCommandSuccessful(json.getString("value")));
                        break;
                    case "increase_volume":
                        giveFeedback(Feedback.getIncreaseVolumneCommandSuccessful(json.getInt("percentage")));
                        break;
                    case "decrease_volume":
                        giveFeedback(Feedback.getDecreaseVolumeCommandSuccessful(json.getInt("percentage")));
                        break;
                    default:
                        giveFeedback(Feedback.getUnknownCommandFeedback());
                        break;
                }
            }
        } catch (JSONException e) {
            giveFeedback(Feedback.getSpeechAnalysisServerResponseErrorFeedback());
        }
    }

    protected void giveFeedback(Feedback feedback) {
        if (feedback.canAudibleFeedbackBeGiven) {
            try {
                URL url = new URL(getString(R.string.voice_synthesizer_api_url) + "?tekst=\"" + feedback.getFeedback() + "\"");
                URLConnection urlConnection = url.openConnection();
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();

                int ch;
                StringBuilder body = new StringBuilder();
                while ((ch = inputStream.read()) != -1) {
                    body.append((char) ch);
                }
                JSONObject json = new JSONObject(body.toString());
                System.out.println(json);
                mediaPlayer.reset();
                mediaPlayer.setDataSource((String) json.get("mp3url"));
                mediaPlayer.prepare(); // might take long! (for buffering, etc)
                mediaPlayer.start();
            } catch (IOException e) {
                feedback.setFeedback(feedback.getFeedback(), feedback.getShortenedFeedback() + "\nNB! Tehiskõne serveriga ühendus puudub.");
            } catch (JSONException e) {
                feedback.setFeedback(feedback.getFeedback(), feedback.getShortenedFeedback() + "\nNB! Ei saa esitada tehiskõne.");
            } catch (RuntimeException e) {
                feedback.setFeedback(feedback.getFeedback(), feedback.getShortenedFeedback() + "\nNB! Ei saa esitada tehiskõne.");
            }
        }

        runOnUiThread(() -> instructionalTextView.setText(feedback.getShortenedFeedback()));
        runOnUiThread(() -> setGUIInitialState());
    }



    protected class SpeechInputRecognitionListener implements RecognitionListener {

        // Error codes.
        private final int ERR_NETWORK = 2;
        private final int ERR_AUDIO = 3;
        private final int ERR_SERVER = 4;
        private final int ERR_CLIENT = 5;

        private final int TRANSCRIBER_RETURNED_NO_RESULTS=7;

        @Override
        public void onReadyForSpeech(Bundle params) {}

        @Override
        public void onBeginningOfSpeech() {}

        @Override
        public void onEndOfSpeech() {}

        /**
         * We process all possible SpeechRecognizer errors. Most of them
         * are generated by our implementation, others can be generated by the
         * framework, e.g. ERROR_CLIENT results from
         * "stopListening called with no preceding startListening".
         *
         * @param errorCode SpeechRecognizer error code
         */
        @Override
        public void onError(final int errorCode) {
            switch (errorCode) {
                case ERR_AUDIO:
                    giveFeedback(Feedback.getMicrophoneErrorFeedback());
                    break;
                case ERR_NETWORK:
                    giveFeedback(Feedback.getNoInternetConnectionErrorFeedback());
                    break;
                case ERR_CLIENT:
                    giveFeedback(Feedback.getDeviceNetworkErrorFeedback());
                    break;
                case ERR_SERVER:
                    giveFeedback(Feedback.getTranscriptonServerErrorFeedback());
                    break;
                case TRANSCRIBER_RETURNED_NO_RESULTS:
                    Feedback feedback = Feedback.getUnknownCommandFeedback();
                    feedback.setCanAudibleFeedbackBeGiven(false);
                    giveFeedback(feedback);
                    break;
                default:
                    giveFeedback(Feedback.getUnknownErrorFeedback());
                    break;
            }
        }

        @Override
        public void onPartialResults(final Bundle bundle) {
            ArrayList<String> results = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if (results != null && !results.isEmpty()) {
                System.out.println(results);
                new Thread(() -> {
                    determineAction(results.get(0));
                }).start();
            } else {
                new Thread(() -> {
                    giveFeedback(Feedback.getNoTranscriptionResultsFeedback());
                }).start();
            }
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
            // TODO: future work: not sure how this can be generated by the service
        }

        @Override
        public void onResults(final Bundle bundle) {
            ArrayList<String> results = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if (results != null && !results.isEmpty()) {
                new Thread(() -> {
                    determineAction(results.get(0));
                }).start();
            } else {
                new Thread(() -> {
                    giveFeedback(Feedback.getNoTranscriptionResultsFeedback());
                }).start();
            }
        }

        @Override
        public void onRmsChanged(float rmsdB) {}

        @Override
        public void onBufferReceived(byte[] buffer) {}
    }
}