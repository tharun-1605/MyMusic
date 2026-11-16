package com.tharun.mymusic.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String = "", val icon: ImageVector? = null) {
    data object Splash : Screen("splash")
    data object Home : Screen("home", "Home", Icons.Default.Home)
    data object Search : Screen("search", "Search", Icons.Default.Search)
    data object Library : Screen("library", "Library", Icons.Default.LibraryMusic)
    data object Settings : Screen("settings", "Settings", Icons.Default.Settings)

    data object NowPlaying : Screen("now_playing")
    data object Queue : Screen("queue")
    data object Lyrics : Screen("lyrics/{songId}") {
        fun createRoute(songId: String) = "lyrics/$songId"
    }
    data object Equalizer : Screen("equalizer")
    data object PlaylistDetail : Screen("playlist/{playlistId}") {
        fun createRoute(playlistId: Long) = "playlist/$playlistId"
    }

    companion object {
        val bottomNavItems = listOf(Home, Search, Library, Settings)
    }
}
