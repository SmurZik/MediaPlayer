package com.smurzik.mediaplayer.player.presentation

import com.smurzik.mediaplayer.core.LiveDataWrapper

interface SeekBarLiveDataWrapper {
    interface Read : LiveDataWrapper.Read<Long>

    interface Mutable : LiveDataWrapper.Mutable<Long>

    class Base() : LiveDataWrapper.Abstract<Long>(), Mutable
}