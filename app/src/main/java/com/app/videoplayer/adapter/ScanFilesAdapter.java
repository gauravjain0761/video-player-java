package com.app.videoplayer.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.app.videoplayer.R;
import com.app.videoplayer.databinding.AdapterSongsScanBinding;
import com.app.videoplayer.entity.SongEntity;
import com.app.videoplayer.utils.AppUtils;
import com.app.videoplayer.utils.ImageUtil;

import java.util.Objects;

public class ScanFilesAdapter extends ListAdapter<SongEntity, ScanFilesAdapter.ItemViewHolder> {

    String TAG = ScanFilesAdapter.class.getSimpleName();
    ScanFilesListener scanFilesListener;
    Context context;

    public ScanFilesAdapter(Context con, ScanFilesListener listener) {
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
        scanFilesListener = listener;
        context = con;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder(AdapterSongsScanBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
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
        final AdapterSongsScanBinding binding;

        public ItemViewHolder(@NonNull AdapterSongsScanBinding itemBinding) {
            super(itemBinding.getRoot());
            this.binding = itemBinding;
            try {
                binding.getRoot().setOnClickListener(v -> {
                    try {
                        SongEntity songEntity = getItem(getPosition());
                        scanFilesListener.setOnCheckedChangeListener(songEntity, !songEntity.getIsChecked(), getPosition());
                        songEntity.setIsChecked(!songEntity.getIsChecked());
                        notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                binding.cbDelete.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    try {
                        SongEntity entity = getItem(getPosition());
                        entity.setIsChecked(isChecked);
                        scanFilesListener.setOnCheckedChangeListener(entity, isChecked, getPosition());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public interface ScanFilesListener {
        void setOnCheckedChangeListener(SongEntity entity, boolean isChecked, int position);
    }
}