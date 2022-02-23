package com.zhongjh.transitionsutils.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 数据源
 *
 * @author zhongjh
 * @date 2022/2/22
 */
public class Data implements Parcelable {

    public Data(int position, String url, String originalUrl) {
        this.position = position;
        this.url = url;
        this.originalUrl = originalUrl;
    }

    private int position;
    private String url;
    private String originalUrl;

    protected Data(Parcel in) {
        position = in.readInt();
        url = in.readString();
        originalUrl = in.readString();
    }

    public static final Creator<Data> CREATOR = new Creator<Data>() {
        @Override
        public Data createFromParcel(Parcel in) {
            return new Data(in);
        }

        @Override
        public Data[] newArray(int size) {
            return new Data[size];
        }
    };

    public static String transitionName(int position) {
        return "item" + position;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(position);
        parcel.writeString(url);
        parcel.writeString(originalUrl);
    }
}
