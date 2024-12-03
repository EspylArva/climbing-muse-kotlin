package com.iteration.climbingmuse.ui.file

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.iteration.climbingmuse.R
import timber.log.Timber

class FileExplorerViewModel(application: Application) : AndroidViewModel(application) {

    private val _text = MutableLiveData<String>().apply {
        value = "This functionality is still under development. Come back later!"
    }
    val text: LiveData<String> = _text

    val useOnline = MutableLiveData<Boolean>()


    init {
        val res = application.resources
        val sp = application.getSharedPreferences(res.getString(R.string.app_name), Context.MODE_PRIVATE)

        val online = sp.getBoolean(res.getString(R.string.sp_use_online), false)
        useOnline.apply { value = online } // Posting doesn't seem to be fast enough

        Timber.d("FileExplorerViewModel inited. useOnline=${useOnline.value} (online=$online)")


    }

}