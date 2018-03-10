package com.example.admin.websocketdemo.View;

public interface DataView {
    void onConnectionMade();

    void onMessageReceived(String jsonString);

    void onConnectionClosed(int i, String s, boolean b);

    void onErrorInConnection(Exception e);

    void onLog(String s);
}
