package com.lanou3g.socketdemo;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.lanou3g.socketdemo.activity.ChatActivity;
import com.lanou3g.socketdemo.service.SocketService;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String KEY_ITEMS = "key_items";
    private SocketReceiver receiver;
    private ArrayList<String> addresses;
    private ArrayAdapter<String> adapter;
    private ListView listView;
    private Intent socketService;
    private static final String CONTENT = "长按尝试连接目标IP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            addresses = new ArrayList<>();
            addresses.add(CONTENT);
            socketService = new Intent(this, SocketService.class);
            startService(socketService);
        } else {
            addresses = savedInstanceState.getStringArrayList(KEY_ITEMS);
        }
        receiver = new SocketReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(SocketService.ACTION_SOCKET_RECEIVE);
        registerReceiver(receiver, filter);

        listView = (ListView) findViewById(R.id.list_view);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, addresses);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String address = addresses.get(position);
                if (!address.equals(CONTENT)) toNextActivity(address);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) showDialog();
                return false;
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(KEY_ITEMS, addresses);
    }

    private void toNextActivity(String address) {
        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
        intent.putExtra(ChatActivity.KEY_IP_ADDRESS, address);
        startActivity(intent);
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请输入目标IP地址:");
        builder.setMessage("IP地址...");
        final EditText et = new EditText(this);
        builder.setView(et);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String address = et.getText().toString();
                toNextActivity(address);
            }
        });
        builder.show();
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        stopService(socketService);
    }


    private class SocketReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String ipAddress = intent.getStringExtra(SocketService.KEY_HOSTNAME);
            if (!addresses.contains(ipAddress)) {
                addresses.add(ipAddress);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
