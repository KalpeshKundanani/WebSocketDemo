package com.example.admin.websocketdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.admin.websocketdemo.Model.WebSocketManager;
import com.example.admin.websocketdemo.Presenter.DataPresenter;
import com.example.admin.websocketdemo.View.DataView;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements DataView {
    private DataPresenter webSocketManager;
    TextView viewById;
    private final String WEB_SOCKET_MANAGER = "web_socket_manager";
    private final String LOG_TEXT = "LOG_TEXT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewById = findViewById(R.id.tv_msg);
        String message;

        logIt("onCreate");

        if (savedInstanceState != null) {
            message = savedInstanceState.getString(LOG_TEXT);
            viewById.setText(message);
            webSocketManager = savedInstanceState.getParcelable(WEB_SOCKET_MANAGER);
            if (webSocketManager != null) webSocketManager.setDataView(this);
        }
        if (webSocketManager == null) {
            webSocketManager = new WebSocketManager(MainActivity.this);
        }
    }

    /**
     * Send Message to server via web-socket.
     *
     * @param view : Send Button on Main Screen.
     */
    public void onSendClicked(View view) {
        final EditText etMsg = findViewById(R.id.etsendMsg);
        webSocketManager.sendMsgToServer(etMsg.getText().toString());
        etMsg.setText("");
    }

    /**
     * Open web-socket connection.
     *
     * @param view : Open Button on Main Screen.
     */
    public void onOpenClicked(View view) {
        webSocketManager.connectWebSocket();
        webSocketManager.openConnection();
    }

    /**
     * Close existing web-socket connection.
     *
     * @param view : Close Button on Main Screen.
     */
    public void onCloseClicked(View view) {
        webSocketManager.closeConnection();
    }

    @Override
    public void onConnectionMade() {
        logIt("Web Socket Opened");
    }

    @Override
    public void onMessageReceived(String jsonString) {
        logIt(jsonString);

        if (jsonString.contains("uid")) {
            try {
                JSONObject jsonObj = new JSONObject(jsonString);
                String uid = jsonObj.getString("uid");
                String uname = jsonObj.getString("uname");
                String uno = jsonObj.getString("no");
                logIt(uid + " " + uname + " " + uno);
            } catch (JSONException je) {
                logIt(je.toString());
            }
        }
    }

    @Override
    public void onConnectionClosed(int i, String s, boolean b) {
        logIt("Web Socket closed:" + s + "\t" + i + "\t" + b);
    }

    @Override
    public void onErrorInConnection(Exception e) {
        logIt(e.getMessage());
    }

    @Override
    public void onLog(String s) {
        logIt(s);
    }

    private void logIt(final String s) {
        runOnUiThread(() -> {
            Log.d("newF", "MainActivity hash-code : " + MainActivity.this.hashCode());
            String s1 = s + "\n\n" + viewById.getText();
            viewById.setText(s1);
        });
    }

    /**
     * Clear existing logs on view.
     *
     * @param view : Clear Button on Main Screen.
     */
    public void onClearLogClicked(View view) {
        viewById.setText("");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(WEB_SOCKET_MANAGER, webSocketManager);
        outState.putString(LOG_TEXT, viewById.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}