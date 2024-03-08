package com.iteration.climbingmuse.ui.file

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FileExplorerViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This functionality is still under development. Come back later!"
    }
    val text: LiveData<String> = _text
}