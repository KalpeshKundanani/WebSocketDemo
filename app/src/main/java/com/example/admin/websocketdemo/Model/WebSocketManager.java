package com.example.admin.websocketdemo.Model;

import android.os.Parcel;
import android.util.Log;

import com.example.admin.websocketdemo.Presenter.DataPresenter;
import com.example.admin.websocketdemo.View.DataView;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;


public class WebSocketManager implements DataPresenter {
    private DataView dataView;
    private final static String WS_PATH = "ws://192.168.0.44:8080/WSDemo2/endpoint";

    private WebSocketClient mWebSocketClient;

    public WebSocketManager(final DataView dataView) {
        this.dataView = dataView;
    }

    private WebSocketManager(Parcel in) {
        Log.d("newF", "WebSocketManager: ");
        mWebSocketClient = (WebSocketClient) in.readValue(WebSocketClient.class.getClassLoader());
        dataView = (DataView) in.readValue(DataView.class.getClassLoader());
        dataView.onLog("WebSocketManager");
    }

    public static final Creator<WebSocketManager> CREATOR = new Creator<WebSocketManager>() {
        @Override
        public WebSocketManager createFromParcel(Parcel in) {
            return new WebSocketManager(in);
        }

        @Override
        public WebSocketManager[] newArray(int size) {
            return new WebSocketManager[size];
        }
    };

    @Override
    public void connectWebSocket() {
        if (mWebSocketClient != null) {
            if (!(mWebSocketClient.getReadyState() == WebSocket.READYSTATE.CLOSED)) {
                mWebSocketClient.close();
            }
        }
        initWebSocket();
    }

    @Override
    public void sendMsgToServer(String msg) {
        if (mWebSocketClient != null && mWebSocketClient.getReadyState() == WebSocket.READYSTATE.OPEN) {
            mWebSocketClient.send("From mobile: " + msg);
            dataView.onLog("Sent : " + msg);
        } else {
            dataView.onLog("Please open connection first!");
        }
    }

    @Override
    public void openConnection() {
        try {
            if (!(mWebSocketClient.getReadyState() == WebSocket.READYSTATE.OPEN)) {
                mWebSocketClient.connect();
            } else {
                dataView.onLog("Already connected");
            }
        } catch (Exception e) {
            dataView.onErrorInConnection(e);
        }
    }

    @Override
    public void closeConnection() {
        if (mWebSocketClient != null) {
            if (!(mWebSocketClient.getReadyState() == WebSocket.READYSTATE.CLOSED)) {
                mWebSocketClient.close();
                dataView.onLog("close");
            } else {
                dataView.onLog("Already closed!");
            }
        } else {
            dataView.onLog("Connection not found");
        }
    }

    private void initWebSocket() {
        URI wsuri;
        try {
            wsuri = new URI(WS_PATH);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            dataView.onErrorInConnection(e);
            return;
        }

        mWebSocketClient = new WebSocketClient(wsuri, new Draft_17()) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened");
                dataView.onConnectionMade();
            }

            @Override
            public void onMessage(String jsonString) {
                dataView.onMessageReceived(jsonString);
            }

            @Override
            public void onClose(int i, final String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
                dataView.onConnectionClosed(i, s, b);
            }

            @Override
            public void onError(final Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
                dataView.onErrorInConnection(e);
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeValue(mWebSocketClient);
        parcel.writeValue(dataView);
    }

    @Override
    public void setDataView(DataView dataView) {
        this.dataView = dataView;
    }
}