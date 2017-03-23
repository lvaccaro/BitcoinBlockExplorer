package io.merkur.bitcoinblockexplorer;

import android.app.Activity;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;


public class MySnackbar {

    // Showing the status in Snackbar
    public static void showPositive(Activity activity, String message) {
        Snackbar snackbar = Snackbar.make(activity.findViewById(R.id.fab), message, Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.GREEN);
        snackbar.setDuration(10*1000);
        snackbar.show();
    }
    public static void showNegative(Activity activity, String message) {
        Snackbar snackbar = Snackbar.make(activity.findViewById(R.id.fab), message, Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.RED);
        snackbar.setDuration(10*1000);
        snackbar.show();
    }
    public static void showWarning(Activity activity, String message) {
        Snackbar snackbar = Snackbar.make(activity.findViewById(R.id.fab), message, Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.setDuration(10*1000);
        snackbar.show();
    }
}
