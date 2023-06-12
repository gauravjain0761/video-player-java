package com.app.videoplayer.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.videoplayer.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetFragmentSortBy extends BottomSheetDialogFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    BottomSheetClick bottomSheetClick;
    int selectedRadio = 0;

    public BottomSheetFragmentSortBy(int selected, BottomSheetClick click) {
        this.bottomSheetClick = click;
        this.selectedRadio = selected;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(@NonNull final Dialog dialog, int style) {
        try {
            View contentView = View.inflate(getContext(), R.layout.bottomsheet_sortby, null);
            dialog.setContentView(contentView);
            ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

            RadioGroup radioGroup = contentView.findViewById(R.id.radioGroup);
            ((RadioButton) radioGroup.getChildAt(selectedRadio)).setChecked(true);
            radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                try {
                    if (radioGroup.getCheckedRadioButtonId() == radioGroup.getChildAt(0).getId()) {
                        selectedRadio = 0;
                    } else if (radioGroup.getCheckedRadioButtonId() == radioGroup.getChildAt(1).getId()) {
                        selectedRadio = 1;
                    } else if (radioGroup.getCheckedRadioButtonId() == radioGroup.getChildAt(2).getId()) {
                        selectedRadio = 2;
                    } else if (radioGroup.getCheckedRadioButtonId() == radioGroup.getChildAt(3).getId()) {
                        selectedRadio = 3;
                    } else if (radioGroup.getCheckedRadioButtonId() == radioGroup.getChildAt(4).getId()) {
                        selectedRadio = 4;
                    } else if (radioGroup.getCheckedRadioButtonId() == radioGroup.getChildAt(5).getId()) {
                        selectedRadio = 5;
                    } else if (radioGroup.getCheckedRadioButtonId() == radioGroup.getChildAt(6).getId()) {
                        selectedRadio = 6;
                    } else if (radioGroup.getCheckedRadioButtonId() == radioGroup.getChildAt(7).getId()) {
                        selectedRadio = 7;
                    }
                    bottomSheetClick.onRadioClick(selectedRadio);
                    dismiss();
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface BottomSheetClick {
        void onRadioClick(int selectedRadio);
    }
}