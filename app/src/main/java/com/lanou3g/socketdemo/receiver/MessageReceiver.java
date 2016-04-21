package com.lanou3g.socketdemo.receiver;

/**
 * 本类由: Risky57 创建于: 16/4/20.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ListView;

import com.lanou3g.socketdemo.adapter.SocketAdapter;
import com.lanou3g.socketdemo.service.SocketService;

public class MessageReceiver extends BroadcastReceiver {

    private static final String TAG = "MessageReceiver";
    private ListView listView;

    public MessageReceiver(ListView listView) {
        this.listView = listView;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String hostName = intent.getStringExtra(SocketService.KEY_HOSTNAME);
        String message = intent.getStringExtra(SocketService.KEY_MESSAGE);
        int port = intent.getIntExtra(SocketService.KEY_PORT,6080);
        SocketAdapter adapter = (SocketAdapter) listView.getAdapter();
        adapter.receiveMsg(message);
        listView.smoothScrollToPosition(listView.getMaxScrollAmount());
    }
}
