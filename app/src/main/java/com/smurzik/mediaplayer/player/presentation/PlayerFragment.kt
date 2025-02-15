package com.smurzik.mediaplayer.player.presentation

import android.content.ComponentName
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Player.Listener
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.bumptech.glide.Glide
import com.google.common.util.concurrent.MoreExecutors
import com.smurzik.mediaplayer.R
import com.smurzik.mediaplayer.core.MediaPlayerApp
import com.smurzik.mediaplayer.core.PlaybackService

class PlayerFragment : Fragment() {

    private var mediaController: MediaController? = null
    private lateinit var playerListener: Listener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.player_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val albumArt = view.findViewById<ImageView>(R.id.album_art)
        val seekBar = view.findViewById<SeekBar>(R.id.seek_bar)
        val previousButton = view.findViewById<ImageButton>(R.id.btn_prev)
        val nextButton = view.findViewById<ImageButton>(R.id.btn_next)
        val playPauseButton = view.findViewById<ImageButton>(R.id.btn_play_pause)
        val title = view.findViewById<TextView>(R.id.song_title)
        val artist = view.findViewById<TextView>(R.id.song_artist)

        val viewModel = (requireActivity().application as MediaPlayerApp).playerViewModel

        playerListener = object : Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                val iconRes = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
                view.findViewById<ImageButton>(R.id.btn_play_pause).setImageResource(iconRes)
                viewModel.updateSeekBar()
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                if (playbackState == Player.STATE_READY) {
                    mediaController?.mediaMetadata?.let {
                        val trackInfo = PlayerInfoUi(
                            it.artworkUri.toString(),
                            it.title.toString(),
                            it.artist.toString(),
                            mediaController?.duration ?: 0
                        )
                        viewModel.updateCurrentTrack(trackInfo)
                        viewModel.updateSeekBar()
                    }
                }
            }
        }

        playPauseButton.setImageResource(R.drawable.ic_pause)

        viewModel.liveData().observe(viewLifecycleOwner) {
            Glide.with(this).load(it.albumUri).placeholder(R.drawable.ic_music_note).into(albumArt)
            title.text = it.title
            artist.text = it.artist
            seekBar.max = it.duration.toInt()
            viewModel.updateSeekBar()
        }

        nextButton.setOnClickListener {

        }

        viewModel.seekBarLiveDataWrapper().observe(viewLifecycleOwner) {
            seekBar.progress = it.toInt()
        }

        val sessionToken = SessionToken(
            requireActivity(),
            ComponentName(requireActivity(), PlaybackService::class.java)
        )
        val controllerFuture = MediaController.Builder(requireActivity(), sessionToken).buildAsync()
        controllerFuture.addListener(
            {
                mediaController = controllerFuture.get()
                mediaController?.addListener(playerListener)
            },
            MoreExecutors.directExecutor()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaController?.removeListener(playerListener)
    }
}