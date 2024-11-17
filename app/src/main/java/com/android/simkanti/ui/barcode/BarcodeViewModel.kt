package com.android.simkanti.ui.barcode

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BarcodeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Barcode Fragment"
    }
    val text: LiveData<String> = _text
}