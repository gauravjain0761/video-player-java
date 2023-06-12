package com.app.videoplayer.ui.activity;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MergeCursor;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewbinding.ViewBinding;

import com.app.videoplayer.R;
import com.app.videoplayer.adapter.ScanFilesAdapter;
import com.app.videoplayer.adapter.TrashedSongsAdapter;
import com.app.videoplayer.databinding.ActivityScanFilesBinding;
import com.app.videoplayer.db.DBUtils;
import com.app.videoplayer.db.FetchSongsFromLocal;
import com.app.videoplayer.entity.SongEntity;
import com.app.videoplayer.ui.IActivityContract;
import com.app.videoplayer.ui.fragment.BottomSheetFragmentSortBy;
import com.app.videoplayer.ui.fragment.FragmentSongs;
import com.app.videoplayer.ui.presenter.ScanFilesActivityPresenter;
import com.app.videoplayer.utils.AppUtils;
import com.app.videoplayer.utils.SortComparator;
import com.app.mvpdemo.businessframe.mvp.activity.BaseActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ScanFilesActivity extends BaseActivity<ScanFilesActivityPresenter> implements IActivityContract.IActivityView {

    ActivityScanFilesBinding binding;
    ScanFilesActivityPresenter presenter;
    TrashedSongsAdapter adapter;
    String TAG = ScanFilesActivity.class.getSimpleName();
    public static List<SongEntity> songsList = new ArrayList<>();
    int selectedSortByRadio = 0;
    boolean isKeyboardShowing = false;

    @Override
    protected boolean isSupportHeadLayout() {
        return false;
    }

    @Override
    protected boolean isNeedSetStatusBar() {
        return true;
    }

    @Override
    protected ViewBinding getContentView() {
        binding = ActivityScanFilesBinding.inflate(getLayoutInflater());
        return binding;
    }

    @Override
    protected ScanFilesActivityPresenter createPresenter(Context context) {
        presenter = new ScanFilesActivityPresenter(context, this);
        presenter.setBinding(binding);
        return presenter;
    }

    @Override
    protected void setStatusBar() {
        try {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initWidget() {
        try {
            binding.getRoot().getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                try {
                    Rect r = new Rect();
                    binding.getRoot().getWindowVisibleDisplayFrame(r);
                    int screenHeight = binding.getRoot().getRootView().getHeight();
                    int keypadHeight = screenHeight - r.bottom;

                    if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                        // keyboard is opened
                        if (!isKeyboardShowing) {
                            isKeyboardShowing = true;
                        }
                    } else {
                        // keyboard is closed
                        if (isKeyboardShowing) {
                            isKeyboardShowing = false;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            binding.imageViewBack.setOnClickListener(v -> {
                try {
                    AppUtils.hideKeyboardOnClick(ScanFilesActivity.this, v);
                    clear();
                    setResult(Activity.RESULT_OK, new Intent());
                    finish();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            binding.cbDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (songsList != null && songsList.size() > 0) {
                            if (binding.cbDelete.isChecked()) {
                                for (int i = 0; i < songsList.size(); i++) {
                                    SongEntity songs = songsList.get(i);
                                    songs.setIsChecked(true);
                                    songsList.set(i, songs);
                                }
                            } else {
                                for (int i = 0; i < songsList.size(); i++) {
                                    SongEntity songs = songsList.get(i);
                                    songs.setIsChecked(false);
                                    songsList.set(i, songs);
                                }
                            }
                            notifyAdapter();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

//            binding.cbDelete.setOnCheckedChangeListener((buttonView, isChecked) -> {
//                try {
//                    if (songsList != null && songsList.size() > 0) {
//                        if (isChecked) {
//                            for (int i = 0; i < songsList.size(); i++) {
//                                SongEntity songs = songsList.get(i);
//                                songs.setIsChecked(true);
//                                songsList.set(i, songs);
//                            }
//                        } else {
//                            for (int i = 0; i < songsList.size(); i++) {
//                                SongEntity songs = songsList.get(i);
//                                songs.setIsChecked(false);
//                                songsList.set(i, songs);
//                            }
//                        }
//                        notifyAdapter();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            });

            binding.txtDone.setOnClickListener(v -> {
                try {
                    clear();
                    if (adapter != null) adapter.clearLongPress();
                    checkTopLayout();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            binding.txtSort.setOnClickListener(v -> {
                try {
                    if (songsList != null && songsList.size() > 0) {
                        new BottomSheetFragmentSortBy(selectedSortByRadio, selected -> {
                            selectedSortByRadio = selected;
                            Log.e("TAG", " selected " + selectedSortByRadio);
                            sortList();
                        }).show(getSupportFragmentManager(), "BottomSheetFragmentSortBy");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            binding.fab.setOnClickListener(v -> {
                try {
                    if (songsList.stream().filter(SongEntity::getIsChecked).collect(Collectors.toList()).size() > 0) {
                        FragmentSongs.scanSongList.postValue(songsList.stream().filter(SongEntity::getIsChecked).collect(Collectors.toList()));
                        setResult(Activity.RESULT_OK, new Intent());
                        finish();
                        //FragmentSongs.insertSongsInDb(songsList.stream().filter(SongEntity::getIsChecked).collect(Collectors.toList()));

//                        int moveCount = songsList.stream().filter(SongEntity::getIsChecked).collect(Collectors.toList()).size();
//                        DBUtils.insertMultipleSongs(songsList.stream().filter(SongEntity::getIsChecked).collect(Collectors.toList()), new DBUtils.TaskComplete() {
//                            @Override
//                            public void onTaskComplete() {
//                                try {
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            try {
//                                                setResult(Activity.RESULT_OK, new Intent());
//                                                finish();
//                                                Toast.makeText(getApplicationContext(), moveCount + " " + getResources().getString(R.string.alert_saved_msg), Toast.LENGTH_SHORT).show();
//                                            } catch (Exception e) {
//                                                e.printStackTrace();
//                                            }
//                                        }
//                                    });
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        });
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.alert_saved_error_msg), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            binding.imageViewSearch.setOnClickListener(v -> {
                if (songsList != null && songsList.size() > 0) {
                    startActivityForResult(new Intent(this, SearchScanFilesActivity.class), 1001);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initData() {
        try {
            new AsyncScanList().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class AsyncScanList extends AsyncTask<String, String, List<SongEntity>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(4000);
            rotate.setRepeatCount(ValueAnimator.INFINITE);
            rotate.setInterpolator(new LinearInterpolator());
            binding.imageViewRotate.startAnimation(rotate);
        }

        @Override
        protected List<SongEntity> doInBackground(String... strings) {
            try {
                if (songsList != null) songsList.clear();
                songsList = FetchSongsFromLocal.getAllSongs(ScanFilesActivity.this);
                Collections.sort(songsList, new SortComparator.SortByName.Ascending());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return songsList;
        }

        @Override
        protected void onPostExecute(List<SongEntity> list) {
            super.onPostExecute(list);
            try {
                binding.layoutScan.setVisibility(View.GONE);
                refreshView();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void registerEvent() {
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void unRegisterEvent() {
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void refreshView() {
        try {
            if (songsList != null && songsList.size() > 0) {
                showListView();
            } else {
                showNoDataView();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void notifyAdapter() {
        try {
            if (binding.listView.getAdapter() != null)
                binding.listView.getAdapter().notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setListView() {
        try {
            binding.listView.setHasFixedSize(true);
            binding.listView.setVerticalScrollBarEnabled(true);
            binding.listView.setLayoutManager(new LinearLayoutManager(ScanFilesActivity.this));
            binding.listView.setItemAnimator(new DefaultItemAnimator());

            binding.layoutList.setVisibility(android.view.View.VISIBLE);
            binding.layoutListView.setVisibility(android.view.View.VISIBLE);
            binding.listView.setVisibility(android.view.View.VISIBLE);
            binding.txtNoData.setVisibility(android.view.View.GONE);
            binding.layoutScan.setVisibility(android.view.View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showListView() {
        try {
            setListView();
            adapter = new TrashedSongsAdapter(ScanFilesActivity.this, new TrashedSongsAdapter.TrashSongsListener() {
                @Override
                public void onCheckedChangeListener(SongEntity result, boolean isChecked, int position) {
                    try {
                        songsList.set(position, result);
                        checkTopLayout();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void longPressSongs(boolean isAnyLongPress) {
                    if (isKeyboardShowing) {
                        AppUtils.closeKeyboard(ScanFilesActivity.this);
                    }
                    checkTopLayout();
                }
            });
            binding.listView.setAdapter(adapter);
            adapter.submitList(songsList);
            checkTopLayout();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkTopLayout() {
        try {
            if (songsList != null && songsList.size() > 0) {
                if (adapter != null && adapter.getLongPress()) {
                    //if (adapter != null) adapter.setLongPress();
                    binding.layoutTopCheckBox.setVisibility(View.VISIBLE);
                    binding.layoutTopSearch.setVisibility(View.GONE);
                    binding.fab.setVisibility(View.VISIBLE);
                    if (checkBoxAllIsChecked()) binding.cbDelete.setChecked(true);
                } else {
                    binding.layoutTopCheckBox.setVisibility(View.GONE);
                    binding.layoutTopSearch.setVisibility(View.VISIBLE);
                    binding.fab.setVisibility(View.GONE);
                }
            } else {
                showNoDataView();
            }
            if (checkBoxIsAnyUnChecked()) binding.cbDelete.setChecked(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean checkBoxAllIsChecked() {
        try {
            for (SongEntity songEntity : songsList) {
                if (!songEntity.getIsChecked()) return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean checkBoxIsAnyUnChecked() {
        try {
            for (SongEntity songEntity : songsList) {
                if (!songEntity.getIsChecked()) return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void showNoDataView() {
        try {
            binding.layoutListView.setVisibility(android.view.View.GONE);
            binding.listView.setVisibility(android.view.View.GONE);
            binding.layoutScan.setVisibility(android.view.View.GONE);
            binding.layoutList.setVisibility(android.view.View.VISIBLE);
            binding.txtNoData.setVisibility(android.view.View.VISIBLE);
            binding.layoutTopCheckBox.setVisibility(android.view.View.GONE);
            binding.layoutTopSearch.setVisibility(android.view.View.GONE);
            binding.fab.setVisibility(android.view.View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sortList() {
        try {
            if (songsList != null && songsList.size() > 0) {
                Log.e("TAG", " selected " + selectedSortByRadio);
                Collections.sort(songsList, new SortComparator.SortByName.Ascending());
                if (selectedSortByRadio == 0) {
                    binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_nameA2Z));
                    Collections.sort(songsList, new SortComparator.SortByName.Ascending());
                } else if (selectedSortByRadio == 1) {
                    binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_nameZ2A));
                    Collections.sort(songsList, new SortComparator.SortByName.Descending());
                } else if (selectedSortByRadio == 2) {
                    binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_date_new));
                    Collections.sort(songsList, new SortComparator.SortByDate.Descending());
                } else if (selectedSortByRadio == 3) {
                    binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_date_old));
                    Collections.sort(songsList, new SortComparator.SortByDate.Ascending());
                } else if (selectedSortByRadio == 4) {
                    binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_duration_short));
                    Collections.sort(songsList, new SortComparator.SortByDuration.Ascending());
                } else if (selectedSortByRadio == 5) {
                    binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_duration_long));
                    Collections.sort(songsList, new SortComparator.SortByDuration.Descending());
                } else if (selectedSortByRadio == 6) {
                    binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_size_small));
                    Collections.sort(songsList, new SortComparator.SortBySize.Ascending());
                } else if (selectedSortByRadio == 7) {
                    binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_size_large));
                    Collections.sort(songsList, new SortComparator.SortBySize.Descending());
                } else {
                    songsList = FetchSongsFromLocal.getAllSongs(ScanFilesActivity.this);
                }

                notifyAdapter();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 1001 && resultCode == RESULT_OK) {
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clear() {
        try {
            binding.cbDelete.setChecked(false);
            if (songsList != null && songsList.size() > 0) {
                for (int i = 0; i < songsList.size(); i++) {
                    SongEntity songs = songsList.get(i);
                    songs.setIsChecked(false);
                    songsList.set(i, songs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        refreshView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            clear();
            setResult(Activity.RESULT_OK, new Intent());
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateSongsList(List<SongEntity> list) {
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addSongs(SongEntity entity) {
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}