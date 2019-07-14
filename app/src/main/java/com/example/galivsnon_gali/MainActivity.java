package com.example.galivsnon_gali;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.cloud.ParticleEvent;
import io.particle.android.sdk.cloud.ParticleEventHandler;
import io.particle.android.sdk.cloud.exceptions.ParticleCloudException;
import io.particle.android.sdk.utils.Async;

public class MainActivity extends AppCompatActivity {


    String[] gali = {"બેન્ચોદ", "ભોસરીના", "મંધરચોદ" ,"ચુતીયા", "લોરો", "તારી ગાંડ"};

    String[] nonGali = {"ભગવાન", "સુગંધ" ,"સુંદર", "ધાર્મિક", "લોકપ્રિય", "પ્યાર"};

    String displayWord = "";
    int gameRounds;
    int wordIn = 0;
    int g_flag;
    int g_index;
    TextView question;
    String dataFromParticle = "";
    String particleId = "";
    private final String TAG = "Jarvis";
   // Data data = new Data();
    Random r = new Random();
    // MARK: Particle Account Info
    private final String PARTICLE_USERNAME = "patelpranav1313@gmail.com";
    private final String PARTICLE_PASSWORD = "$Patel14";

    // MARK: Particle Publish / Subscribe variables
    private long subscriptionId;

    // MARK: Particle device
    private List<ParticleDevice> mDevice;
    private List<DevicesData> devices = new LinkedList<>();
    TextView scorelbl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        gameRounds = 0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        question = (TextView) findViewById(R.id.questionLabel);
        // 1. Initialize your connection to the Particle API
        ParticleCloudSDK.init(this.getApplicationContext());

        // 2. Setup your device variable
        getDeviceFromCloud();
        setWord();
        scorelbl = (TextView) findViewById(R.id.scoreLabel);


        Async.executeAsync(ParticleCloudSDK.getCloud(), new Async.ApiWork<ParticleCloud, Object>() {

            @Override
            public Object callApi(@NonNull ParticleCloud particleCloud) throws ParticleCloudException, IOException {
                subscriptionId = ParticleCloudSDK.getCloud().subscribeToAllEvents(
                        "answer",  // the first argument, "eventNamePrefix", is optional
                        new ParticleEventHandler() {
                            public void onEvent(String eventName, ParticleEvent event) {
                                dataFromParticle = "" + event.dataPayload;
                                particleId = "" + event.deviceId;
                                 Log.i("12345", "Received event with payload: " + dataFromParticle + "Device Id = " + particleId);
                                for (int i = 0; i<devices.size();i++){
                                    if(devices.get(i).getDevice().getID().equals(particleId) && devices.get(i).isHasVoted() == false){
                                        devices.get(i).setHasVoted(true);
                                        devices.get(i).setVote(dataFromParticle);
                                    }
                                }
                                if(userStatus() == true && gameRounds <= 5){
                                    checkAnswer();
                                } else {
                                    Log.d("Game", "Game Over");
                                    scorelbl.setText("Game Over");
                                }
                            }
                            public void onEventError(Exception e) {
                                Log.e(TAG, "Event error: ", e);
                            }
                        });
                return -1;
            }

            @Override
            public void onSuccess(Object o) {
                Log.d(TAG, "Successfully subscribed device to Cloud");
            }

            @Override
            public void onFailure(ParticleCloudException exception) {
                Log.d(TAG, exception.getBestMessage());
            }
        });
    }

    public boolean userStatus(){
        int uiCnt = 0;
        for(int i = 0; i < devices.size(); i++){
            if(devices.get(i).isHasVoted() ==true){
                uiCnt++;
            }
        }
        if(uiCnt == devices.size()){
            gameRounds++;
            Log.d("RRRR", ""+gameRounds);
        }
        return (uiCnt == devices.size());
    }


    public void setWord(){
        if(gameRounds <= 5) {
            Log.d("GameRound",""+gameRounds);
            Random r1 = new Random();
            g_flag = r1.nextInt(2);
            g_index = r1.nextInt(6);
            if (g_flag == 0) {
                question.setText(gali[g_index]);
                displayWord = gali[g_index];
            } else {
                question.setText(nonGali[g_index]);
                displayWord = nonGali[g_index];
            }
            Log.d("Gali", displayWord);
        }
    }

    public void checkExistance(){
        if(Arrays.stream(gali).anyMatch(displayWord::equals)){
            wordIn = 1;
        }
        if(Arrays.stream(nonGali).anyMatch(displayWord::equals)){
            wordIn = 2;
        }
    }
    public void checkAnswer(){
        String cmdts = "";
        checkExistance();
        Log.d("DataIn", "Display Word = " + displayWord);
        Log.d("DataIn", "Word In = " + wordIn);
        scorelbl.setText("");
        for (int i = 0; i < devices.size(); i++){
            int ans = Integer.parseInt(devices.get(i).getVote());
            if(ans == wordIn){
                devices.get(i).setScore(devices.get(i).getScore() + 1);
                Log.d("DataIn", devices.get(i).getDevice().getID() + "You got it right...");
                cmdts = "0,255,0";

            }
            else{
                Log.d("DataIn", devices.get(i).getDevice().getID() +"You got it wrong...");
                cmdts = "255,0,0";
                changeColorsPressed(cmdts);
            }

            Log.d("DataIn", "Score = " + devices.get(i).getScore());
            Log.d("DataIn" , "Id = " + devices.get(i).getDevice().getName() + " -- answer = " + devices.get(i).getVote());
            scorelbl.setText(scorelbl.getText() + "Particle Name =" + devices.get(i).getDevice().getName() + " - Score = " +  devices.get(i).getScore() + "\n");
            devices.get(i).setVote("0");
            devices.get(i).setHasVoted(false);
            runOnUiThread(new Thread(new Runnable() {
                @Override
                public void run() {
                    setWord();
                }
            }));

            changeColorsPressed(cmdts);
        }

    }

    public void getDeviceFromCloud() {
        // This function runs in the background
        // It tries to connect to the Particle Cloud and get your device
        Async.executeAsync(ParticleCloudSDK.getCloud(), new Async.ApiWork<ParticleCloud, Object>() {
            @Override
            public Object callApi(@NonNull ParticleCloud particleCloud) throws ParticleCloudException, IOException {
                particleCloud.logIn(PARTICLE_USERNAME, PARTICLE_PASSWORD);
                //mDevice = particleCloud.getDevice(DEVICE_ID);
                mDevice = particleCloud.getDevices();
                for (int i = 0; i<mDevice.size();i++){
                    devices.add(new DevicesData(mDevice.get(i)));
                    //Log.d("jenelle",mDevice.get(i).getID());
                }
                for (int i = 0; i<devices.size();i++){
                    Log.d("id particle", "Hello World!!! " + devices.get(i).getDevice().getID());
                }
                return -1;
            }
            @Override
            public void onSuccess(Object o) {
                Log.d(TAG, "Successfully got device from Cloud");
            }
            @Override
            public void onFailure(ParticleCloudException exception) {
                Log.d(TAG, exception.getBestMessage());
            }
        });
    }

    public void changeColorsPressed(String cmd) {
        // logic goes here
        Async.executeAsync(ParticleCloudSDK.getCloud(), new Async.ApiWork<ParticleCloud, Object>() {
            @Override
            public Object callApi(@NonNull ParticleCloud particleCloud) throws ParticleCloudException, IOException {
                // 2. build a list and put the r,g,b into the list
                List<String> functionParameters = new ArrayList<String>();
                functionParameters.add(cmd);
                for (int i = 0; i <= devices.size(); i++) {
                    try {
                        devices.get(i).getDevice().callFunction("colors", functionParameters);
                        //mDevice.callFunction("colors", functionParameters);
                    } catch (ParticleDevice.FunctionDoesNotExistException e) {
                        e.printStackTrace();
                    }
                }
                return -1;
            }
            @Override
            public void onSuccess(Object o) {

                Log.d(TAG, "Sent colors command to device.");
            }
            @Override
            public void onFailure(ParticleCloudException exception) {
                Log.d(TAG, exception.getBestMessage());
            }
        });
    }
}
