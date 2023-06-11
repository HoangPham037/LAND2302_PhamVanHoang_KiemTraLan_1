package com.example.applistenmusic

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import phucdv.android.musichelper.Song

class MusicAdapter(
    private val musicList: MutableList<Song>,
    private val context: Context
) : RecyclerView.Adapter<MusicAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val titleNameSong: TextView = itemView.findViewById(R.id.tvTitleSong)
        val titleNameSing: TextView = itemView.findViewById(R.id.tvNameSing)
        val titleAlbum: TextView = itemView.findViewById(R.id.tvAlbumTitle)
        val imgPlay: ImageView = itemView.findViewById(R.id.imgStart)
        val imgPause: ImageView = itemView.findViewById(R.id.imgPause)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                listener?.onItemClick(adapterPosition)
            }
        }
    }

    interface ItemClickListener {
        fun onItemClick(position: Int)
    }

    private var listener: ItemClickListener? = null
    private var selectedPosition = -1

    fun setItemClickListener(listener: ItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_song, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return musicList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val music = musicList[position]
        holder.titleNameSong.text = music.title
        holder.titleNameSing.text = music.artist
        holder.titleAlbum.text = music.albumTitle

        if (position == selectedPosition) {
            holder.imgPlay.visibility = View.GONE
            holder.imgPause.visibility = View.VISIBLE
        } else {
            holder.imgPlay.visibility = View.VISIBLE
            holder.imgPause.visibility = View.GONE
        }
    }

    fun setSelectedPosition(position: Int) {
        selectedPosition = position
        notifyDataSetChanged()
    }
}