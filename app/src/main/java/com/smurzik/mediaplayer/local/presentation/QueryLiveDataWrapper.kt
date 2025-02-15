package com.smurzik.mediaplayer.local.presentation

import com.smurzik.mediaplayer.core.LiveDataWrapper

class QueryLiveDataWrapper {

    interface Read : LiveDataWrapper.Read<String>

    interface Mutable : LiveDataWrapper.Mutable<String>

    class Base() : LiveDataWrapper.Abstract<String>(), Mutable
}