package com.lanou3g.socketdemo.activity;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.lanou3g.socketdemo.R;
import com.lanou3g.socketdemo.adapter.SocketAdapter;
import com.lanou3g.socketdemo.receiver.MessageReceiver;
import com.lanou3g.socketdemo.service.SocketService;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * 本类由: Risky57 创建于: 16/4/20.
 */
public class ChatActivity extends AppCompatActivity {
    public static final String KEY_IP_ADDRESS = "ip_address";
    private ListView lvChat;
    private EditText etMessage;
    private Button btnSend;
    private String ipAddress;
    private SocketAdapter adapter;
    private MessageReceiver receiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        lvChat = (ListView) findViewById(R.id.lv_chat);
        etMessage = (EditText) findViewById(R.id.et_edit);
        btnSend = (Button) findViewById(R.id.btn_send);
        ipAddress = getIntent().getStringExtra(KEY_IP_ADDRESS);

        adapter = new SocketAdapter(this);
        lvChat.setAdapter(adapter);
        if (savedInstanceState == null) {
            sendInBackground("connect");
        }

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String message = etMessage.getText().toString();
                adapter.sendMsg(message);
                lvChat.smoothScrollToPosition(lvChat.getMaxScrollAmount());
                etMessage.setText("");
                sendInBackground(message);
            }
        });

    }

    private void sendInBackground(final String message) {
        if (TextUtils.isEmpty(message)) return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendMessage(message);
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        receiver = new MessageReceiver(lvChat);
        IntentFilter filter = new IntentFilter();
        filter.addAction(SocketService.ACTION_SOCKET_CHAT);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    private void sendMessage(String message) {
        try {
            Socket socket = new Socket(ipAddress, 6080);
            OutputStream os = socket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            BufferedWriter writer = new BufferedWriter(osw);
            writer.write(message);
            writer.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
