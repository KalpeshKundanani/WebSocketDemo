package com.example.admin.websocketdemo.Presenter;


import android.os.Parcel;
import android.os.Parcelable;

public interface DataPresenter extends Parcelable {
    void connectWebSocket();
    void sendMsgToServer(String msg);
    void openConnection();
    void closeConnection();

    @Override
    int describeContents();

    @Override
    void writeToParcel(Parcel parcel, int i);
}
