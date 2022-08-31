package com.saurav_ranjan.stopwatch;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.animation.LayoutTransition;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.core.app.NotificationCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    private Button startButton;
    private Button lapButton;
    private TextView textView;
    private Timer timer;
    private NotificationCompat.Builder builder;
    private NotificationManager manager;
    private LayoutTransition transition;
    private LinearLayout linearLayout;
    private int currentTime = 0;
    private int lapTime = 0;
    private int lapCounter = 0;
    private int mId = 1;
    private boolean lapViewExists;
    private boolean isButtonStartPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = (Button) findViewById(R.id.btn_start);
        lapButton = (Button) findViewById(R.id.btn_hold);
        textView = (TextView) findViewById(R.id.stopwatch_view);
        textView.setTextSize(55);
    }
    public void onStart(View view) {
        if (isButtonStartPressed) {
            onSWatchStop();
        } else {
            isButtonStartPressed = true;

            startButton.setBackgroundResource(R.drawable.btn_stop_status);
            startButton.setText(R.string.stop);

            lapButton.setBackgroundResource(R.drawable.btn_hold_status);
            lapButton.setText(R.string.btn_hold);
            lapButton.setEnabled(true);

            setUpNotification();

            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            currentTime += 1;
                            lapTime += 1;

                            manager = (NotificationManager)
                                    getSystemService(Context.NOTIFICATION_SERVICE);

                            // update notification text
                            builder.setContentText(Timeformatutil.toDisplayString(currentTime));
                            manager.notify(mId, builder.build());

                            // update ui
                            textView.setText(Timeformatutil.toDisplayString(currentTime));
                        }
                    });
                }
            }, 0, 10);
        }
    }

    public void onSWatchStop() {
        startButton.setBackgroundResource(R.drawable.btn_start_status);
        startButton.setText(R.string.start);
        lapButton.setEnabled(true);
        lapButton.setBackgroundResource(R.drawable.btn_hold_status);
        lapButton.setText(R.string.reset);

        isButtonStartPressed = false;
        timer.cancel();
        manager.cancel(mId);
    }

    public void onSWatchReset() {
        timer.cancel();
        currentTime = 0;
        lapTime = 0;
        lapCounter = 0;
        textView.setText(Timeformatutil.toDisplayString(currentTime));
        lapButton.setEnabled(false);
        lapButton.setText(R.string.btn_hold);
        lapButton.setBackgroundResource(R.drawable.btn_reset_status);

        if (lapViewExists) {
            linearLayout.setLayoutTransition(null);
            linearLayout.removeAllViews();
            lapViewExists = false;
        }
    }

    public void onHold(View view) {
        if (!isButtonStartPressed) {
            onSWatchReset();
        } else {
            lapViewExists = true;
            lapCounter++;

            transition = new LayoutTransition();
            transition.setAnimator(LayoutTransition.CHANGE_APPEARING, null);
            transition.setStartDelay(LayoutTransition.APPEARING, 0);

            linearLayout = (LinearLayout) findViewById(R.id.layout);
            linearLayout.setLayoutTransition(transition);

            TextView lapDisplay = new TextView(this);
            ImageView imageView = new ImageView(this);
            imageView.setFocusableInTouchMode(true);

            lapDisplay.setGravity(Gravity.CENTER);
            lapDisplay.setTextColor(Color.WHITE);
            lapDisplay.setTextSize(30);

            linearLayout.addView(lapDisplay);
            linearLayout.addView(imageView);

            imageView.requestFocus();

            lapDisplay.setText(String.format("Lap %d: %s", lapCounter, Timeformatutil.toDisplayString(lapTime)));
            imageView.setImageResource(R.drawable.divider);
            lapTime = 0;
        }
    }

    public void setUpNotification() {
        builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.timelapse)
                        .setContentTitle("Stopwatch running")
                        .setContentText("00:00:00")
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setOngoing(true);


        Intent resultIntent = new Intent(this, MainActivity.class);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(resultPendingIntent);

    }

}