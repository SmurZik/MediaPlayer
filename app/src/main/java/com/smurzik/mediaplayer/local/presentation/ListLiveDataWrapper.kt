package com.smurzik.mediaplayer.local.presentation

import com.smurzik.mediaplayer.core.LiveDataWrapper

interface ListLiveDataWrapper {

    interface Read : LiveDataWrapper.Read<List<LocalTrackUi>>

    interface Mutable : LiveDataWrapper.Mutable<List<LocalTrackUi>>

    class Base() : LiveDataWrapper.Abstract<List<LocalTrackUi>>(), Mutable
}