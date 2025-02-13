package com.smurzik.mediaplayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.WorkDuration
import android.widget.Button
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {

    private lateinit var seekBar: SeekBar
    private lateinit var viewModel: MainViewModel
    private lateinit var myUri: Uri
    private var duration = 0

    private val REQUEST_CODE = 123

    private val progressReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val progress = intent?.getIntExtra(PlayerService.EXTRA_PROGRESS, 0) ?: return
            viewModel.updateProgress(progress)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        seekBar = findViewById(R.id.seekBar)
        viewModel = MainViewModel(this)

        viewModel.trackProgress.observe(this) {
            seekBar.progress = it
            seekBar.max = duration
        }

        val startButton = findViewById<Button>(R.id.playButton)
        val stopButton = findViewById<Button>(R.id.stopButton)

        startButton.setOnClickListener {
            Intent(applicationContext, PlayerService::class.java).also {
                it.action = PlayerService.Actions.START.toString()
                it.putExtra(PlayerService.EXTRA_TRACK_URI, myUri.toString())
                startService(it)
            }
        }

        stopButton.setOnClickListener {
            Intent(applicationContext, PlayerService::class.java).also {
                it.action = PlayerService.Actions.STOP.toString()
                startService(it)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        checkAndRequestPermissions()
        val filter = IntentFilter(PlayerService.PROGRESS_UPDATE)
        LocalBroadcastManager.getInstance(this).registerReceiver(progressReceiver, filter)
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(progressReceiver)
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
            initializePlayer()
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
                initializePlayer()
            } else {
                Toast.makeText(this, "Некоторые разрешения не получены", Toast.LENGTH_SHORT).show()
            }
        }
    }

//    private fun checkPermissions(): Boolean {
//        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            ActivityCompat.checkSelfPermission(
//                this,
//                android.Manifest.permission.READ_MEDIA_AUDIO
//            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                this,
//                android.Manifest.permission.FOREGROUND_SERVICE
//            ) == PackageManager.PERMISSION_GRANTED
//        } else {
//            ActivityCompat.checkSelfPermission(
//                this,
//                android.Manifest.permission.READ_EXTERNAL_STORAGE
//            ) == PackageManager.PERMISSION_GRANTED
//        }
//    }
//
//    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
//    private fun requestRuntimePermissions() {
//        if (checkPermissions()) {
//
//        } else if (ActivityCompat.shouldShowRequestPermissionRationale(
//                this,
//                android.Manifest.permission.READ_MEDIA_AUDIO
//            )
//        ) {
//            showPermissionRationaleDialog()
//        } else {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(
//                    android.Manifest.permission.READ_MEDIA_AUDIO,
//                    android.Manifest.permission.FOREGROUND_SERVICE
//                ),
//                REQUEST_CODE
//            )
//        }
//    }
//
//    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
//    private fun showPermissionRationaleDialog() {
//        Log.d("smurzLog", "show dialog")
//        AlertDialog.Builder(this)
//            .setTitle("Разрешение требуется")
//            .setMessage("Доступ к аудиофайлам необходим для воспроизведения музыки.")
//            .setPositiveButton("OK") { dialog, _ ->
//                ActivityCompat.requestPermissions(
//                    this,
//                    arrayOf(
//                        android.Manifest.permission.READ_MEDIA_AUDIO,
//                        android.Manifest.permission.FOREGROUND_SERVICE
//                    ),
//                    REQUEST_CODE
//                )
//                dialog.dismiss()
//            }
//            .setNegativeButton("Отмена") { dialog, _ ->
//                dialog.dismiss()
//            }
//            .show()
//    }
//
//    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//        } else if (!ActivityCompat.shouldShowRequestPermissionRationale(
//                this,
//                android.Manifest.permission.READ_MEDIA_AUDIO
//            )
//        ) {
//            AlertDialog.Builder(this)
//                .setTitle("Разрешение требуется")
//                .setMessage("Доступ к аудиофайлам необходим для воспроизведения музыки.")
//                .setPositiveButton("Настройки") { dialog, _ ->
//                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//                    val uri = Uri.fromParts("package", packageName, null)
//                    intent.setData(uri)
//                    startActivity(intent)
//                    dialog.dismiss()
//                }
//                .setNegativeButton("Отмена") { dialog, _ ->
//                    dialog.dismiss()
//                }
//                .show()
//        } else {
//            requestRuntimePermissions()
//            Toast.makeText(this, "Разрешение не получено", Toast.LENGTH_SHORT).show()
//        }
//    }

    private fun initializePlayer() {
        duration = viewModel.getAllAudioTracks(this).first().duration.toInt() / 1000
        myUri = viewModel.getAllAudioTracks(this).first().uri
    }
}