package com.app.videoplayer.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewbinding.ViewBinding;

import com.app.videoplayer.AppController;
import com.app.videoplayer.R;
import com.app.videoplayer.entity.SongEntity;
import com.app.videoplayer.ui.IActivityContract;
import com.app.videoplayer.ui.activity.HomeActivity;
import com.app.videoplayer.ui.activity.ScanFilesActivity;
import com.app.videoplayer.ui.activity.SearchSongsActivity;
import com.app.videoplayer.adapter.FragmentSongsAdapter;
import com.app.videoplayer.databinding.FragmentSongsBinding;
import com.app.videoplayer.db.DBUtils;
import com.app.videoplayer.db.FetchSongsFromLocal;
import com.app.videoplayer.ui.presenter.FragmentSongPresenter;
import com.app.videoplayer.utils.AppUtils;
import com.app.videoplayer.utils.FileUtils1;
import com.app.mvpdemo.businessframe.mvp.fragment.BaseFragment;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FragmentSongs extends BaseFragment<FragmentSongPresenter> implements IActivityContract.IActivityView {
    String TAG = FragmentSongs.class.getSimpleName();
    FragmentSongsBinding binding;
    FragmentSongsAdapter adapter;
    FragmentSongPresenter presenter;
    List<SongEntity> songsList = new ArrayList<>();

    public static MutableLiveData<List<SongEntity>> scanSongList = new MutableLiveData<>();

    int selectedSortByRadio = 0;
    boolean isLocalFilesClick = false;
    boolean isKeyboardShowing = false;

    @Override
    protected ViewBinding getContentView() {
        binding = FragmentSongsBinding.inflate(getLayoutInflater());
        return binding;
    }

    @Override
    protected FragmentSongPresenter createPresenter(Context context) {
        presenter = new FragmentSongPresenter(context, this);
        presenter.setBinding(binding);
        return presenter;
    }

    @Override
    protected void initWidget(View root) {
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

            binding.txtSort.setOnClickListener(v -> {
                try {
                    if (songsList != null && songsList.size() > 0) {
                        new BottomSheetFragmentSortBy(selectedSortByRadio, selected -> {
                            Log.e("TAG", "selected" + selected);
                            selectedSortByRadio = selected;
                            Log.e("TAG", "selected" + selectedSortByRadio);
                            refreshView();
                        }).show(requireActivity().getSupportFragmentManager(), "BottomSheetFragmentSortBy");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            binding.imageViewSearch.setOnClickListener(v -> {
                if (songsList != null && songsList.size() > 0) {
                    Intent intent = new Intent(requireActivity(), SearchSongsActivity.class);
                    intent.putExtra("selectedSortByRadio", selectedSortByRadio);
                    requireActivity().startActivityForResult(intent, 1001);
                }
            });

            binding.fab.setOnClickListener(v -> {
                try {
                    showBottomMenu();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            HomeActivity.bindingHome.playScreenFrameLayout.setTag(HomeActivity.bindingHome.playScreenFrameLayout.getVisibility());
            HomeActivity.bindingHome.playScreenFrameLayout.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                try {
                    if (HomeActivity.bindingHome.playScreenFrameLayout.getVisibility() == View.VISIBLE) {
                        AppUtils.setMargins(binding.fab, 0, 0, (int) getResources().getDimension(R.dimen.fab_margin), (int) getResources().getDimension(com.intuit.sdp.R.dimen._55sdp));
                    } else {
                        AppUtils.setMargins(binding.fab, 0, 0, (int) getResources().getDimension(R.dimen.fab_margin), (int) getResources().getDimension(R.dimen.fab_margin));
                    }
                    int newVis = HomeActivity.bindingHome.playScreenFrameLayout.getVisibility();
                    if ((int) HomeActivity.bindingHome.playScreenFrameLayout.getTag() != newVis) {
                        HomeActivity.bindingHome.playScreenFrameLayout.setTag(HomeActivity.bindingHome.playScreenFrameLayout.getVisibility());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            binding.btnDelete.setOnClickListener(v -> {
                if (songsList != null && songsList.size() > 0 && isAnyChecked()) {
                    showDeleteAlertDialog();
                } else {
                    Toast.makeText(requireActivity(), getResources().getString(R.string.alert_saved_error_msg), Toast.LENGTH_SHORT).show();
                }
            });

            // add changed listener
            scanSongList.observe(this, (Observer) o -> {
                if (scanSongList != null && scanSongList.getValue().size() > 0) {
                    insertSongsInDb(scanSongList.getValue());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initData() {
        try {
            refreshView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final BroadcastReceiver deletePlaySongReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getAction() != null && intent.getAction().equals("DeletePlaySong")) {
                    refreshList();
                    if (HomeActivity.bindingHome.playScreenFrameLayout.getVisibility() == View.VISIBLE) {
                        Intent updateDataBroadCast = new Intent("SongListAfterDelete");
                        AppController.getSpSongInfo().edit().putString("songList", new Gson().toJson(songsList)).apply();
                        requireActivity().sendBroadcast(updateDataBroadCast);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private final BroadcastReceiver playSongReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getAction() != null && intent.getAction().equals("GetPlaySong")) {
                    notifyAdapter();
//                    if (AppController.getSpSongInfo().getBoolean("isPlaying", false) && !AppController.getSpSongInfo().getString("CurrentSong", "").isEmpty()) {
//                        if (adapter != null)
//                            adapter.setPlayerInfo(new Gson().fromJson(AppController.getSpSongInfo().getString("CurrentSong", ""), SongEntity.class), true);
//                    } else {
//                        adapter.setPlayerInfo(null, false);
//                    }
//                    AppController.getSpSongInfo().edit().clear().apply();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void registerEvent() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.registerReceiver(requireActivity(), playSongReceiver, new IntentFilter("GetPlaySong"), ContextCompat.RECEIVER_NOT_EXPORTED);
                ContextCompat.registerReceiver(requireActivity(), deletePlaySongReceiver, new IntentFilter("DeletePlaySong"), ContextCompat.RECEIVER_NOT_EXPORTED);
            } else {
                requireActivity().registerReceiver(playSongReceiver, new IntentFilter("GetPlaySong"));
                requireActivity().registerReceiver(deletePlaySongReceiver, new IntentFilter("DeletePlaySong"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void unRegisterEvent() {
        try {
            try {
                if (playSongReceiver != null)
                    requireActivity().unregisterReceiver(playSongReceiver);
                if (deletePlaySongReceiver != null)
                    requireActivity().unregisterReceiver(deletePlaySongReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void refreshView() {
        try {
            refreshList();
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
    public void setListView() {
        try {
            binding.listView.setHasFixedSize(true);
            binding.listView.setVerticalScrollBarEnabled(true);
            binding.listView.setLayoutManager(new GridLayoutManager(requireActivity(), 2, LinearLayoutManager.VERTICAL, false));
            binding.listView.setItemAnimator(new DefaultItemAnimator());

            binding.layoutListView.setVisibility(View.VISIBLE);
            binding.listView.setVisibility(View.VISIBLE);
            binding.layoutTopListView.setVisibility(View.VISIBLE);
            binding.layoutNoData.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showListView() {
        try {
            setListView();
            adapter = new FragmentSongsAdapter(requireActivity(), new FragmentSongsAdapter.SongsClickListener() {

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
//                            AppUtils.setMargins(binding.fab, 0, 0, (int) getResources().getDimension(R.dimen.fab_margin), (int) getResources().getDimension(com.intuit.sdp.R.dimen._55sdp));
//                            AppUtils.setMargins(binding.fabDelete, 0, 0, (int) getResources().getDimension(R.dimen.fab_margin), (int) getResources().getDimension(com.intuit.sdp.R.dimen._55sdp));
//                            HomeActivity.bindingHome.playScreenFrameLayout.setVisibility(View.VISIBLE);
//                            //HomeActivity.fragmentPlayer.setPlayMotionFullScreen();
//                        }
//                        Intent updateDataBroadCast = new Intent("SongClick");
//                        AppController.getSpSongInfo().edit().putInt("position", position).putString("songList", new Gson().toJson(songsList)).apply();
//                        requireActivity().sendBroadcast(updateDataBroadCast);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onSongLongClick(SongEntity result, int position) {
                    if (isKeyboardShowing) {
                        AppUtils.closeKeyboard(requireActivity());
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
    public void notifyAdapter() {
        try {
            if (binding.listView.getAdapter() != null)
                binding.listView.getAdapter().notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showNoDataView() {
        try {
            binding.layoutListView.setVisibility(View.GONE);
            binding.listView.setVisibility(View.GONE);
            binding.layoutTopListView.setVisibility(View.GONE);
            binding.layoutNoData.setVisibility(View.VISIBLE);
            binding.layoutBottomButton.setVisibility(View.GONE);
            binding.layoutTopCheckBox.setVisibility(View.GONE);
            binding.fab.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshList() {
        try {
            if (songsList != null) songsList.clear();
            Log.e("TAG", "refreshList selected" + selectedSortByRadio);

            if (selectedSortByRadio == 0) {
                binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_nameA2Z));
                songsList = DBUtils.getAllSongByNameAsc();
            } else if (selectedSortByRadio == 1) {
                binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_nameZ2A));
                songsList = DBUtils.getAllSongByNameDesc();
            } else if (selectedSortByRadio == 2) {
                binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_date_new));
                songsList = DBUtils.getAllSongByDateDesc();
            } else if (selectedSortByRadio == 3) {
                binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_date_old));
                songsList = DBUtils.getAllSongByDateAsc();
            } else if (selectedSortByRadio == 4) {
                binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_duration_short));
                songsList = DBUtils.getAllSongByDurationAsc();
            } else if (selectedSortByRadio == 5) {
                binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_duration_long));
                songsList = DBUtils.getAllSongByDurationDesc();
            } else if (selectedSortByRadio == 6) {
                binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_size_small));
                songsList = DBUtils.getAllSongBySizeAsc();
            } else if (selectedSortByRadio == 7) {
                binding.txtSort.setText(getResources().getString(R.string.txt_sort_by_size_large));
                songsList = DBUtils.getAllSongBySizeDesc();
            } else {
                songsList = DBUtils.getAllSongs();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void isFileExist() {
        try {
            for (SongEntity songEntity : DBUtils.getAllSongs()) {
                Log.e("TAG", "file exist" + songEntity.getData());
                //String path = FileUtils1.getPath(requireActivity(), Uri.parse(songEntity.getData()));
                File file = new File(songEntity.getData());
                Log.e("TAG", "file exist" + file.exists());
            }
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

    private void showDeleteAlertDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
            builder.setMessage(getResources().getString(R.string.alert_trash));
            builder.setTitle(R.string.app_name);
            builder.setCancelable(false);
            builder.setPositiveButton(getResources().getString(R.string.txt_yes), (dialog, id) -> {
                int songListCount = songsList.size();
                int deleteCount = songsList.stream().filter(SongEntity::getIsChecked).collect(Collectors.toList()).size();
                DBUtils.trashMultipleSongs(songsList.stream().filter(SongEntity::getIsChecked).collect(Collectors.toList()), new DBUtils.TaskComplete() {
                    @Override
                    public void onTaskComplete() {
                        try {
                            requireActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        refreshView();
                                        if (HomeActivity.bindingHome.playScreenFrameLayout.getVisibility() == View.VISIBLE) {
                                            Intent updateDataBroadCast = new Intent("SongListAfterDelete");
                                            AppController.getSpSongInfo().edit().putString("songList", new Gson().toJson(songsList)).apply();
                                            requireActivity().sendBroadcast(updateDataBroadCast);
                                        }
                                        Toast.makeText(requireActivity(), deleteCount + " " + getResources().getString(R.string.alert_trash_msg), Toast.LENGTH_SHORT).show();
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
                    binding.layoutTopListView.setVisibility(View.GONE);
                    binding.fab.setVisibility(View.GONE);
                    binding.layoutBottomButton.setVisibility(View.VISIBLE);
                    binding.layoutTopCheckBox.setVisibility(View.VISIBLE);
                    HomeActivity.bindingHome.playScreenFrameLayout.setVisibility(View.GONE);
                    if (checkBoxAllIsChecked()) binding.cbDelete.setChecked(true);
                } else {
                    binding.layoutTopListView.setVisibility(View.VISIBLE);
                    binding.fab.setVisibility(View.VISIBLE);
                    binding.layoutBottomButton.setVisibility(View.GONE);
                    binding.layoutTopCheckBox.setVisibility(View.GONE);
                    if (HomeActivity.fragmentPlayer.playSongEntity != null) {
                        HomeActivity.bindingHome.playScreenFrameLayout.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                binding.layoutTopListView.setVisibility(View.VISIBLE);
                binding.fab.setVisibility(View.VISIBLE);
                binding.layoutBottomButton.setVisibility(View.GONE);
                binding.layoutTopCheckBox.setVisibility(View.GONE);
                if (HomeActivity.fragmentPlayer.playSongEntity != null) {
                    HomeActivity.bindingHome.playScreenFrameLayout.setVisibility(View.VISIBLE);
                }
            }
            if (checkBoxIsAnyUnChecked()) binding.cbDelete.setChecked(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openStorageFiles() {
        try {
            if (isLocalFilesClick) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                intent.setType("video/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
                startActivityForResult(Intent.createChooser(intent, "Select Video"), 222);
            } else {
                requireActivity().startActivityForResult(new Intent(requireActivity(), ScanFilesActivity.class), 1001);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showBottomMenu() {
        try {
            new BottomSheetFragmentMenu(new BottomSheetFragmentMenu.BottomSheetClick() {
                @Override
                public void onLocalFilesClick() {
                    try {
                        isLocalFilesClick = true;
                        if (AppUtils.allPermissionsGranted(requireActivity())) {
                            openStorageFiles();
                        } else {
                            AppUtils.getRuntimePermissions(requireActivity());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onScanFilesClick() {
                    isLocalFilesClick = false;
                    if (AppUtils.allPermissionsGranted(requireActivity())) {
                        openStorageFiles();
                    } else {
                        AppUtils.getRuntimePermissions(requireActivity());
                    }
                }
            }).show(requireActivity().getSupportFragmentManager(), "BottomSheetMenuFragment");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 222) {
                if (null != data) { // checking empty selection
                    if (null != data.getClipData()) { // checking multiple selection or not
                        for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                            Uri uri = data.getClipData().getItemAt(i).getUri();
                            try {
//                                MediaScannerConnection.scanFile(requireActivity(), new String[]{FileUtils1.getPath(requireActivity(), uri)}, null, (path, uri1) -> {
//                                    try {
//                                        requireActivity().runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                try {
//                                                    DBUtils.insertMultipleSongs(FetchSongsFromLocal.getSelectedSongs(requireActivity(), uri1));
//                                                    clear();
//                                                    if (adapter != null) adapter.clearLongPress();
//                                                    refreshView();
//                                                } catch (Exception e) {
//                                                    e.printStackTrace();
//                                                }
//                                            }
//                                        });
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//                                });
                                DBUtils.insertMultipleSongs(FetchSongsFromLocal.getSelectedSongs(requireActivity(), FileUtils1.getPath(requireActivity(), uri)), new DBUtils.TaskComplete() {
                                    @Override
                                    public void onTaskComplete() {

                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        Uri uri = data.getData();
                        try {
//                            MediaScannerConnection.scanFile(requireActivity(), new String[]{FileUtils1.getPath(requireActivity(), uri)}, null, (path, uri1) -> {
//                                try {
//                                    requireActivity().runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            try {
//                                                DBUtils.insertMultipleSongs(FetchSongsFromLocal.getSelectedSongs(requireActivity(), uri1));
//                                                clear();
//                                                if (adapter != null) adapter.clearLongPress();
//                                                refreshView();
//                                            } catch (Exception e) {
//                                                e.printStackTrace();
//                                            }
//                                        }
//                                    });
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            });
                            DBUtils.insertMultipleSongs(FetchSongsFromLocal.getSelectedSongs(requireActivity(), FileUtils1.getPath(requireActivity(), uri)), new DBUtils.TaskComplete() {
                                @Override
                                public void onTaskComplete() {

                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            clear();
            if (adapter != null) adapter.clearLongPress();
            refreshView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        openStorageFiles();
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
    public void onDestroyView() {
        super.onDestroyView();
        try {
            unRegisterEvent();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
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

    public void onSourceFileDelete() {
        try {
            refreshView();
            if (HomeActivity.bindingHome.playScreenFrameLayout.getVisibility() == View.VISIBLE) {
                Intent updateDataBroadCast = new Intent("SongListAfterDelete");
                AppController.getSpSongInfo().edit().putString("songList", new Gson().toJson(songsList)).apply();
                requireActivity().sendBroadcast(updateDataBroadCast);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void insertSongsInDb(List<SongEntity> songs) {
        try {
            Log.e("TAG", "insertSongsInDb size " + songs.size());
            binding.layoutDataInsert.setVisibility(View.VISIBLE);
            binding.circularProgressbar.setProgress(0);   // Main Progress
            binding.circularProgressbar.setSecondaryProgress(100); // Secondary Progress
            binding.circularProgressbar.setMax(100); // Maximum Progress
            int moveCount = songs.size();
            for (int i = 0; i < songs.size(); i++) {
                Log.e("TAG", "i " + i);
                SongEntity song = songs.get(i);
                song.setIsChecked(false);
                song.setIsTrashed(false);
                if (!DBUtils.checkSongIsExistInDB(song.getSongId())) {
                    AppController.getDaoSession().getSongEntityDao().save(song);
                } else {
                    DBUtils.restoreSongIsExistInDB(song.getSongId());
                }
                int update = ((i + 1) / moveCount) * 100;
                Log.e("TAG", "update " + update);
                binding.circularProgressbar.setProgress(update);
                binding.tv.setText(update + "%");

//                try {
//                    Thread.sleep(1000);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

            }
            //Toast.makeText(context, moveCount + " " + context.getResources().getString(R.string.alert_saved_msg), Toast.LENGTH_SHORT).show();
            binding.layoutDataInsert.setVisibility(View.GONE);

//            DBUtils.insertMultipleSongs(songs, new DBUtils.TaskComplete() {
//                @Override
//                public void onTaskComplete() {
//                    try {
////                        runOnUiThread(new Runnable() {
////                            @Override
////                            public void run() {
////                                try {
////                                    setResult(Activity.RESULT_OK, new Intent());
////                                    finish();
////                                    Toast.makeText(getApplicationContext(), moveCount + " " + getResources().getString(R.string.alert_saved_msg), Toast.LENGTH_SHORT).show();
////                                } catch (Exception e) {
////                                    e.printStackTrace();
////                                }
////                            }
////                        });
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}