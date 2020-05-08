package edu.sdsu.cs.android.onestopshop.ui.groceries

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GroceriesViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is groceries Fragment"
    }
    val text: LiveData<String> = _text
}