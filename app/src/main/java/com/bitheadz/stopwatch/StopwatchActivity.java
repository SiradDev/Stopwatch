package com.bitheadz.stopwatch;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StopwatchActivity extends Activity {

    private int secs =0;
    private boolean running;
    private boolean wasRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);
        //retrieve values if activity gets destroyed, as a consequence of rotating screen or locale
        if(savedInstanceState != null){
            secs = savedInstanceState.getInt("seconds");
            running = savedInstanceState.getBoolean("running");
            wasRunning = savedInstanceState.getBoolean("wasRunning");
        }
        runTimer();

        /* Only the main thread can update the UI, if any other thread tries to do so,
        * you get a CalledFromWrongThreadException. Solution: use a Handler.*/
    }

    public void onClickStart(View view) {
        running = true;
    }
    public void onClickStop(View view) {
        running = false;
    }
    public void onClickReset(View view) {
        running = false;
        secs = 0;
    }

    private void runTimer() {

        final TextView timeView = (TextView)findViewById(R.id.timeView);
        /* Handler is Android class you can use to schedule code that
        * should be run at some point in the future.
        * You can also use it to post code that needs to run on different thread.*/

        final Handler handler = new Handler();
        //run code in runnable as soon as possible - in practice almost immediately
        handler.post(new Runnable() {
            @Override
            public void run() {
                int hrs = secs/3600;
                int mins = (secs%3600)/60;
                int seconds = secs%60;
                String time = String.format("%d:%02d:%02d", hrs, mins, seconds);
                timeView.setText(time);
                //increment secs variable if running has value true
                if(running){
                    secs++;
                }
                //schedule to run this code again after 1000 milliseconds
                handler.postDelayed(this, 1000);
            }
        });
    }

    /* Just playing with key lifecycle methods, although not necessary for this example START */

    @Override
    protected void onStart() {
        //you need to call superclass method, or else android will generate exception
        super.onStart();
        if(wasRunning){
            running=true;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //we need was running variable to tell us if stopwatch was already running
        wasRunning = running;
        running = false;
    }

    @Override
    protected void onRestart() {
        //this DOESN'T get called when the activity becomes visible FOR THE FIRST TIME
        //rotating screen will cause activity to get destroyed and recreated
        super.onRestart();
    }

    //if activity is in foreground but loses focus/regains it, these lifecycle methods get called

    @Override
    protected void onPause() {
        super.onPause();
        wasRunning = running;
        running = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(wasRunning) {
            running = true;
        }
    }
    /* Just playing with key lifecycle methods, although not necessary for this example STOP */

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        //save state before onDestroy so we can recreate it
        savedInstanceState.putInt("seconds", secs);
        savedInstanceState.putBoolean("running", running);
        savedInstanceState.putBoolean("wasRunning", wasRunning);
    }
}
