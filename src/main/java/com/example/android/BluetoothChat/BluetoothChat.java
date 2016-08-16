/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.BluetoothChat;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

/**
 * This is the main Activity that displays the current chat session.
 */
public class BluetoothChat extends Activity {
    // Debugging
    private static final String TAG = "BluetoothChat";
    private static final boolean D = true;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    // Layout Views
    /*private ListView mConversationView;
    private EditText mOutEditText;
	private Button mSendButton;*/

    private ImageButton rock, paper, sci;
    private TextView scorep1, scorep2, round, p1sub, p2sub;
    private ImageView p1, p2;

    Random r = new Random();
    int i1;
    int count = 0;
    int p1scr = 0;
    int p2scr = 0;
    int draws = 0;
    boolean bool1 = false;
    boolean bool2 = false;

    private SomeNameTask _SomeNameTask;

    int maxRounds = 0;
    String type = "peer";
    String select = "host";
    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Array adapter for the conversation thread
    private ArrayAdapter<String> mConversationArrayAdapter;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothChatService mChatService = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (D)
            Log.e(TAG, "+++ ON CREATE +++");

        // Set up the window layout
        setContentView(R.layout.main);


        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

    }


    @Override
    public void onStart() {
        super.onStart();
        if (D)
            Log.e(TAG, "++ ON START ++");

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else {
            if (mChatService == null)
                setupChat();
        }
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        if (D)
            Log.e(TAG, "+ ON RESUME +");

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity
        // returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't
            // started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
    }

    private void setupChat() {

        Log.d(TAG, "setupChat()");

        // Initialize the array adapter for the conversation thread
        mConversationArrayAdapter = new ArrayAdapter<String>(this,
                R.layout.message);
        /*mConversationView = (ListView) findViewById(R.id.in);
        mConversationView.setAdapter(mConversationArrayAdapter);

		// Initialize the compose field with a listener for the return key
		mOutEditText = (EditText) findViewById(R.id.edit_text_out);
		mOutEditText.setOnEditorActionListener(mWriteListener);

		// Initialize the send button with a listener that for click events
		mSendButton = (Button) findViewById(R.id.button_send);
		mSendButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Send a message using content of the edit text widget
				TextView view = (TextView) findViewById(R.id.edit_text_out);
				String message = view.getText().toString();
				sendMessage(message);
			}
		});*/
        rock = (ImageButton) findViewById(R.id.imageButton);
        paper = (ImageButton) findViewById(R.id.imageButton2);
        sci = (ImageButton) findViewById(R.id.imageButton3);

        scorep1 = (TextView) findViewById(R.id.tvp1score);
        scorep2 = (TextView) findViewById(R.id.tvp2score);
        round = (TextView) findViewById(R.id.tvRound);

        p1 = (ImageView) findViewById(R.id.ivp1);
        p2 = (ImageView) findViewById(R.id.ivp2);

        p1sub = (TextView) findViewById(R.id.tvp1sub);
        p2sub = (TextView) findViewById(R.id.tvp2sub);

        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        if (bd != null) {
            String getType = (String) bd.get("TYPE");
            String getSelect = (String) bd.get("SELECT");
            String getRounds = (String) bd.get("ROUNDS");
            maxRounds = Integer.parseInt(getRounds);
            type = getType;
            select = getSelect;
        }

        rock.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Send a message using content of the edit text widget
                if (type.equals("PEER")) {
                    String message = "1";
                    sendMessage(message);
                } else {
                    p1.setImageResource(R.drawable.rock);
                    p1sub.setText("Rock");
                    new GameAITask().execute("");
                }
            }
        });
        paper.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Send a message using content of the edit text widget
                if (type.equals("PEER")) {
                    String message = "2";
                    sendMessage(message);
                } else {
                    p1.setImageResource(R.drawable.paper1);
                    p1sub.setText("Paper");
                    new GameAITask().execute("");
                }

            }
        });
        sci.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Send a message using content of the edit text widget
                if (type.equals("PEER")) {
                    String message = "3";
                    sendMessage(message);
                } else {
                    p1.setImageResource(R.drawable.scissors1);
                    p1sub.setText("Scissors");
                    new GameAITask().execute("");
                }
            }
        });

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(this, mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");




    }


    @Override
    public synchronized void onPause() {
        super.onPause();
        if (D)
            Log.e(TAG, "- ON PAUSE -");
    }

    @Override
    public void onStop() {
        super.onStop();
        if (D)
            Log.e(TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mChatService != null)
            mChatService.stop();
        if (D)
            Log.e(TAG, "--- ON DESTROY ---");
    }

    private void ensureDiscoverable() {
        if (D)
            Log.d(TAG, "ensure discoverable");
        if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(
                    BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }


    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
            //scorep1.setText(mOutStringBuffer);
        }
    }

    // The action listener for the EditText widget, to listen for the return key
    private TextView.OnEditorActionListener mWriteListener = new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId,
                                      KeyEvent event) {
            // If the action is a key-up event on the return key, send the
            // message
            if (actionId == EditorInfo.IME_NULL
                    && event.getAction() == KeyEvent.ACTION_UP) {
                String message = view.getText().toString();
                sendMessage(message);
            }
            if (D)
                Log.i(TAG, "END onEditorAction");
            return true;
        }
    };

    private final void setStatus(int resId) {
        final ActionBar actionBar = getActionBar();
        actionBar.setSubtitle(resId);

    }

    private final void setStatus(CharSequence subTitle) {
        final ActionBar actionBar = getActionBar();
        actionBar.setSubtitle(subTitle);
    }

    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if (D)
                        Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to,
                                    mConnectedDeviceName));
                            mConversationArrayAdapter.clear();
                            Intent intent = new Intent(BluetoothChat.this, Timer.class);
                            startActivity(intent);
                            count = 0;
                            p1scr = 0;
                            p2scr = 0;
                            draws = 0;
                            count++;
                            round.setText("Round " + count);
                            new GameLimitTask().execute("");
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            setStatus(R.string.title_connecting);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            setStatus(R.string.title_not_connected);
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    String writeMessage = new String(writeBuf);

                    if (writeMessage.equals("1")) {
                        p1.setImageResource(R.drawable.rock);
                        p1sub.setText("Rock");
                        bool1 = true; //p1 set
                        new SomeNameTask().execute("");
                    }
                    if (writeMessage.equals("2")) {
                        p1.setImageResource(R.drawable.paper1);
                        p1sub.setText("Paper");
                        bool1 = true; //p1 set
                        new SomeNameTask().execute("");
                    }
                    if (writeMessage.equals("3")) {
                        p1.setImageResource(R.drawable.scissors1);
                        p1sub.setText("Scissors");
                        bool1 = true; //p1 set
                        new SomeNameTask().execute("");
                    }

                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);

                    p2.setImageResource(R.drawable.ic_action_navigation_more_horiz);
                    p2sub.setVisibility(View.INVISIBLE);
                    if (readMessage.equals("1")) {
                        p2sub.setText("Rock");
                        bool2 = true;
                        new SomeNameTask().execute("");
                    }

                    if (readMessage.equals("2")) {
                        p2sub.setText("Paper");
                        bool2 = true;
                        new SomeNameTask().execute("");
                    }

                    if (readMessage.equals("3")) {
                        p2sub.setText("Scissors");
                        bool2 = true;
                        new SomeNameTask().execute("");
                    }
                    int tmp = Integer.parseInt(readMessage);
                    if (tmp > 3) {
                        maxRounds = tmp;
                        Log.d("JajankenLog", tmp + "limit is set");
                    }


                    //p2.setImageResource(R.drawable.ic_action_navigation_more_horiz);

                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(),
                            "Connected to " + mConnectedDeviceName,
                            Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(),
                            msg.getData().getString(TOAST), Toast.LENGTH_SHORT)
                            .show();
                    break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (D)
            Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    private void connectDevice(Intent data) {
        // Get the device MAC address
        String address = data.getExtras().getString(
                DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent serverIntent = null;
        switch (item.getItemId()) {
            case R.id.connect_scan:
                // Launch the DeviceListActivity to see devices and do scan
                serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);

                return true;
            case R.id.discoverable:
                // Ensure this device is discoverable by others
                ensureDiscoverable();
                return true;
        }
        return false;
    }

    private class SomeNameTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {

            if (bool1 && bool2) {
                return "true";
            } else {
                return "false";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, result);
            String out = "", desc = "";
            //Toast.makeText(getApplicationContext(),""+result,Toast.LENGTH_SHORT).show();
            if (result == "true") {

                if (p2sub.getText().equals(p1sub.getText())) {
                    Log.d(TAG, "logs DRAW on " + scorep1.getText());
                    out = "It's a draw.";
                    desc = "Try again.";
                    draws++;
                    count++;
                }

                if (p1sub.getText().equals("Rock") && p2sub.getText().equals("Paper")) {
                    Log.d(TAG, "you lost" + scorep1.getText());
                    out = "You lose.";
                    desc = "paper beats rock";
                    p2scr++;
                    count++;
                    scorep1.setText("" + p1scr);
                    scorep2.setText("" + p2scr);
                }
                if (p1sub.getText().equals("Rock") && p2sub.getText().equals("Scissors")) {
                    Log.d(TAG, "you win" + scorep1.getText());
                    out = "You win.";
                    desc = "rock beats scissors";
                    p1scr++;
                    count++;
                    scorep1.setText("" + p1scr);
                    scorep2.setText("" + p2scr);
                }
                if (p1sub.getText().equals("Paper") && p2sub.getText().equals("Rock")) {
                    Log.d(TAG, "you win" + scorep1.getText());
                    out = "You win.";
                    desc = "paper beats rock";
                    p1scr++;
                    count++;
                    scorep1.setText("" + p1scr);
                    scorep2.setText("" + p2scr);
                }
                if (p1sub.getText().equals("Paper") && p2sub.getText().equals("Scissors")) {
                    Log.d(TAG, "you lost" + scorep1.getText());
                    out = "You lose.";
                    desc = "scissors beats paper";
                    p2scr++;
                    count++;
                    scorep1.setText("" + p1scr);
                    scorep2.setText("" + p2scr);
                }
                if (p1sub.getText().equals("Scissors") && p2sub.getText().equals("Rock")) {
                    Log.d(TAG, "you lost" + scorep1.getText());
                    out = "You lose.";
                    desc = "rock beats scissors";
                    p2scr++;
                    count++;
                    scorep1.setText("" + p1scr);
                    scorep2.setText("" + p2scr);
                }
                if (p1sub.getText().equals("Scissors") && p2sub.getText().equals("Paper")) {
                    Log.d(TAG, "you win" + scorep1.getText());
                    out = "You win.";
                    desc = "scissors beats paper";
                    p1scr++;
                    count++;
                    scorep1.setText("" + p1scr);
                    scorep2.setText("" + p2scr);
                }

                p2sub.setVisibility(View.VISIBLE);
                if (p2sub.getText().equals("Rock")) {
                    p2.setImageResource(R.drawable.rock1);
                }
                if (p2sub.getText().equals("Paper")) {
                    p2.setImageResource(R.drawable.paper);
                }
                if (p2sub.getText().equals("Scissors")) {
                    p2.setImageResource(R.drawable.scissors);
                }

                if (count == maxRounds + 1) {
                    Intent eopintent = new Intent(BluetoothChat.this, Result.class);
                    eopintent.putExtra("OUT", "GAME OVER");
                    if (p1scr > p2scr) {
                        eopintent.putExtra("DESC", "Congratulations!");
                    } else if (p2scr > p1scr) {
                        eopintent.putExtra("DESC", "You Lost!!!");
                    } else {
                        eopintent.putExtra("DESC", "DEADLOCK!");
                    }
                    eopintent.putExtra("SCORE_ONE", "" + p1scr);
                    eopintent.putExtra("SCORE_TWO", "" + p2scr);
                    eopintent.putExtra("ROUND", "" + (count - 1));
                    eopintent.putExtra("DRAW", "" + draws);
                    startActivity(eopintent);
                    scorep1.setText("0");
                    scorep2.setText("0");
                    count = 0;
                    p1scr = 0;
                    p2scr = 0;
                    draws = 0;
                    count++;
                    round.setText("Round " + count);
                } else {
                    Intent intent = new Intent(BluetoothChat.this, Result.class);
                    intent.putExtra("OUT", out);
                    intent.putExtra("DESC", desc);
                    intent.putExtra("SCORE_ONE", "" + p1scr);
                    intent.putExtra("SCORE_TWO", "" + p2scr);
                    intent.putExtra("ROUND", "" + (count - 1));
                    intent.putExtra("DRAW", "" + draws);
                    startActivity(intent);
                }


                new CountDownTimer(2000, 1000) {

                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        p1.setImageResource(R.drawable.ic_action_navigation_more_horiz);
                        p2.setImageResource(R.drawable.ic_action_navigation_more_horiz);
                        p1sub.setText("-");
                        p2sub.setText("-");

                        round.setText("Round " + count);

                    }
                }.start();

                Log.d(TAG, out + " is good");
                Log.d(TAG, desc + " is good");
                bool1 = false;
                bool2 = false;

            } else {
            }

        }

    }

    private class GameLimitTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {

            return "true";
        }

        @Override
        protected void onPostExecute(String result) {

            sendMessage("" + maxRounds);

        }
    }

    private class GameAITask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {

            return "true";
        }

        @Override
        protected void onPostExecute(String result) {

            i1 = r.nextInt(4 - 1) + 1;
            String tmp = ""+i1;
            p2sub.setText(""+i1);
            if (tmp.equals("1")) {
                p2sub.setText("Rock");
            }

            if (tmp.equals("2")) {
                p2sub.setText("Paper");
            }

            if (tmp.equals("3")) {
                p2sub.setText("Scissors");
            }
            Log.d(TAG, result);
            String out = "", desc = "";
            //Toast.makeText(getApplicationContext(),""+result,Toast.LENGTH_SHORT).show();
            if (result == "true") {

                if (p2sub.getText().equals(p1sub.getText())) {
                    Log.d(TAG, "logs DRAW on " + scorep1.getText());
                    out = "It's a draw.";
                    desc = "Try again.";
                    draws++;
                    count++;
                }

                if (p1sub.getText().equals("Rock") && p2sub.getText().equals("Paper")) {
                    Log.d(TAG, "you lost" + scorep1.getText());
                    out = "You lose.";
                    desc = "paper beats rock";
                    p2scr++;
                    count++;
                    scorep1.setText("" + p1scr);
                    scorep2.setText("" + p2scr);
                }
                if (p1sub.getText().equals("Rock") && p2sub.getText().equals("Scissors")) {
                    Log.d(TAG, "you win" + scorep1.getText());
                    out = "You win.";
                    desc = "rock beats scissors";
                    p1scr++;
                    count++;
                    scorep1.setText("" + p1scr);
                    scorep2.setText("" + p2scr);
                }
                if (p1sub.getText().equals("Paper") && p2sub.getText().equals("Rock")) {
                    Log.d(TAG, "you win" + scorep1.getText());
                    out = "You win.";
                    desc = "paper beats rock";
                    p1scr++;
                    count++;
                    scorep1.setText("" + p1scr);
                    scorep2.setText("" + p2scr);
                }
                if (p1sub.getText().equals("Paper") && p2sub.getText().equals("Scissors")) {
                    Log.d(TAG, "you lost" + scorep1.getText());
                    out = "You lose.";
                    desc = "scissors beats paper";
                    p2scr++;
                    count++;
                    scorep1.setText("" + p1scr);
                    scorep2.setText("" + p2scr);
                }
                if (p1sub.getText().equals("Scissors") && p2sub.getText().equals("Rock")) {
                    Log.d(TAG, "you lost" + scorep1.getText());
                    out = "You lose.";
                    desc = "rock beats scissors";
                    p2scr++;
                    count++;
                    scorep1.setText("" + p1scr);
                    scorep2.setText("" + p2scr);
                }
                if (p1sub.getText().equals("Scissors") && p2sub.getText().equals("Paper")) {
                    Log.d(TAG, "you win" + scorep1.getText());
                    out = "You win.";
                    desc = "scissors beats paper";
                    p1scr++;
                    count++;
                    scorep1.setText("" + p1scr);
                    scorep2.setText("" + p2scr);
                }

                p2sub.setVisibility(View.VISIBLE);
                if (p2sub.getText().equals("Rock")) {
                    p2.setImageResource(R.drawable.rock1);
                }
                if (p2sub.getText().equals("Paper")) {
                    p2.setImageResource(R.drawable.paper);
                }
                if (p2sub.getText().equals("Scissors")) {
                    p2.setImageResource(R.drawable.scissors);
                }

                if (count == maxRounds + 1) {
                    Intent eopintent = new Intent(BluetoothChat.this, Result.class);
                    eopintent.putExtra("OUT", "GAME OVER");
                    if (p1scr > p2scr) {
                        eopintent.putExtra("DESC", "Congratulations!");
                    } else if (p2scr > p1scr) {
                        eopintent.putExtra("DESC", "You Lost!!!");
                    } else {
                        eopintent.putExtra("DESC", "DEADLOCK!");
                    }
                    eopintent.putExtra("SCORE_ONE", "" + p1scr);
                    eopintent.putExtra("SCORE_TWO", "" + p2scr);
                    eopintent.putExtra("ROUND", "" + (count - 1));
                    eopintent.putExtra("DRAW", "" + draws);
                    startActivity(eopintent);
                    scorep1.setText("0");
                    scorep2.setText("0");
                    count = 0;
                    p1scr = 0;
                    p2scr = 0;
                    draws = 0;
                    count++;
                    round.setText("Round " + count);
                } else {
                    Intent intent = new Intent(BluetoothChat.this, Result.class);
                    intent.putExtra("OUT", out);
                    intent.putExtra("DESC", desc);
                    intent.putExtra("SCORE_ONE", "" + p1scr);
                    intent.putExtra("SCORE_TWO", "" + p2scr);
                    intent.putExtra("ROUND", "" + (count - 1));
                    intent.putExtra("DRAW", "" + draws);
                    startActivity(intent);
                }


                new CountDownTimer(2000, 1000) {

                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        p1.setImageResource(R.drawable.ic_action_navigation_more_horiz);
                        p2.setImageResource(R.drawable.ic_action_navigation_more_horiz);
                        p1sub.setText("-");
                        p2sub.setText("-");

                        round.setText("Round " + count);

                    }
                }.start();

                Log.d(TAG, out + " is good");
                Log.d(TAG, desc + " is good");
                bool1 = false;
                bool2 = false;

            } else {
            }

        }
    }

}
