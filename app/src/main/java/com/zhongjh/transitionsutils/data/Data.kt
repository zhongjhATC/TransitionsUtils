package com.zhongjh.transitionsutils.data

import android.os.Parcel
import android.os.Parcelable

/**
 * 数据源
 *
 * @author zhongjh
 * @date 2022/2/22
 */
class Data : Parcelable {
    constructor(position: Int, url: String?, originalUrl: String?) {
        this.position = position
        this.url = url
        this.originalUrl = originalUrl
    }

    var position: Int
    var url: String?
    var originalUrl: String?

    protected constructor(`in`: Parcel) {
        position = `in`.readInt()
        url = `in`.readString()
        originalUrl = `in`.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, i: Int) {
        parcel.writeInt(position)
        parcel.writeString(url)
        parcel.writeString(originalUrl)
    }

    companion object CREATOR : Parcelable.Creator<Data> {
        override fun createFromParcel(`in`: Parcel): Data? {
            return Data(`in`)
        }

        override fun newArray(size: Int): Array<Data?> {
            return arrayOfNulls(size)
        }
    }




}