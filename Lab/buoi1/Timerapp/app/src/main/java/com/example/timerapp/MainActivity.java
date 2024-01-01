package com.example.timerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.sql.Time;

public class MainActivity extends AppCompatActivity {
    TextView timer_tv;
    ImageButton start_btn;
    ImageButton reset_btn;
    CountDownTimer countDownTimer;
    Boolean counterIsActive = false;
    ProgressBar timer_pg;
    MediaPlayer mediaPlayer;
    NumberPicker npHours;
    NumberPicker npMinutes;
    NumberPicker npSeconds;
    ImageButton stop_btn;
    public long TimeLeft = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timer_pg = findViewById(R.id.progressBar);
        stop_btn = findViewById(R.id.stop_btn);
        timer_pg.setMax(30);
        timer_pg.setProgress(0);
        timer_tv = findViewById(R.id.timer_tv);
        start_btn = findViewById(R.id.start_btn);
        reset_btn = findViewById(R.id.reset_btn);
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.alarm);
        npHours = findViewById(R.id.npHours);
        npMinutes = findViewById(R.id.npMinutes);
        npSeconds = findViewById(R.id.npSeconds);
        npSeconds.setMinValue(0);
        npSeconds.setMaxValue(60);
        npMinutes.setMinValue(0);
        npMinutes.setMaxValue(60);
        npHours.setMinValue(0);
        npHours.setMaxValue(24);
        npSeconds.setValue(10);
        reset_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimeReset();

            }
        });
        stop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop();
            }
        });
        reset_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimeReset();
            }
        });

    }


    private void update(int progress){
        int hours = progress/60/60;
        int minutes = progress/60%60;
        int seconds = progress;
        String hours_String;
        String minutes_String;
        String seconds_String;
        if (hours<=9)
            hours_String ="0"+hours;
        else
            hours_String=String.valueOf(hours);
        if (minutes<=9)
            minutes_String = "0" + minutes;
        else
            minutes_String = String.valueOf(minutes);
        int second_int = seconds-hours*3600-minutes*60;
        if (second_int<=9)
            seconds_String="0"+second_int;
        else
            seconds_String=String.valueOf(second_int);
        timer_pg.setProgress(progress);
        timer_tv.setText(hours_String+":" + minutes_String + ":" + seconds_String);
    }

    public void start_timer(View view){
        if(counterIsActive == false){
            if (TimeLeft==0){
                counterIsActive = true;
                TimeLeft = 0 ;
                TimeLeft += (long) npHours.getValue()*60*60;
                TimeLeft += (long) npMinutes.getValue()*60;
                TimeLeft += (long) npSeconds.getValue();
                timer_pg.setMax(((int)TimeLeft));
                TimeLeft = TimeLeft*1000;
                timer_pg.setProgress(0);
            }

            npHours.setVisibility(View.GONE);
            npMinutes.setVisibility(View.GONE);
            npSeconds.setVisibility(View.GONE);
            timer_tv.setVisibility(View.VISIBLE);
            start_btn.setVisibility(View.GONE);

            stop_btn.setVisibility(View.VISIBLE);
            countDownTimer = new CountDownTimer(TimeLeft, 1000) {
                @Override
                public void onTick(long l) {
                    TimeLeft = l;
                    update((int) l/ 1000);
                }

                @Override
                public void onFinish() {
                    TimeReset();
                    if(mediaPlayer != null)
                        mediaPlayer.start();
                }
            }.start();
        }else{
            stop();
        }
    }

    public void stop(){
        stop_btn.setVisibility(View.GONE);
        start_btn.setVisibility(View.VISIBLE);
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        start_btn.setImageResource(R.drawable.baseline_play_circle_24);
        timer_pg.setEnabled(true);
        counterIsActive = false;
    }
    public void TimeReset(){
        stop();
        counterIsActive = false;
        timer_tv.setVisibility(View.GONE);
        npHours.setVisibility(View.VISIBLE);
        npMinutes.setVisibility(View.VISIBLE);
        npSeconds.setVisibility(View.VISIBLE);
        timer_pg.setProgress(0);
        timer_tv.setVisibility(View.GONE);
        TimeLeft = 0 ;

    }
    @Override
    protected void onPause() {
        super.onPause();
        if(counterIsActive){
            countDownTimer.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(counterIsActive){
            countDownTimer.cancel();
        }
    }
}