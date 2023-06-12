package com.app.videoplayer.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class SongEntity implements Parcelable {
    @Id(autoincrement = true)
    private Long id;
    @Nullable
    private int songId;
    private int trackNumber;
    @Nullable
    private int year;
    @Nullable
    private int albumId;
    @Nullable
    private int artistId;
    @Nullable
    private long duration;
    @Nullable
    private long dateModified;
    @Nullable
    private long dateAdded;
    @Nullable
    private long bookmark;
    @Nullable
    private int genreId;
    @Nullable
    private String genreName = "";
    @Nullable
    private String title = "";
    @Nullable
    private String artistName = "";
    @Nullable
    private String composer = "";
    @Nullable
    private String albumName = "";
    @Nullable
    private String albumArt = "";
    @Nullable
    private String data = "";
    @Nullable
    private boolean isChecked = false;
    @Nullable
    private boolean isTrashed = false;
    @Nullable
    private long size;
    @Nullable
    private String bitmapCover;

    protected SongEntity(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        songId = in.readInt();
        trackNumber = in.readInt();
        year = in.readInt();
        albumId = in.readInt();
        artistId = in.readInt();
        duration = in.readLong();
        dateModified = in.readLong();
        dateAdded = in.readLong();
        bookmark = in.readLong();
        genreId = in.readInt();
        genreName = in.readString();
        title = in.readString();
        artistName = in.readString();
        composer = in.readString();
        albumName = in.readString();
        albumArt = in.readString();
        data = in.readString();
        isChecked = in.readByte() != 0;
        isTrashed = in.readByte() != 0;
        size = in.readLong();
        bitmapCover = in.readString();
    }

    @Generated(hash = 1096222486)
    public SongEntity(Long id, int songId, int trackNumber, int year, int albumId,
            int artistId, long duration, long dateModified, long dateAdded,
            long bookmark, int genreId, String genreName, String title,
            String artistName, String composer, String albumName, String albumArt,
            String data, boolean isChecked, boolean isTrashed, long size,
            String bitmapCover) {
        this.id = id;
        this.songId = songId;
        this.trackNumber = trackNumber;
        this.year = year;
        this.albumId = albumId;
        this.artistId = artistId;
        this.duration = duration;
        this.dateModified = dateModified;
        this.dateAdded = dateAdded;
        this.bookmark = bookmark;
        this.genreId = genreId;
        this.genreName = genreName;
        this.title = title;
        this.artistName = artistName;
        this.composer = composer;
        this.albumName = albumName;
        this.albumArt = albumArt;
        this.data = data;
        this.isChecked = isChecked;
        this.isTrashed = isTrashed;
        this.size = size;
        this.bitmapCover = bitmapCover;
    }

    @Generated(hash = 274420887)
    public SongEntity() {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeInt(songId);
        dest.writeInt(trackNumber);
        dest.writeInt(year);
        dest.writeInt(albumId);
        dest.writeInt(artistId);
        dest.writeLong(duration);
        dest.writeLong(dateModified);
        dest.writeLong(dateAdded);
        dest.writeLong(bookmark);
        dest.writeInt(genreId);
        dest.writeString(genreName);
        dest.writeString(title);
        dest.writeString(artistName);
        dest.writeString(composer);
        dest.writeString(albumName);
        dest.writeString(albumArt);
        dest.writeString(data);
        dest.writeByte((byte) (isChecked ? 1 : 0));
        dest.writeByte((byte) (isTrashed ? 1 : 0));
        dest.writeLong(size);
        dest.writeString(bitmapCover);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getSongId() {
        return this.songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

    public int getTrackNumber() {
        return this.trackNumber;
    }

    public void setTrackNumber(int trackNumber) {
        this.trackNumber = trackNumber;
    }

    public int getYear() {
        return this.year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getAlbumId() {
        return this.albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public int getArtistId() {
        return this.artistId;
    }

    public void setArtistId(int artistId) {
        this.artistId = artistId;
    }

    public long getDuration() {
        return this.duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getDateModified() {
        return this.dateModified;
    }

    public void setDateModified(long dateModified) {
        this.dateModified = dateModified;
    }

    public long getDateAdded() {
        return this.dateAdded;
    }

    public void setDateAdded(long dateAdded) {
        this.dateAdded = dateAdded;
    }

    public long getBookmark() {
        return this.bookmark;
    }

    public void setBookmark(long bookmark) {
        this.bookmark = bookmark;
    }

    public int getGenreId() {
        return this.genreId;
    }

    public void setGenreId(int genreId) {
        this.genreId = genreId;
    }

    public String getGenreName() {
        return this.genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtistName() {
        return this.artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getComposer() {
        return this.composer;
    }

    public void setComposer(String composer) {
        this.composer = composer;
    }

    public String getAlbumName() {
        return this.albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getAlbumArt() {
        return this.albumArt;
    }

    public void setAlbumArt(String albumArt) {
        this.albumArt = albumArt;
    }

    public String getData() {
        return this.data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean getIsChecked() {
        return this.isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public boolean getIsTrashed() {
        return this.isTrashed;
    }

    public void setIsTrashed(boolean isTrashed) {
        this.isTrashed = isTrashed;
    }

    public long getSize() {
        return this.size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getBitmapCover() {
        return this.bitmapCover;
    }

    public void setBitmapCover(String bitmapCover) {
        this.bitmapCover = bitmapCover;
    }

    public static final Creator<SongEntity> CREATOR = new Creator<SongEntity>() {
        @Override
        public SongEntity createFromParcel(Parcel in) {
            return new SongEntity(in);
        }

        @Override
        public SongEntity[] newArray(int size) {
            return new SongEntity[size];
        }
    };
}
