package com.example.android.BluetoothChat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

/**
 * Created by Zeal on 8/5/2016.
 */
public class Select extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .8), (int) (height * .6));

        Button host, join;
        host = (Button) findViewById(R.id.btnHost);
        host.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        join = (Button) findViewById(R.id.btnJoin);
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Select.this, BluetoothChat.class);
                intent.putExtra("ROUNDS", "0");
                intent.putExtra("TYPE", "PEER");
                intent.putExtra("SELECT", "JOIN");
                startActivity(intent);
            }
        });
    }
}
