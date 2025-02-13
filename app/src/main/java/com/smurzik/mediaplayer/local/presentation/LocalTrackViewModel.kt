package com.smurzik.mediaplayer.local.presentation

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smurzik.mediaplayer.local.domain.LocalTrackInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LocalTrackViewModel(
    private val listLiveDataWrapper: ListLiveDataWrapper.Mutable,
    private val progressLiveDataWrapper: ProgressLiveDataWrapper.Mutable,
    private val interactor: LocalTrackInteractor,
    private val mapper: LocalTrackResultMapper
) : ListLiveDataWrapper.Read, ViewModel() {

    fun init() {
        progressLiveDataWrapper.update(View.VISIBLE)
        viewModelScope.launch(Dispatchers.IO) {
            val result = interactor.init()
            progressLiveDataWrapper.update(View.GONE)
            result.map(mapper)
        }
    }

    override fun liveData(): LiveData<List<LocalTrackUi>> = listLiveDataWrapper.liveData()
}