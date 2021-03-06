/*
 * Copyright (c) 2014 Jeff Martin
 * Copyright (c) 2015 Pedro Lafuente
 * Copyright (c) 2017 Gregor Santner and Markor contributors
 *
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 */
package net.gsantner.markor.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import net.gsantner.markor.R;
import net.gsantner.markor.util.AppSettings;

import java.io.Serializable;

public class ConfirmDialog extends DialogFragment {
    public static final String FRAGMENT_TAG = "ConfirmDialog";

    private static final String EXTRA_TITLE_RES_ID = "EXTRA_TITLE_RES_ID";
    public static final String EXTRA_DATA = "EXTRA_DATA";

    private Serializable _data;
    private ConfirmDialogCallback[] _callbacks;

    public static ConfirmDialog newInstance(@StringRes int titleResId,
                                            Serializable data, ConfirmDialogCallback... callbacks) {
        ConfirmDialog confirmDialog = new ConfirmDialog();
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_DATA, data);
        args.putInt(EXTRA_TITLE_RES_ID, titleResId);
        confirmDialog.setArguments(args);
        confirmDialog.setCallbacks(callbacks);
        return confirmDialog;
    }

    public void setCallbacks(ConfirmDialogCallback[] callbacks) {
        _callbacks = callbacks;
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int titleResId = getArguments().getInt(EXTRA_TITLE_RES_ID);
        _data = getArguments().getSerializable(EXTRA_DATA);

        AlertDialog.Builder dialogBuilder;
        boolean darkTheme = AppSettings.get().isDarkThemeEnabled();
        dialogBuilder = new AlertDialog.Builder(getActivity(), darkTheme ?
                R.style.Theme_AppCompat_Dialog : R.style.Theme_AppCompat_Light_Dialog);


        dialogBuilder.setTitle(getResources().getString(titleResId));

        dialogBuilder.setPositiveButton(getString(android.R.string.ok), new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        for (ConfirmDialogCallback cdc : _callbacks) {
                            cdc.onConfirmDialogAnswer(true, _data);
                        }
                    }
                });

        dialogBuilder.setNegativeButton(getString(android.R.string.cancel), new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        for (ConfirmDialogCallback cdc : _callbacks) {
                            cdc.onConfirmDialogAnswer(false, _data);
                        }
                    }
                });

        return dialogBuilder.show();
    }

    public interface ConfirmDialogCallback {
        void onConfirmDialogAnswer(boolean confirmed, Serializable data);
    }
}
