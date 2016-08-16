package com.example.android.BluetoothChat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.widget.TextView;

/**
 * Created by Zeal on 8/5/2016.
 */
public class Timer extends Activity {
    TextView countdown;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timer);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .8), (int) (height * .6));


        /*Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        if(bd != null)
        {
            String getResult = (String) bd.get("OUT");
            String getDesc = (String) bd.get("DESC");
            result.setText(getResult);
            desc.setText(getDesc);
        }*/

        countdown = (TextView) findViewById(R.id.tvCount);

        new CountDownTimer(4000, 1000) {

            public void onTick(long millisUntilFinished) {
                //round.setText("" + millisUntilFinished / 1000);
                countdown.setText(""+ ((millisUntilFinished / 1000) -1));
                if(countdown.getText().equals("0")){
                    countdown.setText("GAME");
                }
            }

            public void onFinish() {
                //round.setText("###");
                finish();
            }
        }.start();
    }
}
