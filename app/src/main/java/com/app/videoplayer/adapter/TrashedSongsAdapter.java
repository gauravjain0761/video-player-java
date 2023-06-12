package com.app.videoplayer.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.app.videoplayer.R;
import com.app.videoplayer.databinding.AdapterSongsTrashedBinding;
import com.app.videoplayer.entity.SongEntity;
import com.app.videoplayer.utils.AppUtils;
import com.app.videoplayer.utils.ImageUtil;

import java.util.Objects;

public class TrashedSongsAdapter extends ListAdapter<SongEntity, TrashedSongsAdapter.ItemViewHolder> {

    String TAG = TrashedSongsAdapter.class.getSimpleName();
    TrashSongsListener trashSongsListener;
    Context context;
    boolean isAnyLongPress = false;

    public TrashedSongsAdapter(Context con, TrashSongsListener listener) {
        super(new DiffUtil.ItemCallback<SongEntity>() {
            @Override
            public boolean areItemsTheSame(@NonNull SongEntity oldItem, @NonNull SongEntity newItem) {
                return Objects.equals(oldItem.getId(), newItem.getId());
            }

            @SuppressLint("DiffUtilEquals")
            @Override
            public boolean areContentsTheSame(@NonNull SongEntity oldItem, @NonNull SongEntity newItem) {
                return oldItem == newItem;
            }
        });
        trashSongsListener = listener;
        context = con;
    }

    public boolean getLongPress() {
        return isAnyLongPress;
    }

    public void clearLongPress() {
        try {
            isAnyLongPress = false;
            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder(AdapterSongsTrashedBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        try {
            holder.bindHolder(getItem(position), position);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        final AdapterSongsTrashedBinding binding;

        public ItemViewHolder(@NonNull AdapterSongsTrashedBinding itemBinding) {
            super(itemBinding.getRoot());
            this.binding = itemBinding;
            try {
                binding.getRoot().setOnClickListener(v -> {
                    try {
                        SongEntity songEntity = getItem(getPosition());
                        if (isAnyLongPress) {
                            trashSongsListener.onCheckedChangeListener(songEntity, !songEntity.getIsChecked(), getPosition());
                            songEntity.setIsChecked(!songEntity.getIsChecked());
                            notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                binding.cbDelete.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    try {
                        SongEntity songEntity = getItem(getPosition());
                        songEntity.setIsChecked(isChecked);
                        trashSongsListener.onCheckedChangeListener(songEntity, isChecked, getPosition());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                binding.getRoot().setOnLongClickListener(v -> {
                    try {
                        isAnyLongPress = true;
                        trashSongsListener.longPressSongs(isAnyLongPress);
                        notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return false;
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void bindHolder(final SongEntity entity, final int position) {
            try {
                if (binding != null) {
                    binding.txtTitle.setText("" + (("" + entity.getTitle()).replace("null", "").replace("Null", "")));
                    binding.txtMsg.setText(AppUtils.formatSize(Long.parseLong("" + (("" + entity.getSize()).replace("null", "").replace("Null", "")))));
                    if (("" + (("" + entity.getBitmapCover()).replace("null", "").replace("Null", ""))).isEmpty()) {
                        binding.imageView.setImageResource(R.drawable.icv_songs);
                    } else {
                        binding.imageView.setImageBitmap(ImageUtil.convertToBitmap(entity.getBitmapCover()));
                    }
                    binding.cbDelete.setChecked(entity.getIsChecked());
                    if (isAnyLongPress) {
                        binding.cbDelete.setVisibility(View.VISIBLE);
                    } else {
                        binding.cbDelete.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public interface TrashSongsListener {
        void onCheckedChangeListener(SongEntity result, boolean isChecked, int position);

        void longPressSongs(boolean isAnyLongPress);
    }
}