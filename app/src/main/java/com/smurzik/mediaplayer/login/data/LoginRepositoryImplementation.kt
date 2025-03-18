package com.smurzik.mediaplayer.login.data

import android.util.Log
import com.smurzik.mediaplayer.login.data.cache.UserCache
import com.smurzik.mediaplayer.login.data.cache.UserDao
import com.smurzik.mediaplayer.login.data.cloud.LoginBody
import com.smurzik.mediaplayer.login.data.cloud.LoginService
import com.smurzik.mediaplayer.login.data.cloud.RegistrationBody
import com.smurzik.mediaplayer.login.data.cloud.UserReceive
import com.smurzik.mediaplayer.login.data.cloud.UserResponse
import com.smurzik.mediaplayer.login.domain.LoginRepository
import com.smurzik.mediaplayer.login.domain.Result
import retrofit2.HttpException
import java.net.ConnectException
import java.net.UnknownHostException

class LoginRepositoryImplementation(
    private val loginService: LoginService,
    private val dao: UserDao
) : LoginRepository {
    override suspend fun login(
        email: String,
        password: String
    ): Result {
        try {
            val loginBody = LoginBody(
                email, password
            )
            val token = loginService.login(loginBody).token
            val userCache = UserCache(
                email,
                "",
                token,
                password
            )
            dao.insertUser(userCache)
            return Result.Success
        } catch (e: ConnectException) {
            return Result.Failure("Нет соединения с сервером")
        } catch (e: HttpException) {
            return Result.Failure(e.response()?.errorBody()?.string() ?: "")
        } catch (e: Exception) {
            return Result.Failure("Что-то пошло не так")
        }
    }

    override suspend fun register(
        email: String,
        username: String,
        password: String
    ): Result {
        try {
            val registrationBody = RegistrationBody(
                email, password, username
            )
            val token = loginService.register(registrationBody).token
            val userCache = UserCache(
                email,
                username,
                token,
                password
            )
            dao.insertUser(userCache)
            return Result.Success
        } catch (e: ConnectException) {
            return Result.Failure("Нет соединения с сервером")
        } catch (e: HttpException) {
            return Result.Failure(e.response()?.errorBody()?.string() ?: "")
        } catch (e: Exception) {
            return Result.Failure("Что-то пошло не так")
        }
    }

    override suspend fun getToken(): String? {
        return dao.getToken()
    }

    override suspend fun clearUser() {
        dao.clearUser()
    }

    override suspend fun getUser(): Result {
        try {
            val userCloud = loginService.fetchUser(dao.getToken() ?: "")
            return Result.SuccessUser(
                userCloud.login,
                userCloud.username,
                userCloud.password
            )
        } catch (e: ConnectException) {
            if (dao.getToken() == null) return Result.NotRegistered
            return Result.Failure("Нет соединения с сервером")
        } catch (e: HttpException) {
            if (dao.getToken() == null) return Result.NotRegistered
            return Result.Failure(e.response()?.errorBody()?.string() ?: "")
        } catch (e: Exception) {
            return Result.Failure("Что-то пошло не так")
        }
    }

    override suspend fun updateUser(email: String, username: String) {
        val userReceive = UserReceive(
            login = email,
            password = "",
            username = username
        )
        loginService.updateUser(dao.getToken() ?: "", userReceive)
    }
}