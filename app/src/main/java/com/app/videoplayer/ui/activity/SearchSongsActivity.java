package com.app.videoplayer.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewbinding.ViewBinding;

import com.app.videoplayer.R;
import com.app.videoplayer.adapter.FragmentSongsAdapter;
import com.app.videoplayer.databinding.ActivitySearchSongsBinding;
import com.app.videoplayer.db.DBUtils;
import com.app.videoplayer.entity.SongEntity;
import com.app.videoplayer.entity.SongEntityDao;
import com.app.videoplayer.ui.IActivityContract;
import com.app.videoplayer.ui.presenter.SearchSongsActivityPresenter;
import com.app.videoplayer.utils.AppUtils;
import com.app.mvpdemo.businessframe.mvp.activity.BaseActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SearchSongsActivity extends BaseActivity<SearchSongsActivityPresenter> implements IActivityContract.IActivityView {

    ActivitySearchSongsBinding binding;
    SearchSongsActivityPresenter presenter;
    FragmentSongsAdapter adapter;
    String TAG = SearchSongsActivity.class.getSimpleName();
    List<SongEntity> songsList = new ArrayList<>();
    boolean isKeyboardShowing = false;

    int selectedSortByRadio = 0;

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
        binding = ActivitySearchSongsBinding.inflate(getLayoutInflater());
        return binding;
    }

    @Override
    protected SearchSongsActivityPresenter createPresenter(Context context) {
        presenter = new SearchSongsActivityPresenter(context, this);
        presenter.setBinding(binding);
        return presenter;
    }

    @Override
    protected void setStatusBar() {
        try {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initWidget() {
        try {
            binding.etSearch.mSearchActivity = this;
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(binding.etSearch, InputMethodManager.SHOW_IMPLICIT);

            if (getIntent() != null && getIntent().hasExtra("selectedSortByRadio")) {
                selectedSortByRadio = getIntent().getIntExtra("selectedSortByRadio", 0);
            }

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
                            checkBottomLayout();
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
//                        checkBottomLayout();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            });

            binding.txtDone.setOnClickListener(v -> {
                try {
                    clear();
                    if (adapter != null) adapter.clearLongPress();
                    checkBottomLayout();
//                    if (isKeyboardShowing) {
//                        AppUtils.closeKeyboard(this);
//                    } else {
//                        clear();
//                        setResult(Activity.RESULT_OK, new Intent());
//                        finish();
//                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            binding.imageViewBack.setOnClickListener(v -> {
                AppUtils.hideKeyboardOnClick(SearchSongsActivity.this, v);
                clear();
                finish();
            });

            binding.etSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try {
                        getSearchedList(s.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            binding.btnDelete.setOnClickListener(v -> {
                if (songsList != null && songsList.size() > 0 && isAnyChecked()) {
                    showDeleteAlertDialog();
                } else {
                    Toast.makeText(SearchSongsActivity.this, getResources().getString(R.string.alert_saved_error_msg), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initData() {
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void registerEvent() {
        try {
            registerReceiver(playSongReceiver, new IntentFilter("GetPlaySong"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void unRegisterEvent() {
        try {
            if (playSongReceiver != null)
                registerReceiver(playSongReceiver, new IntentFilter("GetPlaySong"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final BroadcastReceiver playSongReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getAction() != null && intent.getAction().equals("GetPlaySong")) {
                    notifyAdapter();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

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
            binding.listView.setLayoutManager(new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false));
            binding.listView.setItemAnimator(new DefaultItemAnimator());

            binding.layoutListView.setVisibility(android.view.View.VISIBLE);
            binding.listView.setVisibility(android.view.View.VISIBLE);
            binding.txtNoData.setVisibility(android.view.View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showListView() {
        try {
            setListView();
            adapter = new FragmentSongsAdapter(SearchSongsActivity.this, new FragmentSongsAdapter.SongsClickListener() {

                @Override
                public void deleteSongs(SongEntity result, boolean isChecked, int position) {
                    try {
                        checkBottomLayout();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onSongsClick(SongEntity result, int position) {
                    try {
//                        if (HomeActivity.bindingHome.playScreenFrameLayout.getVisibility() == View.GONE) {
//                            HomeActivity.bindingHome.playScreenFrameLayout.setVisibility(View.VISIBLE);
//                        }
//                        Intent updateDataBroadCast = new Intent("SongClick");
//                        AppController.getSpSongInfo().edit().putInt("position", position).putString("songList", new Gson().toJson(songsList)).apply();
//                        sendBroadcast(updateDataBroadCast);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onSongLongClick(SongEntity result, int position) {
//                    Intent intent = new Intent(SearchSongsActivity.this, DeleteSongsActivity.class);
//                    intent.putExtra("selectedSortByRadio", 0);
//                    intent.putExtra("searchText", binding.etSearch.getText().toString());
//                    startActivityForResult(intent, 101);
                    //startActivityForResult(new Intent(SearchSongsActivity.this, DeleteSongsActivity.class), 101);

                    if (isKeyboardShowing) {
                        AppUtils.closeKeyboard(SearchSongsActivity.this);
                    }
                    checkBottomLayout();
                }
            });
            binding.listView.setAdapter(adapter);
            adapter.submitList(songsList);
            checkBottomLayout();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showNoDataView() {
        try {
            binding.layoutListView.setVisibility(android.view.View.GONE);
            binding.listView.setVisibility(android.view.View.GONE);
            if (binding.etSearch.getText().toString().isEmpty()) {
                binding.txtNoData.setVisibility(android.view.View.GONE);
            } else {
                binding.txtNoData.setVisibility(android.view.View.VISIBLE);
            }
            binding.layoutBottomButton.setVisibility(android.view.View.GONE);
            binding.layoutTopCheckBox.setVisibility(android.view.View.GONE);
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

    private void checkBottomLayout() {
        try {
            if (songsList != null && songsList.size() > 0) {
                if (adapter != null && adapter.getLongPress()) {
                    //if (adapter != null) adapter.setLongPress();
                    binding.layoutBottomButton.setVisibility(View.VISIBLE);
                    binding.layoutTopCheckBox.setVisibility(View.VISIBLE);
                    if (checkBoxAllIsChecked()) binding.cbDelete.setChecked(true);
                } else {
                    binding.layoutBottomButton.setVisibility(View.GONE);
                    binding.layoutTopCheckBox.setVisibility(View.GONE);
                }
            } else {
                binding.layoutBottomButton.setVisibility(View.GONE);
                binding.layoutTopCheckBox.setVisibility(View.GONE);
            }
            if (checkBoxIsAnyUnChecked()) binding.cbDelete.setChecked(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isAnyChecked() {
        try {
            for (SongEntity songEntity : songsList) {
                if (songEntity.getIsChecked()) return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void refreshList(String text) {
        try {
            if (songsList != null) songsList.clear();
            Log.e("TAG", "refreshList selected" + selectedSortByRadio);
            if (selectedSortByRadio == 0) {
                songsList = DBUtils.getSearchSongsByName(text, "ASC", SongEntityDao.Properties.Title);
            } else if (selectedSortByRadio == 1) {
                songsList = DBUtils.getSearchSongsByName(text, "", SongEntityDao.Properties.Title);
            } else if (selectedSortByRadio == 2) {
                songsList = DBUtils.getSearchSongsByName(text, "", SongEntityDao.Properties.DateAdded);
            } else if (selectedSortByRadio == 3) {
                songsList = DBUtils.getSearchSongsByName(text, "ASC", SongEntityDao.Properties.DateAdded);
            } else if (selectedSortByRadio == 4) {
                songsList = DBUtils.getSearchSongsByName(text, "ASC", SongEntityDao.Properties.Duration);
            } else if (selectedSortByRadio == 5) {
                songsList = DBUtils.getSearchSongsByName(text, "", SongEntityDao.Properties.Duration);
            } else if (selectedSortByRadio == 6) {
                songsList = DBUtils.getSearchSongsByName(text, "ASC", SongEntityDao.Properties.Size);
            } else if (selectedSortByRadio == 7) {
                songsList = DBUtils.getSearchSongsByName(text, "", SongEntityDao.Properties.Size);
            } else {
                songsList = DBUtils.getSearchSongsByName(text, "ASC", SongEntityDao.Properties.Title);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getSearchedList(String text) {
        try {
            if (text != null && !text.isEmpty()) {
                if (songsList != null) songsList.clear();
                refreshList(text);
                refreshView();
            } else {
                if (songsList != null) songsList.clear();
                refreshView();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showDeleteAlertDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getResources().getString(R.string.alert_trash));
            builder.setTitle(R.string.app_name);
            builder.setCancelable(false);
            builder.setPositiveButton(getResources().getString(R.string.txt_yes), (dialog, id) -> {
                int deleteCount = songsList.stream().filter(SongEntity::getIsChecked).collect(Collectors.toList()).size();
                DBUtils.trashMultipleSongs(songsList.stream().filter(SongEntity::getIsChecked).collect(Collectors.toList()), new DBUtils.TaskComplete() {
                    @Override
                    public void onTaskComplete() {
                        try {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Intent updatePlayBroadCast = new Intent("DeletePlaySong");
                                        sendBroadcast(updatePlayBroadCast);
                                        getSearchedList(binding.etSearch.getText().toString());
                                        Toast.makeText(getApplicationContext(), deleteCount + " " + getResources().getString(R.string.alert_trash_msg), Toast.LENGTH_SHORT).show();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }).setNegativeButton(getResources().getString(R.string.txt_no), (dialog, id) -> dialog.cancel());
            builder.create().show();
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
        try {
            getSearchedList(binding.etSearch.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            clear();
            unRegisterEvent();
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