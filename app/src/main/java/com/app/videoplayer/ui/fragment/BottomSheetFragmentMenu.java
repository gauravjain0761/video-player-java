package com.app.videoplayer.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.videoplayer.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetFragmentMenu extends BottomSheetDialogFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    BottomSheetClick bottomSheetClick;

    public BottomSheetFragmentMenu(BottomSheetClick click) {
        this.bottomSheetClick = click;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(@NonNull final Dialog dialog, int style) {
        try {
            View contentView = View.inflate(getContext(), R.layout.bottomsheet_add_songs, null);
            dialog.setContentView(contentView);
            ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

            contentView.findViewById(R.id.layoutScan).setOnClickListener(v -> {
                try {
                    bottomSheetClick.onScanFilesClick();
                    dismiss();
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            contentView.findViewById(R.id.layoutLocalFiles).setOnClickListener(v -> {
                try {
                    dismiss();
                    dialog.dismiss();
                    bottomSheetClick.onLocalFilesClick();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface BottomSheetClick {
        void onLocalFilesClick();

        void onScanFilesClick();
    }
}