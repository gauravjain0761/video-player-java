package com.app.videoplayer.ui.fragment;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.SeekBar;

import androidx.core.content.ContextCompat;
import androidx.viewbinding.ViewBinding;

import com.app.videoplayer.AppController;
import com.app.videoplayer.R;
import com.app.videoplayer.entity.SongEntity;
import com.app.videoplayer.ui.IActivityContract;
import com.app.videoplayer.ui.activity.HomeActivity;
import com.app.videoplayer.databinding.FragmentPlayerBinding;
import com.app.videoplayer.ui.presenter.FragmentPlayerPresenter;
import com.app.videoplayer.utils.AppUtils;
import com.app.videoplayer.utils.ImageUtil;
import com.app.videoplayer.utils.visualizer.utils.TunnelPlayerWorkaround;
import com.app.mvpdemo.businessframe.mvp.fragment.BaseFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FragmentPlayer extends BaseFragment<FragmentPlayerPresenter> implements IActivityContract.IActivityView {
    String TAG = FragmentPlayer.class.getSimpleName();
    FragmentPlayerBinding binding;
    FragmentPlayerPresenter presenter;
    public MediaPlayer mediaPlayer = new MediaPlayer();
    Handler handler = new Handler();
    public List<SongEntity> songsList = new ArrayList<>();
    public List<SongEntity> originalSongsList = new ArrayList<>();
    int position = 0;
    public SongEntity playSongEntity;
    ObjectAnimator objectAnimator;
    int playMode = 1;//1 for loop-all,2 for single,3 for random

    @Override
    protected ViewBinding getContentView() {
        binding = FragmentPlayerBinding.inflate(getLayoutInflater());
        return binding;
    }

    @Override
    protected FragmentPlayerPresenter createPresenter(Context context) {
        presenter = new FragmentPlayerPresenter(context, this);
        presenter.setBinding(binding);
        return presenter;
    }

    @Override
    protected void initWidget(View root) {
        try {
            initTunnelPlayerWorkaround();

            try {
                ObjectAnimator.class.getMethod("setDurationScale", float.class).invoke(null, 1f);
            } catch (Throwable t) {
                Log.e(TAG, t.getMessage());
            }

            try {
                ValueAnimator.class.getMethod("setDurationScale", float.class).invoke(null, 1f);
            } catch (Throwable t) {
                Log.e(TAG, t.getMessage());
            }

            try {
                objectAnimator = ObjectAnimator.ofFloat(binding.imageViewRotate, View.ROTATION, 0f, 360f).setDuration(5000);
                objectAnimator.setInterpolator(new LinearInterpolator());
                objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
            } catch (Throwable t) {
                Log.e(TAG, t.getMessage());
            }

            binding.collapseImageView.setOnClickListener(v -> {
                try {
                    binding.playerMotion.transitionToCollapse();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            binding.closeImageView.setOnClickListener(v -> {
                try {
                    closeMediaPlayer();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            binding.btnPlayMode.setOnClickListener(v -> {
                try {
                    Log.e("TAG", "playMode clicked" + playMode);

                    //String item = ThreadLocalRandom.current().ints(0, songsList.size()).distinct().limit(1).asLongStream().toString();
                    //Log.e("TAG", "playMode clicked" + item);

                    if (playMode == 1) {
                        playMode = 2;
                    } else if (playMode == 2) {
                        playMode = 3;
                    } else if (playMode == 3) {
                        playMode = 1;
                    }
                    setMediaPlayerPlayMode();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            binding.playPauseImageView.setOnClickListener(v -> startStopOnClick());
            binding.imageViewRotate.setOnClickListener(v -> startStopOnClick());

            binding.btnNext.setOnClickListener(v -> {
                try {
                    playNextSong();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            binding.btnPrevious.setOnClickListener(v -> {
                try {
                    playPreviousSong();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initData() {
        try {
            setMediaPlayerPlayMode();
            binding.playerMotion.transitionToEnd();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void registerEvent() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.registerReceiver(requireActivity(), songClickReceiver, new IntentFilter("SongClick"), ContextCompat.RECEIVER_NOT_EXPORTED);
                ContextCompat.registerReceiver(requireActivity(), songListAfterDeleteReceiver, new IntentFilter("SongListAfterDelete"), ContextCompat.RECEIVER_NOT_EXPORTED);
            } else {
                requireActivity().registerReceiver(songClickReceiver, new IntentFilter("SongClick"));
                requireActivity().registerReceiver(songListAfterDeleteReceiver, new IntentFilter("SongListAfterDelete"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void unRegisterEvent() {
        try {
            if (songClickReceiver != null) requireActivity().unregisterReceiver(songClickReceiver);
            if (songListAfterDeleteReceiver != null)
                requireActivity().unregisterReceiver(songListAfterDeleteReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final BroadcastReceiver songClickReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getAction() != null && intent.getAction().equals("SongClick")) {
                    if (songsList != null) songsList.clear();
                    if (originalSongsList != null) originalSongsList.clear();
                    position = AppController.getSpSongInfo().getInt("position", 0);
                    songsList = new Gson().fromJson(AppController.getSpSongInfo().getString("songList", ""), new TypeToken<List<SongEntity>>() {
                    }.getType());
                    originalSongsList = new Gson().fromJson(AppController.getSpSongInfo().getString("songList", ""), new TypeToken<List<SongEntity>>() {
                    }.getType());
                    AppController.getSpSongInfo().edit().clear().apply();

                    if (songsList != null && songsList.size() > 0 && songsList.size() > position) {
//                        try {
//                            binding.playerMotion.transitionToEnd();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
                        setMediaPlayer(songsList.get(position));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private final BroadcastReceiver songListAfterDeleteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getAction() != null && intent.getAction().equals("SongListAfterDelete")) {
                    if (songsList != null) songsList.clear();
                    if (originalSongsList != null) originalSongsList.clear();
                    songsList = new Gson().fromJson(AppController.getSpSongInfo().getString("songList", ""), new TypeToken<List<SongEntity>>() {
                    }.getType());
                    originalSongsList = new Gson().fromJson(AppController.getSpSongInfo().getString("songList", ""), new TypeToken<List<SongEntity>>() {
                    }.getType());

                    boolean isSongDelete = true;
                    if (playSongEntity != null) {
                        for (SongEntity model : songsList) {
                            if (model.getId() == playSongEntity.getId()) {
                                isSongDelete = false;
                            }
                        }
                    }

                    Log.e("songsList", "on delete songsList size " + songsList.size());


                    if (songsList != null && songsList.size() > 0) {
                        if (isSongDelete) {
                            if (position > songsList.size()) {
//                                position = songsList.size() - 1;
                                position = 0;
                            }
                            playDeleteNextSong();
                        }
                    } else {
                        closeMediaPlayer();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void refreshView() {
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showListView() {
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showNoDataView() {
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void notifyAdapter() {

    }

    @Override
    public void setListView() {

    }

    private void initTunnelPlayerWorkaround() {
        // Read "tunnel.decode" system property to determine
        // the workaround is needed
        if (TunnelPlayerWorkaround.isTunnelDecodeEnabled(requireActivity())) {
            TunnelPlayerWorkaround.createSilentMediaPlayer(requireActivity());
        }
    }

    private void playNextSong() {
        try {
            if (songsList != null && position != songsList.size() - 1) {
                position = position + 1;
                if (songsList.size() > 0 && songsList.size() > position) {
                    setMediaPlayer(songsList.get(position));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playDeleteNextSong() {
        try {
            if (songsList != null && songsList.size() > 0 && songsList.size() > position) {
                setMediaPlayer(songsList.get(position));
                startStopOnClick();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playPreviousSong() {
        try {
            if (songsList != null && position > 0) {
                position = position - 1;
                if (songsList != null && songsList.size() > 0 && songsList.size() > position) {
                    setMediaPlayer(songsList.get(position));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMediaPlayerPlayMode() {
        try {
            Log.e("TAG", "playMode setMediaPlayerPlayMode" + playMode);
            //int playMode = 1;//1 for loop-all,2 for single,3 for random
            if (mediaPlayer != null) {
                if (playMode == 1) {
                    mediaPlayer.setLooping(false);
                    binding.btnPlayMode.setImageResource(R.drawable.player_ic_playmode_loop_all);
                    if (songsList != null) songsList.clear();
                    if (songsList != null && originalSongsList != null)
                        songsList.addAll(originalSongsList);
                } else if (playMode == 2) {
                    mediaPlayer.setLooping(true);
                    binding.btnPlayMode.setImageResource(R.drawable.player_ic_playmode_single_loop);
                    if (songsList != null) songsList.clear();
                    if (songsList != null && originalSongsList != null)
                        songsList.addAll(originalSongsList);
                } else if (playMode == 3) {
                    mediaPlayer.setLooping(false);
                    binding.btnPlayMode.setImageResource(R.drawable.player_ic_playmode_random);
                    if (songsList != null) Collections.shuffle(songsList);
                }
            }

            Log.e("TAG", "playMode setMediaPlayerPlayMode" + songsList.size());
            Log.e("TAG", "playMode setMediaPlayerPlayMode" + mediaPlayer.isLooping());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startStopOnClick() {
        try {
            try {
                if (mediaPlayer != null) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        //binding.musicPlayerView.stop();
                        if (objectAnimator != null) objectAnimator.pause();
                        //binding.visualizerView.hide();
                        binding.playPauseImageView.setImageResource(R.drawable.player_ic_play);

                        Intent updatePlayBroadCast = new Intent("GetPlaySong");
                        AppController.getSpSongInfo().edit().putBoolean("isPlaying", false).putString("CurrentSong", "").apply();
                        AppController.getSpSearchSongInfo().edit().putBoolean("isPlayingSearch", false).putString("CurrentSongSearch", "").apply();
                        requireActivity().sendBroadcast(updatePlayBroadCast);
                    } else {
                        mediaPlayer.start();
                        //binding.musicPlayerView.start();
                        if (objectAnimator != null) objectAnimator.resume();
                        //binding.visualizerView.show();
                        binding.playPauseImageView.setImageResource(R.drawable.player_ic_pause);

                        if (playSongEntity != null) {
                            Intent updatePlayBroadCast = new Intent("GetPlaySong");
                            AppController.getSpSongInfo().edit().putBoolean("isPlaying", true).putString("CurrentSong", new Gson().toJson(playSongEntity)).apply();
                            AppController.getSpSearchSongInfo().edit().putBoolean("isPlayingSearch", true).putString("CurrentSongSearch", new Gson().toJson(playSongEntity)).apply();
                            requireActivity().sendBroadcast(updatePlayBroadCast);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setMediaPlayer(SongEntity songEntity) {
        try {
            Log.e("TAG", "setMediaPlayer called");
            HomeActivity.bindingHome.playScreenFrameLayout.setVisibility(View.VISIBLE);
            playSongEntity = songEntity;
            binding.audioNameTextView.setText("" + (("" + songEntity.getTitle()).replace("null", "").replace("Null", "")));
            binding.artistNameTextView.setText("" + (("" + songEntity.getArtistName()).replace("null", "").replace("Null", "")));
            binding.audioNameTextViewMin.setText("" + (("" + songEntity.getTitle()).replace("null", "").replace("Null", "")));
            binding.artistNameTextViewMin.setText("" + (("" + songEntity.getArtistName()).replace("null", "").replace("Null", "")));

            if (("" + (("" + songEntity.getBitmapCover()).replace("null", "").replace("Null", ""))).isEmpty()) {
                binding.visualizerView.setImageResource(R.drawable.icv_songs);
            } else {
                binding.visualizerView.setImageBitmap(ImageUtil.convertToBitmap(songEntity.getBitmapCover()));
            }

            try {
                if (("" + (("" + songEntity.getBitmapCover()).replace("null", "").replace("Null", ""))).isEmpty()) {
                    binding.imageViewRotate.setImageDrawable(new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.icv_songs)));
                } else {
                    binding.imageViewRotate.setImageDrawable(new BitmapDrawable(getResources(), ImageUtil.convertToBitmap(songEntity.getBitmapCover())));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

//            try {
//                if (("" + (("" + songModel.getBitmapCover()).replace("null", "").replace("Null", ""))).isEmpty()) {
//                    binding.musicPlayerView.setCoverDrawable(new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.ic_medieview)));
//                } else {
//                    binding.musicPlayerView.setCoverDrawable(new BitmapDrawable(getResources(), ImageUtil.convertToBitmap(songModel.getBitmapCover())));
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
            }
            mediaPlayer = null;
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(songEntity.getData());
            mediaPlayer.prepare();
            mediaPlayer.start();
            if (objectAnimator != null) objectAnimator.start();
            //binding.musicPlayerView.start();
            binding.playPauseImageView.setImageResource(R.drawable.player_ic_pause);
            setAudioProgress();

            mediaPlayer.setOnCompletionListener(mp -> {
                try {
                    if (objectAnimator != null) objectAnimator.pause();
                    playNextSong();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            Intent updatePlayBroadCast = new Intent("GetPlaySong");
            AppController.getSpSongInfo().edit().putBoolean("isPlaying", true).putString("CurrentSong", new Gson().toJson(songEntity)).apply();
            AppController.getSpSearchSongInfo().edit().putBoolean("isPlayingSearch", true).putString("CurrentSongSearch", new Gson().toJson(songEntity)).apply();
            requireActivity().sendBroadcast(updatePlayBroadCast);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setAudioProgress() {
        try {
            binding.seekDurationTextView.setText(AppUtils.secondToTime(((int) mediaPlayer.getDuration()) / 1000));
            binding.seekTimeTextView.setText(AppUtils.secondToTime(((int) mediaPlayer.getCurrentPosition()) / 1000));

            //binding.musicPlayerView.setMax(mediaPlayer.getDuration());
            binding.seekBar.setProgress(0);
            binding.seekBar.setMax(mediaPlayer.getDuration());

            binding.seekBarMin.setProgress(0);
            binding.seekBarMin.setMax(mediaPlayer.getDuration());

            binding.seekBarMin.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    try {
                        if (mediaPlayer != null && fromUser) {
                            mediaPlayer.seekTo(progress);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    try {
                        if (mediaPlayer != null && fromUser) {
                            mediaPlayer.seekTo(progress);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    if (mediaPlayer != null) {
                        binding.seekTimeTextView.setText(AppUtils.secondToTime(((int) mediaPlayer.getCurrentPosition()) / 1000));
                        binding.seekBar.setProgress((int) mediaPlayer.getCurrentPosition());
                        binding.seekBarMin.setProgress((int) mediaPlayer.getCurrentPosition());
                        if (handler != null) handler.postDelayed(this, 1000);
                    }
                } catch (Exception ed) {
                    ed.printStackTrace();
                }
            }
        };
        if (handler != null) handler.postDelayed(runnable, 1000);
    }

    public void closeMediaPlayer() {
        try {
            if (mediaPlayer != null) {
                Intent updatePlayBroadCast = new Intent("GetPlaySong");
                AppController.getSpSongInfo().edit().putBoolean("isPlaying", false).putString("CurrentSong", "").apply();
                AppController.getSpSearchSongInfo().edit().putBoolean("isPlayingSearch", true).putString("CurrentSongSearch", "").apply();
                requireActivity().sendBroadcast(updatePlayBroadCast);

                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
                //binding.visualizerView.hide();
                HomeActivity.bindingHome.playScreenFrameLayout.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            closeMediaPlayer();
            unRegisterEvent();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            closeMediaPlayer();
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