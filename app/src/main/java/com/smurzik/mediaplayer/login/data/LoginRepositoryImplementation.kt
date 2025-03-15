package com.smurzik.mediaplayer.login.data

import com.smurzik.mediaplayer.login.data.cache.UserCache
import com.smurzik.mediaplayer.login.data.cache.UserDao
import com.smurzik.mediaplayer.login.data.cloud.LoginBody
import com.smurzik.mediaplayer.login.data.cloud.LoginService
import com.smurzik.mediaplayer.login.data.cloud.RegistrationBody
import com.smurzik.mediaplayer.login.domain.LoginRepository

class LoginRepositoryImplementation(
    private val loginService: LoginService,
    private val dao: UserDao
) : LoginRepository {
    override suspend fun login(
        email: String,
        password: String
    ) {
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
    }

    override suspend fun register(
        email: String,
        username: String,
        password: String
    ) {
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
    }

    override suspend fun getToken(): String? {
        return dao.getToken()
    }

    override suspend fun clearUser() {
        dao.clearUser()
    }
}