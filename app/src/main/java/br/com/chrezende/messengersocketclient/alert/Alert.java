package br.com.chrezende.messengersocketclient.alert;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class Alert {

    private static AlertDialog alert;

    public static void showAlert(final Context context, String title, String message) {
        //Generator
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //define title
        builder.setTitle(title);
        //define message
        builder.setMessage(message);
        //define um button
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                //close
                arg0.dismiss();
            }
        });
        //create the alert
        alert = builder.create();
        //Show
        alert.show();
    }

}
