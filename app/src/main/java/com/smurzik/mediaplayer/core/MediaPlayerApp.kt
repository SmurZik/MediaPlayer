package com.smurzik.mediaplayer.core

import android.app.Application
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.room.Room
import androidx.room.RoomDatabase
import com.smurzik.mediaplayer.cloud.data.CloudTrackRepository
import com.smurzik.mediaplayer.cloud.data.TrackService
import com.smurzik.mediaplayer.cloud.presentation.CloudViewModel
import com.smurzik.mediaplayer.local.data.LocalTrackDataSource
import com.smurzik.mediaplayer.local.data.LocalTrackDataToDomain
import com.smurzik.mediaplayer.local.data.LocalTrackDataToQuery
import com.smurzik.mediaplayer.local.data.LocalTrackRepository
import com.smurzik.mediaplayer.local.domain.LocalTrackInteractor
import com.smurzik.mediaplayer.local.presentation.CurrentTrackLiveDataWrapper
import com.smurzik.mediaplayer.local.presentation.ListLiveDataWrapper
import com.smurzik.mediaplayer.local.presentation.LocalTrackQueryMapper
import com.smurzik.mediaplayer.local.presentation.LocalTrackResultMapper
import com.smurzik.mediaplayer.local.presentation.LocalTrackUiMapper
import com.smurzik.mediaplayer.local.presentation.LocalTrackViewModel
import com.smurzik.mediaplayer.local.presentation.MediaItemUiMapper
import com.smurzik.mediaplayer.local.presentation.ProgressLiveDataWrapper
import com.smurzik.mediaplayer.login.data.LoginRepositoryImplementation
import com.smurzik.mediaplayer.login.data.cache.UserRoomDatabase
import com.smurzik.mediaplayer.login.data.cloud.LoginService
import com.smurzik.mediaplayer.login.presentation.LoginViewModel
import com.smurzik.mediaplayer.player.presentation.PlayerViewModel
import com.smurzik.mediaplayer.player.presentation.SeekBarLiveDataWrapper
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MediaPlayerApp : Application() {

    lateinit var viewModel: LocalTrackViewModel
    lateinit var cloudViewModel: CloudViewModel
    lateinit var playerViewModel: PlayerViewModel
    lateinit var mediaSession: MediaSession
    lateinit var loginViewModel: LoginViewModel
    private lateinit var service: TrackService

    override fun onCreate() {
        super.onCreate()
        val listLiveDataWrapper = ListLiveDataWrapper.Base()
        val sharedTrackLiveDataWrapper = SharedTrackLiveDataWrapper.Base()
        val exoPlayer = ExoPlayer.Builder(this).build()
        mediaSession = MediaSession.Builder(this, exoPlayer).build()
        val serviceHelper = PlaybackServiceHelper(exoPlayer)
        val seekBarLiveDataWrapper = SeekBarLiveDataWrapper.Base()
        val currentTrackLiveDataWrapper = CurrentTrackLiveDataWrapper.Base()
        val roomDatabase = Room.databaseBuilder(
            context = this,
            UserRoomDatabase::class.java,
            "user_database"
        ).fallbackToDestructiveMigration().build()
        val dao = roomDatabase.userDao()

        val logging = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC)
        val client = OkHttpClient.Builder().addInterceptor(logging).build()

        service = Retrofit.Builder().baseUrl("https://api.deezer.com/")
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(TrackService::class.java)

        val loginService = Retrofit.Builder().baseUrl("http://10.0.2.2:8081/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(LoginService::class.java)

        val repository = LoginRepositoryImplementation(loginService, dao)

        val mapper = LocalTrackUiMapper()

        loginViewModel = LoginViewModel(
            repository = repository
        )

        cloudViewModel = CloudViewModel(
            progressLiveDataWrapper = ProgressLiveDataWrapper.Base(),
            interactor = LocalTrackInteractor.Base(
                CloudTrackRepository(
                    service
                )
            ),
            mapper = LocalTrackResultMapper(
                listLiveDataWrapper,
                mapper
            ),
            listLiveDataWrapper = listLiveDataWrapper,
            musicHelper = serviceHelper,
            queryMapper = LocalTrackQueryMapper(
                listLiveDataWrapper,
                mapper
            ),
            trackProgress = seekBarLiveDataWrapper,
            currentTrack = currentTrackLiveDataWrapper,
            mediaItemMapper = MediaItemUiMapper()
        )

        viewModel = LocalTrackViewModel(
            progressLiveDataWrapper = ProgressLiveDataWrapper.Base(),
            interactor = LocalTrackInteractor.Base(
                LocalTrackRepository(
                    LocalTrackDataSource.Base(this),
                    LocalTrackDataToDomain(),
                    LocalTrackDataToQuery()
                )
            ),
            mapper = LocalTrackResultMapper(
                listLiveDataWrapper,
                mapper
            ),
            listLiveDataWrapper = listLiveDataWrapper,
            musicHelper = serviceHelper,
            queryMapper = LocalTrackQueryMapper(
                listLiveDataWrapper,
                mapper
            ),
            trackProgress = seekBarLiveDataWrapper,
            currentTrack = currentTrackLiveDataWrapper,
            mediaItemMapper = MediaItemUiMapper()
        )
        playerViewModel = PlayerViewModel(
            sharedTrackLiveDataWrapper = sharedTrackLiveDataWrapper,
            seekBarLiveDataWrapper = seekBarLiveDataWrapper,
            musicHelper = serviceHelper,
            sharedAllTracksLiveDataWrapper = listLiveDataWrapper,
            currentTrack = currentTrackLiveDataWrapper
        )
    }
}