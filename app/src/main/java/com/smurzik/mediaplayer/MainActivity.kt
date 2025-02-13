package com.smurzik.mediaplayer

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.smurzik.mediaplayer.core.MediaPlayerApp
import com.smurzik.mediaplayer.core.MediaPlayerService
import com.smurzik.mediaplayer.databinding.ActivityMainBinding
import com.smurzik.mediaplayer.local.presentation.ClickListener
import com.smurzik.mediaplayer.local.presentation.LocalTrackListAdapter
import com.smurzik.mediaplayer.local.presentation.LocalTrackUi
import com.smurzik.mediaplayer.local.presentation.TrackMapper

class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        val mapper = TrackMapper()

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setContentView(binding.root)

        val adapter = LocalTrackListAdapter(object : ClickListener {
            override fun click(item: LocalTrackUi) {
                Intent(applicationContext, MediaPlayerService::class.java).also {
                    it.action = MediaPlayerService.Actions.START.toString()
                    it.putExtra(MediaPlayerService.EXTRA_TRACK_URI, item.map(mapper))
                    startService(it)
                }
            }
        })
        binding.recyclerViewDownloadedTracks.adapter = adapter

        val viewModel = (application as MediaPlayerApp).viewModel
        checkAndRequestPermissions()

        viewModel.init()

        viewModel.liveData().observe(this) {
            adapter.update(it)
        }
    }

    private fun checkAndRequestPermissions() {
        val neededPermissions = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_MEDIA_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                neededPermissions.add(android.Manifest.permission.READ_MEDIA_AUDIO)
            }
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                neededPermissions.add(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                neededPermissions.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
        if (neededPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, neededPermissions.toTypedArray(), REQUEST_CODE)
        } else {

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            val deniedPermissions = permissions.zip(grantResults.toList())
                .filter { (_, result) -> result != PackageManager.PERMISSION_GRANTED }
                .map { (permission, _) -> permission }
            if (deniedPermissions.isEmpty()) {

            } else {
                Toast.makeText(this, "Некоторые разрешения не получены", Toast.LENGTH_SHORT).show()
            }
        }
    }
}