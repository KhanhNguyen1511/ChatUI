package com.example.mrm82.chatui;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.provider.AlarmClock;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.mrm82.chatui.Adapter.ChatMessageAdapter;
import com.example.mrm82.chatui.Pojo.ChatMessage;

import java.text.DateFormat;
import java.util.ArrayList;
import ai.api.AIListener;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.model.Result;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity implements AIListener {


    private ListView mListView;
    private FloatingActionButton mButtonSend;
    private EditText mEditTextMessage;
    private ImageView mImageView;
    private ChatMessageAdapter mAdapter;
    private TextView mTextView;
    private AIService   aiService;

    final AIConfiguration config = new AIConfiguration("83972e5951384fd88da7bfe83dcc8033",
            AIConfiguration.SupportedLanguages.English,
            AIConfiguration.RecognitionEngine.System);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.listView);
        mButtonSend = (FloatingActionButton) findViewById(R.id.btn_send);
        mEditTextMessage = (EditText) findViewById(R.id.et_message);
        mImageView = (ImageView) findViewById(R.id.iv_image);
        mAdapter = new ChatMessageAdapter(this, new ArrayList<ChatMessage>());
        mListView.setAdapter(mAdapter);
        mTextView = (TextView) findViewById(R.id.text);
        aiService = AIService.getService(this, config);
        aiService.setListener(this);
        mimicOtherMessage("What can i do for you ?");
//code for sending the message
        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mEditTextMessage.getText().toString();
                sendMessage(message);
                mEditTextMessage.setText("");
                mListView.setSelection(mAdapter.getCount() - 1);

                if (message.contains("call") || message.contains("dialer")) {
                    mimicOtherMessage("I'll  open your dialer");
                    Call(message);

                } else if ( message.contains("message")) {
                    mimicOtherMessage("Ok. Launching the SMS app...");
                    Text();

                } else if (message.contains("camera")) {
                    mimicOtherMessage("Opening camera...");
                    OpenCamera();

                } else if (message.contains("music") || message.contains("play")) {
                    mimicOtherMessage("Launching Music player...");
                    OpenMusicPlayer();

                } else if (message.contains("alarm") ) {
                    mimicOtherMessage("Moving to the Alarm app ...");
                    SetAlarm();

                } else if (message.contains("timer")) {
                    mimicOtherMessage("I will open the timer interface for you...");
                    SetTimer();

                } else if (message.contains("search")) {
                    mimicOtherMessage("Searching for what you need...");
                    openWebPage(message);

                } else if ( message.contains("time")) {

                    Time();

                } else if (message.contains("date")) {

                    Date();

                } else if(message.contains("year")) {

                    Year();

                }else if(message.contains("day")){

                    Day();

                }else if(message.contains("month")){

                    Month();

                }else if (message.contains("bye") || message.contains("good bye")){

                    AutoClose();

                }

            }
        });
    }

    public void onMicroClick(View view) {

        aiService.startListening();
    }

    private void sendMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, true, false);
        mAdapter.add(chatMessage);

    }

    private void mimicOtherMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, false, false);
        mAdapter.add(chatMessage);

    }

    private void sendMessage() {
        ChatMessage chatMessage = new ChatMessage(null, true, true);
        mAdapter.add(chatMessage);

        mimicOtherMessage();
    }

    private void mimicOtherMessage() {
        ChatMessage chatMessage = new ChatMessage(null, false, true);
        mAdapter.add(chatMessage);
    }

    public void OpenCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 0);
    }

    public void openWebPage(String query) {

        Intent intent = new Intent(Intent.ACTION_SEARCH);
        intent.putExtra(SearchManager.QUERY, query);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void SetTimer() {
        Intent intent = new Intent(AlarmClock.ACTION_SET_TIMER)
                .putExtra(AlarmClock.EXTRA_SKIP_UI, true);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void SetAlarm() {

        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM)
                .putExtra(AlarmClock.EXTRA_SKIP_UI, true);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void OpenMusicPlayer() {
        try {
            Intent intent = new Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            // music player not found
            start("Music Player not found!");
        }
    }

    public void Call(String userSpeechStr) {

        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" +""));

        if (callIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(callIntent);
        }
    }

    public void Text() {


        Intent textIntent = new Intent(Intent.ACTION_VIEW);
        textIntent.putExtra("sms_body", "");
        textIntent.setType("vnd.android-dir/mms-sms");
        if (textIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(textIntent);
        }

    }


    public void Time() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
        String formattedDate = sdf.format(cal.getTime());
        mimicOtherMessage("Current time: " + formattedDate);

    }

    public void Date(){
        Calendar cal = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("dd-MM ");
        String date_str = df.format(cal.getTime());
        mimicOtherMessage("Current date: " + date_str);
    }

    public void Day(){
        Calendar cal= Calendar.getInstance();
        DateFormat df= new SimpleDateFormat("dd");
        String day_str = df.format(cal.getTime());
        mimicOtherMessage("Current day: " + day_str);
    }

    public void Year(){
        Calendar cal= Calendar.getInstance();
        DateFormat df= new SimpleDateFormat("yyyy");
        String day_str = df.format(cal.getTime());
        mimicOtherMessage("Current year: " + day_str);
    }

    public void Month(){
        Calendar cal=Calendar.getInstance();
        SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
        String month_name = month_date.format(cal.getTime());
        mimicOtherMessage("Current month: " + month_name);
    }

    public void AutoClose(){
        System.exit(0);
    }


    public void start(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onResult(AIResponse result) {
        // If all is well, do this!
        if (!(result.isError())) {

            // This holds all the goods
            Result Rst = result.getResult();
            String userSpeechStr = Rst.getResolvedQuery();

            Log.i("User speech: ", Rst.getResolvedQuery());
            //String message = mEditTextMessage.getText().toString();
            sendMessage(userSpeechStr);

            if (userSpeechStr.contains("call") || userSpeechStr.contains("dialer")) {
                mimicOtherMessage("I'll  open your dialer");
                Call(userSpeechStr);

            } else if (userSpeechStr.contains("text") || userSpeechStr.contains("send") || userSpeechStr.contains("message")) {
                mimicOtherMessage("I'll  open your dialer");
                Text();

            } else if (userSpeechStr.contains("camera")) {
                mimicOtherMessage("Opening camera...");
                OpenCamera();

            } else if (userSpeechStr.contains("music") || userSpeechStr.contains("play")) {
                mimicOtherMessage("Launching Music player...");
                OpenMusicPlayer();

            } else if (userSpeechStr.contains("alarm") || userSpeechStr.contains("wake")) {
                mimicOtherMessage("Ok. Moving to the Alarm app...");
                SetAlarm();

            } else if (userSpeechStr.contains("timer")) {
                mimicOtherMessage("I will open the timer interface for you...");
                SetTimer();

            } else if (userSpeechStr.contains("search")|| userSpeechStr.contains("what is")) {
                mimicOtherMessage("Searching...");
                openWebPage(userSpeechStr);

            }

        }
    }

    @Override
    public void onError(AIError error) {

        // Actual error message
        String errorMessage = error.getMessage();

        // This is what it displays if the device isn't connected to the Internet
        String noInternet = "Speech recognition engine error: Server sends error status.";
        // This is what it displays if it can't understand your speech
        String cantUnderstand = "Speech recognition engine error: No recognition result matched.";
        // THis is the error message if u don't say anything
        String noInput = "Speech recognition engine error: No speech input.";


        // I want to display a custom error message instead of the pregenerated messages.
        // That is why I have this if else statement

        if (errorMessage.equals(noInternet)) {

            mimicOtherMessage("Sorry, I need to be connected to the Internet to work properly.");

        }

        else if (errorMessage.equals(cantUnderstand)) {


            mimicOtherMessage("Did you say something?. Try again, please.");

        }

        else if (errorMessage.equals(noInput)) {


            mimicOtherMessage("Did you say something? Try again, please.");

        }

    }

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {

    }

    @Override
    public void onListeningCanceled() {

    }

    @Override
    public void onListeningFinished() {

    }
}
