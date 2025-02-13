package com.smurzik.mediaplayer.local.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.smurzik.mediaplayer.databinding.ListItemBinding

class LocalTrackListAdapter : RecyclerView.Adapter<LocalTrackListViewHolder>() {

    private val trackList = mutableListOf<LocalTrackUi>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalTrackListViewHolder {
        return LocalTrackListViewHolder(ListItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun getItemCount() = trackList.size

    override fun onBindViewHolder(holder: LocalTrackListViewHolder, position: Int) {
        holder.bind(trackList[position])
    }

    fun update(source: List<LocalTrackUi>) {
        val diffUtil = DiffUtilCallback(trackList, source)
        val diff = DiffUtil.calculateDiff(diffUtil)
        trackList.clear()
        trackList.addAll(source)
        diff.dispatchUpdatesTo(this)
    }
}

class LocalTrackListViewHolder(binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root) {

    private val title = binding.textViewTrackTitle
    private val artist = binding.textViewTrackArtist
    private val albumArt = binding.imageViewTrackCover
    private val mapper = ListItemMapper(title, artist, albumArt)

    fun bind(item: LocalTrackUi) {
        item.map(mapper)
    }
}

class DiffUtilCallback(
    private val oldList: List<LocalTrackUi>,
    private val newList: List<LocalTrackUi>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition] == newList[newItemPosition]

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition] == newList[newItemPosition]
}