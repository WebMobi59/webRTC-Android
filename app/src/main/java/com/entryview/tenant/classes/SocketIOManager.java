package com.entryview.tenant.classes;

import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketIOManager {
    private static  SocketIOManager socketIOManager;
    public Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://ec2-52-24-49-20.us-west-2.compute.amazonaws.com:2016");
        } catch (URISyntaxException e) {}
    }
    private SocketIOManager() {

    }
    public static SocketIOManager getInstance() {
        if (socketIOManager == null) {
            socketIOManager = new SocketIOManager();
        }
        return socketIOManager;
    }

    public void establishConnection() {
        mSocket.connect();
    }

    public void closeConnection() {
        mSocket.disconnect();
    }
    public void connectToServerWithNickname(String identifier) {
        mSocket.emit("connectUser", identifier);
    }
    public void exitChatWithNickname(String identifier) {
        mSocket.emit("disconnected", identifier);
    }

    public void sendMessage(final JSONObject message) {
        mSocket.emit("message", message);
    }

}
