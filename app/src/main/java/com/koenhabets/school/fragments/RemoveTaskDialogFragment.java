package com.koenhabets.school.fragments;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.koenhabets.school.R;

public class RemoveTaskDialogFragment extends DialogFragment {
    public interface NoticeDialogListener {
        public void onDialogRemoveClick(DialogFragment dialog);
    }

    NoticeDialogListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (NoticeDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Remove task")
                    .setPositiveButton(R.string.Remove, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mListener.onDialogRemoveClick(RemoveTaskDialogFragment.this);
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            return builder.create();
        }
}
