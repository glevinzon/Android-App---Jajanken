package com.example.android.BluetoothChat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Zeal on 8/5/2016.
 */
public class Result extends Activity {
    TextView result;
    TextView timer;
    TextView desc;
    TextView score;
    TextView round;
    TextView w,d,l,r;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.result);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .8), (int) (height * .6));

        result = (TextView) findViewById(R.id.tvResult);
        timer = (TextView) findViewById(R.id.tvtimer);
        desc = (TextView) findViewById(R.id.tvDesc);
        score = (TextView) findViewById(R.id.tvScore);
        round = (TextView) findViewById(R.id.tvRound);

        w = (TextView) findViewById(R.id.tvW);
        d = (TextView) findViewById(R.id.tvD);
        l = (TextView) findViewById(R.id.tvL);
        r = (TextView) findViewById(R.id.tvR);

        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        if(bd != null)
        {
            String getResult = (String) bd.get("OUT");
            String getDesc = (String) bd.get("DESC");
            result.setText(getResult);
            desc.setText(getDesc);
            String getW = (String) bd.get("SCORE_ONE");
            String getL = (String) bd.get("SCORE_TWO");
            String getD = (String) bd.get("DRAW");
            String getR = (String) bd.get("ROUND");
            w.setText(getW);
            d.setText(getD);
            l.setText(getL);
            r.setText("No. of Rounds : "+getR);

        }


        new CountDownTimer(3000, 1000) {

            public void onTick(long millisUntilFinished) {
                //round.setText("" + millisUntilFinished / 1000);
                timer.setText("will dismiss in "+ millisUntilFinished / 1000 +"sec/s");
            }

            public void onFinish() {
                //round.setText("###");
                finish();
            }
        }.start();


    }
}
