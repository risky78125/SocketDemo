package com.lanou3g.socketdemo.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 本类由: Risky57 创建于: 16/4/20.
 */
public class SocketService extends Service {
    public static final String ACTION_SOCKET_CHAT = "com.lanou3g.socket.ACTION_SOCKET_CHAT";
    public static final String ACTION_SOCKET_RECEIVE = "com.lanou3g.socket.ACTION_SOCKET_RECEIVE";
    public static final String KEY_MESSAGE = "receive_message";
    public static final String KEY_HOSTNAME = "host_name";
    public static final String KEY_PORT = "port";
    private static final String TAG = "SocketService";
    private ServerSocket mServerSocket;
    private boolean mIsServerRun = true;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(new Socketable()).start();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        closerServer();
    }

    private class Socketable implements Runnable {

        @Override
        public void run() {
            try {
                mServerSocket = new ServerSocket(6080);
                receiveMessage(mServerSocket);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                closerServer();
            }
        }

        private void receiveMessage(ServerSocket mServerSocket) throws IOException {
            Intent intentChat = new Intent(ACTION_SOCKET_CHAT);
            Intent intentReceive = new Intent(ACTION_SOCKET_RECEIVE);
            while (mIsServerRun) {
                Socket socket = mServerSocket.accept();
                String hostName = socket.getInetAddress().getHostName();
                int port = socket.getPort();
                InputStream is = socket.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader reader = new BufferedReader(isr);
                StringBuffer buffer = new StringBuffer();
                String str = "";
                while ((str = reader.readLine()) != null) {
                    buffer.append(str);
                }
                str = buffer.toString();
                reader.close();
                socket.close();
                if (!TextUtils.isEmpty(str)) {
                    intentChat.putExtra(KEY_MESSAGE, str);
                    intentChat.putExtra(KEY_HOSTNAME, hostName);
                    intentChat.putExtra(KEY_PORT, port);
                    sendBroadcast(intentChat);
                    intentReceive.putExtra(KEY_HOSTNAME, hostName);
                    sendBroadcast(intentReceive);
                }
            }
        }
    }

    private void closerServer() {
        mIsServerRun = false;
        if (mServerSocket != null) {
            try {
                mServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
