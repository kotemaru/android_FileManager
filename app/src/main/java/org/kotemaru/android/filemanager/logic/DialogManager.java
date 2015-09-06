// Copyright (c) kotemaru.org  (APL/2.0)
package org.kotemaru.android.filemanager.logic;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

public class DialogManager {

    public interface OnRenameListener {
        void onRename(CharSequence name);
    }

    public static void openRenameDialog(Activity activity, CharSequence curName, final OnRenameListener listener) {
        final EditText editView = new EditText(activity);
        editView.setText(curName);
        new AlertDialog.Builder(activity)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle("Rename")
                .setView(editView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        if (listener != null) listener.onRename(editView.getText());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                })
                .show();

    }

    public static void openErrorDialog(Activity activity, Throwable t) {
        new AlertDialog.Builder(activity)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Error!")
                .setMessage(t.getMessage())
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                })
                .show();

    }
}
