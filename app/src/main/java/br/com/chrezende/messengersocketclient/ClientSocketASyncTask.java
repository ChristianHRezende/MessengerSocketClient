package br.com.chrezende.messengersocketclient;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import br.com.chrezende.messengersocketclient.activity.MessengerHomeActivity;
import br.com.chrezende.messengersocketclient.activity.enums.StateConnectionEnum;
import br.com.chrezende.messengersocketclient.alert.Alert;
import br.com.chrezende.messengersocketclient.socket.ClientSocketService;

public class ClientSocketASyncTask extends AsyncTask<String, Void, Boolean> {

    private Exception exception;

    private Context mContext;

    public ClientSocketASyncTask(Context context) {
        mContext = context;
    }

    protected Boolean doInBackground(String... params) {

        try {
            //Call service on background
            ClientSocketService.start(Integer.parseInt(params[0]), params[1], mContext);
        } catch (Exception e) {
            this.exception = e;
        }
        return true;
    }

    protected void onPostExecute(Boolean success) {
        //Exceptions
        if (exception != null) {
            if (exception.getClass().equals(UnknownHostException.class)) {
                new Alert().showAlert(mContext, "Warning", "Address not found");
            } else if (exception.getClass().equals(IOException.class)) {
                new Alert().showAlert(mContext, "Warning", "Not possible send and receive messages with server");
            } else if (exception.getClass().equals(NullPointerException.class)) {
                new Alert().showAlert(mContext, "Warning", "Not possible send and receive messages with server");
                exception.printStackTrace();
            } else if (exception.getClass().equals(SocketTimeoutException.class)) {
                new Alert().showAlert(mContext, "Warning", "Server is offline");
            } else {
                new Alert().showAlert(mContext, "Warning", "Verify address and port");
            }
        }
        //Stop show progress and Show disconnected
        MessengerHomeActivity.showLoading(false);
        MessengerHomeActivity.changeConnectedState(false, StateConnectionEnum.NONE.toString().toLowerCase(), 0);
    }
}
