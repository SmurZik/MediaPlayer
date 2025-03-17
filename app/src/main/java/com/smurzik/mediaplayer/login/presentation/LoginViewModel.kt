package com.smurzik.mediaplayer.login.presentation

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smurzik.mediaplayer.core.LiveDataWrapper
import com.smurzik.mediaplayer.login.data.UserDataItem
import com.smurzik.mediaplayer.login.domain.LoginRepository
import com.smurzik.mediaplayer.login.domain.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(
    private val repository: LoginRepository /*domain interactor*/
) : ViewModel() {

    private val _stateLiveData: MutableLiveData<UiState> = MutableLiveData()
    val stateLiveData: LiveData<UiState> = _stateLiveData

    private val _registeredLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val registeredLiveData: LiveData<Boolean> = _registeredLiveData

    private val _successLogin: MutableLiveData<Boolean> = MutableLiveData()
    val successLogin: LiveData<Boolean> = _successLogin

    private val _error: MutableLiveData<String> = MutableLiveData()
    val error: LiveData<String> = _error

    private val _userLiveData: MutableLiveData<UserUiItem> = MutableLiveData()
    val userLiveData: LiveData<UserUiItem> = _userLiveData

    private val _progressUserLiveData: MutableLiveData<Int> = MutableLiveData()
    val progressUserLiveData: LiveData<Int> = _progressUserLiveData

    fun register(email: String, username: String, password: String, repeatedPassword: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.register(email, username, password)
            if (result == Result.Success) {
                _successLogin.postValue(true)
                _error.postValue("")
            } else {
                val failure = result as Result.Failure
                _error.postValue(failure.message)
                _successLogin.postValue(false)
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.login(email, password)
            if (result == Result.Success) {
                _successLogin.postValue(true)
                _error.postValue("")
            } else {
                val failure = result as Result.Failure
                _error.postValue(failure.message)
                _successLogin.postValue(false)
            }
        }
    }

    fun init() {
        viewModelScope.launch(Dispatchers.IO) {
            val token = repository.getToken()
            _registeredLiveData.postValue(token != null)
        }
    }

    fun updateRegistered(isLogin: Boolean) {
        if (isLogin) {
            _stateLiveData.value = UiState.Login
        } else {
            _stateLiveData.value = UiState.Register
        }
    }

    fun clearUser() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearUser()
            _registeredLiveData.postValue(false)
            _successLogin.postValue(false)
        }
    }

    fun getUser() {
        _error.value = ""
        viewModelScope.launch(Dispatchers.IO) {
            _progressUserLiveData.postValue(View.VISIBLE)
            when (val userData = repository.getUser()) {
                is Result.SuccessUser -> {
                    _userLiveData.postValue(
                        UserUiItem(
                            userData.login,
                            userData.password,
                            userData.username
                        )
                    )
                }

                is Result.Failure -> {
                    _error.postValue(userData.message)
                }

                else -> {
                    _registeredLiveData.postValue(false)
                }
            }
            _progressUserLiveData.postValue(View.GONE)
        }
    }
}