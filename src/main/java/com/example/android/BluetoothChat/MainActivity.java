package com.example.android.BluetoothChat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends Activity {
    SeekBar sb;
    TextView round;
    Button ai, peer, help;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sb = (SeekBar) findViewById(R.id.sbrounds);
        round = (TextView) findViewById(R.id.tvrounds);
        ai = (Button) findViewById(R.id.btnAI);
        peer = (Button) findViewById(R.id.btnPeer);
        help = (Button) findViewById(R.id.btnHelp);

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                round.setText(""+progress);
            }
        });

        Intent intent = new Intent(MainActivity.this, Select.class);
        startActivity(intent);

        ai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BluetoothChat.class);
                intent.putExtra("ROUNDS", round.getText().toString());
                intent.putExtra("TYPE", "AI");
                intent.putExtra("SELECT", "HOST");
                startActivity(intent);
            }
        });
        peer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BluetoothChat.class);
                intent.putExtra("ROUNDS", round.getText().toString());
                intent.putExtra("TYPE", "PEER");
                intent.putExtra("SELECT", "HOST");
                startActivity(intent);
            }
        });
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Help.class);
                startActivity(intent);
            }
        });
    }
}
