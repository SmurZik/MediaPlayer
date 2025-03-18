package com.smurzik.mediaplayer.local.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.smurzik.mediaplayer.R
import com.smurzik.mediaplayer.databinding.ListItemBinding

class LocalTrackListAdapter(
    private val clickListener: ClickListener,
    private val favoriteClickListener: ClickListener
) : RecyclerView.Adapter<LocalTrackListViewHolder>() {

    private val trackList = mutableListOf<LocalTrackUi>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalTrackListViewHolder {
        return LocalTrackListViewHolder(
            ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            clickListener,
            favoriteClickListener
        )
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

//    fun updateItem(item: LocalTrackUi) {
//        val itemOld = trackList.first { it.id == item.id }
//        val newList = trackList
//        val index = trackList.indexOf(itemOld)
//        newList.(itemOld)
//        newList.add(index, itemOld)
//        val diffUtil = DiffUtilCallback(trackList, newList)
//        val diff = DiffUtil.calculateDiff(diffUtil)
//        trackList.clear()
//        trackList.addAll(newList)
//        diff.dispatchUpdatesTo(this)
//    }
}

class LocalTrackListViewHolder(
    private val binding: ListItemBinding,
    private val clickListener: ClickListener,
    private val favoriteClickListener: ClickListener
) :
    RecyclerView.ViewHolder(binding.root) {

    private val title = binding.textViewTrackTitle
    private val artist = binding.textViewTrackArtist
    private val albumArt = binding.imageViewTrackCover
    private val mapper = ListItemMapper(title, artist, albumArt)

    fun bind(item: LocalTrackUi) {
        item.map(mapper)
        binding.addFavorite.setImageResource(if (item.isFavorite) R.drawable.ic_favorite_fill else R.drawable.ic_favorite)
        binding.root.setOnClickListener {
            binding.root.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .withEndAction {
                    binding.root.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .start()
                }
                .start()
            clickListener.click(item)
        }

        binding.addFavorite.setOnClickListener {
            favoriteClickListener.click(item)
        }
    }
}

interface ClickListener {
    fun click(item: LocalTrackUi)
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