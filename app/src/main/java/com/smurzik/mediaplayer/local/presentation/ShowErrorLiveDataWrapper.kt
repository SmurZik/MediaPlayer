package com.smurzik.mediaplayer.local.presentation

import com.smurzik.mediaplayer.core.LiveDataWrapper

interface ShowErrorLiveDataWrapper {

    interface Read : LiveDataWrapper.Read<Int>

    interface Mutable : LiveDataWrapper.Mutable<Int>

    class Base() : LiveDataWrapper.Abstract<Int>(), Mutable
}