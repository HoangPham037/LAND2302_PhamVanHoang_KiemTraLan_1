package com.example.applistenmusic

import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaPlayer.OnPreparedListener
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.applistenmusic.databinding.ActivityMainBinding
import phucdv.android.musichelper.MediaHelper


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var mediaPlayer: MediaPlayer? = null
    private var currentSongId: Long = -1
    private var lastPlayedPosition: Int = -1
    private lateinit var musicAdapter: MusicAdapter
    private var songList = mutableListOf<phucdv.android.musichelper.Song>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkPermission()
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                999
            )
        } else {
            doRetrieveAllSong()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 999 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            doRetrieveAllSong()
        } else {
            doRetrieveAllSong()
        }
    }

    private fun doRetrieveAllSong() {
        MediaHelper.retrieveAllSong(
            this
        ) { p0 ->
            if (p0 != null) {
                songList.addAll(p0)
            }

            val layoutManager = LinearLayoutManager(this)
            binding.recMusic.layoutManager = layoutManager
            musicAdapter = MusicAdapter(songList, this@MainActivity)

            binding.recMusic.adapter = musicAdapter
            musicAdapter.setItemClickListener(object : MusicAdapter.ItemClickListener {
                override fun onItemClick(position: Int) {
                    musicAdapter.setSelectedPosition(position)
                    val music = songList[position]
                    if (mediaPlayer == null) {
                        mediaPlayer = MediaPlayer()
                        playSong(this@MainActivity, music.id)
                        mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
                        mediaPlayer?.setOnPreparedListener(OnPreparedListener { mediaPlayer ->
                            mediaPlayer?.start()
                        })
                    } else if (mediaPlayer!!.isPlaying && currentSongId == music.id) {
                        lastPlayedPosition = mediaPlayer!!.currentPosition
                        mediaPlayer!!.pause()
                    } else {
                        if (lastPlayedPosition != -1) {
                            mediaPlayer!!.seekTo(lastPlayedPosition)
                            mediaPlayer!!.start()
                            lastPlayedPosition = -1
                        } else {
                            playSong(this@MainActivity, music.id)
                            mediaPlayer!!.setOnPreparedListener(OnPreparedListener { mediaPlayer ->
                                mediaPlayer!!.start()
                            })
                        }

                        musicAdapter.notifyDataSetChanged()
                    }
                    currentSongId = music.id
                }
            })
        }
    }

    fun playSong(context: Context?, songId: Long) {
        mediaPlayer?.reset()
        val trackUri =
            ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songId)
        try {
            if (context != null) {
                mediaPlayer?.setDataSource(context, trackUri)
            }
        } catch (e: Exception) {
            Log.e("MUSIC SERVICE", "Error starting data source", e)
        }
        mediaPlayer?.prepareAsync()
    }
}