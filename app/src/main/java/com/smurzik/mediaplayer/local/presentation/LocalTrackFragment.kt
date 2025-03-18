package com.smurzik.mediaplayer.local.presentation

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.media3.session.MediaController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.smurzik.mediaplayer.R
import com.smurzik.mediaplayer.core.MediaPlayerApp
import com.smurzik.mediaplayer.databinding.LocalTrackFragmentBinding
import com.smurzik.mediaplayer.player.presentation.PlayerInfoUi

class LocalTrackFragment : Fragment() {

    private lateinit var binding: LocalTrackFragmentBinding
    private lateinit var watcher: TextWatcher

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.local_track_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = LocalTrackFragmentBinding.inflate(layoutInflater)
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerViewDownloadedTracks)
        val searchView = view.findViewById<TextInputEditText>(R.id.searchView)
        val progress = view.findViewById<ProgressBar>(R.id.progressBar)
        val accountButton = view.findViewById<ImageButton>(R.id.accountButton)
        val viewModel = (requireActivity().application as MediaPlayerApp).viewModel
        val loginViewModel = (requireActivity().application as MediaPlayerApp).loginViewModel
        val favoriteViewModel = (requireActivity().application as MediaPlayerApp).favoriteViewModel

        val adapter = LocalTrackListAdapter(object : ClickListener {
            override fun click(item: LocalTrackUi) {
                if (!viewModel.isPlaying() || viewModel.currentTrackIndex() != item.index)
                    requireActivity().findNavController(R.id.containerView)
                        .navigate(R.id.action_mainFragment_to_playerFragment)
                viewModel.changeTrack(item.index)
            }
        }, object : ClickListener {
            override fun click(item: LocalTrackUi) {
                if (loginViewModel.registeredLiveData.value == true) {
                    favoriteViewModel.changeFavorite(item, false)
                } else {
                    requireActivity().findNavController(R.id.containerView)
                        .navigate(R.id.action_mainFragment_to_loginFragment)
                }
            }
        })

        recycler.adapter = adapter

        watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isNotEmpty())
                    viewModel.searchTrack(s.toString())
                else
                    viewModel.init()
            }
        }

        accountButton.setOnClickListener {
            requireActivity().findNavController(R.id.containerView)
                .navigate(R.id.action_mainFragment_to_profileFragment)
        }

        searchView.addTextChangedListener(watcher)

        viewModel.progressLiveData().observe(viewLifecycleOwner) {
            progress.visibility = it
        }

        if (savedInstanceState == null)
            viewModel.init()

        viewModel.liveData().observe(viewLifecycleOwner) {
            adapter.update(it)
        }
    }
}