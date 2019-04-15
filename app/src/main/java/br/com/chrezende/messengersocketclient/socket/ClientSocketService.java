package br.com.chrezende.messengersocketclient.socket;

import android.app.Activity;
import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import br.com.chrezende.messengersocketclient.R;
import br.com.chrezende.messengersocketclient.activity.MessengerHomeActivity;
import br.com.chrezende.messengersocketclient.activity.enums.StateConnectionEnum;
import br.com.chrezende.messengersocketclient.alert.Alert;
import br.com.chrezende.messengersocketclient.utils.SharedPreferencesHelper;

public class ClientSocketService {

    private static Socket socket;
    private static BufferedReader input;
    private static PrintWriter output;

    private Boolean serverReceived = false;


    /**
     * Start socket connection with server
     *
     * @param port
     * @param server
     * @param mContext
     * @throws IOException
     */
    public static void start(final int port, final String server, final Context mContext) throws IOException {
        //Show Progress
        showProgressBar(mContext, true);

        if (socket == null || !socket.isConnected()) {
            // connect to server

            socket = new Socket();
            int timeout = 3000;
            socket.connect(new InetSocketAddress(server, port), timeout);


            //StopShow Progress
            showProgressBar(mContext, false);

            //Show connected on home activity

            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MessengerHomeActivity.changeConnectedState(true, server, port);

                }
            });

            //Get the input and output from server/client socket

            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);

            //Send a start connection message with server

            sendMessageToServer("SignIn", mContext);

            //Check if server send message while is connected
            while (true) {
                //Check if server isConnected
                if (!socket.isConnected()) {
                    throw new NullPointerException();
                }
                //Get message from server
                String line = input.readLine();
                if (line != null && !line.isEmpty()) {

                    //Get message and Show feedback user
                    if (line.equals("success")) {
                        showAlertServerSocket(mContext, "Success", "Message to server received");
                    } else if (line.equals("fail")) {
                        showAlertServerSocket(mContext, "Ops", "Nor possible send to Server, check your connection");
                    } else if (line.equals("off")) {
                        showAlertServerSocket(mContext, "Ops", "Server stopped");
                    }
                }

            }
        }

        //Show disconnected on home activity
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MessengerHomeActivity.changeConnectedState(false, StateConnectionEnum.NONE.toString().toLowerCase(), 0);
            }
        });
    }

    /**
     * Send a message to server
     *
     * @param message
     * @param mContext
     */
    public static void sendMessageToServer(String message, final Context mContext) {
        if (socket != null && socket.isConnected()) {
            SharedPreferencesHelper helper = new SharedPreferencesHelper(mContext, mContext.getString(R.string.pref_key));
            String prefix = helper.getStringParam(mContext.getString(R.string.pref_text)) + "#!@";

            output.println(prefix + message);
        } else {
            showAlertServerSocket(mContext, "Ops", "Connect to server before");
        }

    }

    /**
     * Disconnect to server
     *
     * @throws IOException
     */
    public static void disconnectToServer() throws IOException {
        socket.close();
    }

    /**
     * Show feedback messaage to user
     *
     * @param mContext
     * @param title
     * @param message
     */
    private static void showAlertServerSocket(final Context mContext, final String title, final String message) {
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Alert.showAlert(mContext, title, message);
            }
        });
    }

    /**
     * ASk to controller to show progress bar
     *
     * @param mContext
     * @param isLoading
     */
    private static void showProgressBar(final Context mContext, final Boolean isLoading) {
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MessengerHomeActivity.showLoading(isLoading);
            }
        });
    }
}