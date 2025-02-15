package com.smurzik.mediaplayer.core

import com.smurzik.mediaplayer.player.presentation.PlayerInfoUi

interface SharedTrackLiveDataWrapper {
    interface Read : LiveDataWrapper.Read<PlayerInfoUi>

    interface Mutable : LiveDataWrapper.Mutable<PlayerInfoUi>

    class Base() : LiveDataWrapper.Abstract<PlayerInfoUi>(), Mutable
}