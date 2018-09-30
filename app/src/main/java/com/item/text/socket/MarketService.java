package com.item.text.socket;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * author: wuzongjie
 * time  : 2018/4/27 0027 10:40
 * desc  : 行情的Socket Service
 * 基本可以实现socket 的发送接受数据，socket 心跳包的重连机制
 * 但是Service没有包活
 */

public class MarketService extends Service {
    private static final String TAG = "marcketSocket";
    private static final long sequenceId = 0;//以后用于token
    private static final int requestid = 0;//请求ID
    private static final int version = 1;
    private static final String terminal = "1001";  //安卓:1001,苹果:1002,WEB:1003,PC:1004
    //public static final String ip = "13.231.109.26";//行情  测试
    public static final String ip = "47.75.113.140";// 易币  测试
    private static final int port = 28901;//行情
    private DataInputStream dis = null;
    private DataOutputStream dos = null;
    private Socket socket = null;
    private SocketThread socketThread; // Socket线程
    private PingThread pingThread; // Ping包的线程
    private SocketMessage tradeMessage;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onGetMessage(SocketMessage message) {
        if (message.getCode() == 0) { // 行情方面的推送

            getSocketOne(message);
            if (message.getCmd() == ISocket.CMD.SUBSCRIBE_EXCHANGE_TRADE) { // 这里我保存下盘口推送的的指令
                tradeMessage = message;
            }
        }
    }

    /**
     * 对行情方面推送的处理
     */
    private void getSocketOne(SocketMessage message) {
        if (socketThread != null && socketThread.isAlive()) {
            if (socket == null || !socket.isConnected()) {
                releaseSocket();
            }
            toRequest(message.getCmd(), message.getBody());
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        isOpen = true;
        if (socketThread == null || !socketThread.isAlive()) {
            socketThread = new SocketThread();
            socketThread.start();
        }
    }

    class PingThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (isOpen) {
                try {
                    byte[] requestBytes = buildRequest(ISocket.CMD.HEART_BEAT, null);
                    dos.write(requestBytes);
                    dos.flush();

                } catch (Exception e) {

                    releaseSocket();
                }
                SystemClock.sleep(8000); // 每隔5s发送一次心跳包
            }
        }
    }

    private boolean isOpen;
    private static final String lock = "lock";

    class SocketThread extends Thread {
        @Override
        public void run() {
            super.run();
            synchronized (lock) {
                if (socket == null || !socket.isConnected()) {
                    try {
                        socket = new Socket(ip, port);
                        dis = new DataInputStream(socket.getInputStream());
                        dos = new DataOutputStream(socket.getOutputStream());
                        isOpen = true;
                        pingThread = new PingThread();
                        Log.d("jiejie","socket链接成功");
                        pingThread.start(); // 开启ping包线程
                    } catch (IOException e) {
                        Log.d("jiejie","socket连接失败-----");
                        isOpen = false;
                        socket = null;
                    }
                }
                // 接受服务器返回的信息
                while (isOpen) {
                    try {
                        dealResponse(dis);
                    } catch (Exception e) {
                    }
                }
            }
        }
    }

    /**
     * Socket重新创建
     */
    private void releaseSocket() {
        if (socket != null) {
            try {
                socket.close();
                dos.close();
                dis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            socket = null;
            dis = null;
            dos = null;
        }
        try {
            socket = new Socket(ip, port);
            if (socket.isConnected()) {
                dis = new DataInputStream(socket.getInputStream());
                dos = new DataOutputStream(socket.getOutputStream());
                // 这里我推送最后一次发送来的数据 (这里我只推动行情数据其他的不管了)
                toRequest(ISocket.CMD.SUBSCRIBE_SYMBOL_THUMB, null);
                if (tradeMessage != null)
                    toRequest(tradeMessage.getCmd(), tradeMessage.getBody()); // 发送最后一次订阅的盘口信息
            }
        } catch (IOException e) {

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (socket != null) try {
            socket.close();
            dis.close();
            dos.close();
            socket = null;
            dos = null;
            dis = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        socketThread = null;
        pingThread = null;
        isOpen = false;
        EventBus.getDefault().unregister(this);
    }

    /**
     * 数据发送到socket
     */
    private void toRequest(ISocket.CMD cmd, byte[] body) {
        try {
            byte[] requestBytes = buildRequest(cmd, body);
            dos.write(requestBytes);
            dos.flush();
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public static byte[] buildRequest(ISocket.CMD cmd, byte[] body) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        try {
            int length = body == null ? 26 : (26 + body.length);
            dos.writeInt(length);
            dos.writeLong(sequenceId);
            dos.writeShort(cmd.getCode());
            dos.writeInt(version);
            byte[] terminalBytes = terminal.getBytes();
            dos.write(terminalBytes);
            dos.writeInt(requestid);
            if (body != null) dos.write(body);
            return bos.toByteArray();
        } catch (IOException ex) {
            try {
                dos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 接受的数据
     */
    private void dealResponse(DataInputStream dis) throws Exception {
        int length = dis.readInt();
        long sequenceId = dis.readLong();
        short code = dis.readShort();
        final int responseCode = dis.readInt();
        int requestId = dis.readInt();
        byte[] buffer = new byte[length - 22];
        final ISocket.CMD cmd = ISocket.CMD.findObjByCode(code);
        int nIdx = 0;
        int nReadLen = 0;
        while (nIdx < buffer.length) {
            nReadLen = dis.read(buffer, nIdx, buffer.length - nIdx);
            if (nReadLen > 0) {
                nIdx += nReadLen;
            } else {
                break;
            }
        }
        String str = new String(buffer);
        if (responseCode == 200) {
            EventBus.getDefault().post(new SocketResponse(cmd, str));

        }
    }
}
