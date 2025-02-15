package com.smurzik.mediaplayer.local.presentation

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.media3.session.MediaController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.smurzik.mediaplayer.R
import com.smurzik.mediaplayer.core.MediaPlayerApp
import com.smurzik.mediaplayer.databinding.ActivityMainBinding
import com.smurzik.mediaplayer.databinding.LocalTrackFragmentBinding
import com.smurzik.mediaplayer.player.presentation.PlayerInfoUi

class LocalTrackFragment : Fragment() {

    private lateinit var binding: LocalTrackFragmentBinding
    private var mediaController: MediaController? = null
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

        val viewModel = (requireActivity().application as MediaPlayerApp).viewModel

        val adapter = LocalTrackListAdapter(object : ClickListener {
            override fun click(item: LocalTrackUi) {
                val query = searchView.text.toString()
                viewModel.changeTrack(
                    item.index,
                    query.isNotEmpty(),
                    PlayerInfoUi(item.albumUri, item.title, item.author, item.duration)
                )
                Log.d("smurzLog", "newDuration: ${item.duration}")
                findNavController().navigate(R.id.action_localTrackFragment_to_playerFragment)
            }
        })

        recycler.adapter = adapter

        watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

            override fun afterTextChanged(s: Editable?) {
                viewModel.init(s.toString(), true)
            }
        }

        searchView.addTextChangedListener(watcher)

        if (viewModel.liveData().value?.isEmpty() != false)
            viewModel.init("", false)

        viewModel.liveData().observe(viewLifecycleOwner) {
            adapter.update(it)
        }
    }
}