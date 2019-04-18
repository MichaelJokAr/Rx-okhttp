package com.github.jokar.rx_okhttp;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Create by JokAr. on 2019/4/18.
 */
public class ContentEntity implements Parcelable {
    public static final Parcelable.Creator<ContentEntity> CREATOR = new Parcelable.Creator<ContentEntity>() {
        @Override
        public ContentEntity createFromParcel(Parcel source) {
            return new ContentEntity(source);
        }

        @Override
        public ContentEntity[] newArray(int size) {
            return new ContentEntity[size];
        }
    };

    @JSONField(name = "image_source")
    public String imageSource;
    @JSONField(name = "title")
    public String title;
    @JSONField(name = "image")
    public String image;
    @JSONField(name = "share_url")
    public String share_url;

    public ContentEntity() {
    }

    protected ContentEntity(Parcel in) {

        this.imageSource = in.readString();
        this.title = in.readString();
        this.image = in.readString();
        this.share_url = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.imageSource);
        dest.writeString(this.title);
        dest.writeString(this.image);
        dest.writeString(this.share_url);
    }

    @Override
    public String toString() {
        return "ContentEntity{" +
                ", imageSource='" + imageSource + '\'' +
                ", title='" + title + '\'' +
                ", image='" + image + '\'' +
                ", share_url='" + share_url + '\'' +
                '}';
    }
}
