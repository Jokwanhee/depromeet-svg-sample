package com.depromeet.svg

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.jkh.svgsample.Repository
import okhttp3.ResponseBody

class MainViewModel: ViewModel() {
    fun downloadFileFromServer(url: String): LiveData<ResponseBody> {
        return Repository.downloadFileFromServer(url)
    }
}