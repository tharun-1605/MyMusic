package com.tharun.mymusic

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.tharun.mymusic.core.theme.MyMusicTheme
import com.tharun.mymusic.core.theme.ThemeMode
import com.tharun.mymusic.data.repository.PlaylistRepositoryImpl
import com.tharun.mymusic.data.repository.SongRepositoryImpl
import com.tharun.mymusic.domain.repository.UserPreferencesRepository
import com.tharun.mymusic.navigation.MainAppScaffold
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var userPreferencesRepository: UserPreferencesRepository
    @Inject lateinit var songRepository: SongRepositoryImpl
    @Inject lateinit var playlistRepository: PlaylistRepositoryImpl

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Scans can be triggered if audio permissions are granted
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        requestRequiredPermissions()

        setContent {
            val themeMode by userPreferencesRepository.themeMode.collectAsState(initial = ThemeMode.DARK)

            MyMusicTheme(themeMode = themeMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainAppScaffold(
                        songRepository = songRepository,
                        playlistRepository = playlistRepository
                    )
                }
            }
        }
    }

    private fun requestRequiredPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(Manifest.permission.READ_MEDIA_AUDIO)
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }
}
