package com.smurzik.mediaplayer

import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.common.util.concurrent.MoreExecutors
import com.smurzik.mediaplayer.core.MediaPlayerApp
import com.smurzik.mediaplayer.core.PlaybackService
import com.smurzik.mediaplayer.databinding.ActivityMainBinding
import com.smurzik.mediaplayer.local.presentation.ClickListener
import com.smurzik.mediaplayer.local.presentation.LocalTrackListAdapter
import com.smurzik.mediaplayer.local.presentation.LocalTrackUi

class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE = 123
    private lateinit var binding: ActivityMainBinding
    private var mediaController: MediaController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setContentView(binding.root)

        checkAndRequestPermissions()
    }

    override fun onStart() {
        super.onStart()
        val sessionToken = SessionToken(this, ComponentName(this, PlaybackService::class.java))
        val controllerFuture = MediaController.Builder(this, sessionToken).buildAsync()
        controllerFuture.addListener(
            {
                mediaController = controllerFuture.get()
            },
            MoreExecutors.directExecutor()
        )
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