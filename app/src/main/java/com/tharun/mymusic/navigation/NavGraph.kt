package com.tharun.mymusic.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tharun.mymusic.core.theme.AccentPrimary
import com.tharun.mymusic.core.ui.MiniPlayer
import com.tharun.mymusic.data.repository.PlaylistRepositoryImpl
import com.tharun.mymusic.data.repository.SongRepositoryImpl
import com.tharun.mymusic.feature.equalizer.EqualizerScreen
import com.tharun.mymusic.feature.equalizer.EqualizerViewModel
import com.tharun.mymusic.feature.home.HomeScreen
import com.tharun.mymusic.feature.home.HomeViewModel
import com.tharun.mymusic.feature.library.LibraryScreen
import com.tharun.mymusic.feature.library.LibraryViewModel
import com.tharun.mymusic.feature.lyrics.LyricsScreen
import com.tharun.mymusic.feature.lyrics.LyricsViewModel
import com.tharun.mymusic.feature.player.NowPlayingScreen
import com.tharun.mymusic.feature.player.PlayerViewModel
import com.tharun.mymusic.feature.player.QueueScreen
import com.tharun.mymusic.feature.playlists.PlaylistDetailScreen
import com.tharun.mymusic.feature.playlists.PlaylistDetailViewModel
import com.tharun.mymusic.feature.search.SearchScreen
import com.tharun.mymusic.feature.search.SearchViewModel
import com.tharun.mymusic.feature.settings.SettingsScreen
import com.tharun.mymusic.feature.settings.SettingsViewModel
import com.tharun.mymusic.feature.splash.SplashScreen

@Composable
fun MainAppScaffold(
    songRepository: SongRepositoryImpl,
    playlistRepository: PlaylistRepositoryImpl,
    navController: NavHostController = rememberNavController(),
    playerViewModel: PlayerViewModel = hiltViewModel()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val playbackState by playerViewModel.playbackState.collectAsState()

    val isTopLevelDestination = Screen.bottomNavItems.any { it.route == currentRoute }
    val showMiniPlayer = playbackState.currentSong != null && currentRoute != Screen.NowPlaying.route && currentRoute != Screen.Splash.route

    Scaffold(
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth().navigationBarsPadding()) {
                AnimatedVisibility(
                    visible = showMiniPlayer,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                ) {
                    MiniPlayer(
                        song = playbackState.currentSong,
                        isPlaying = playbackState.isPlaying,
                        progressMs = playbackState.currentPositionMs,
                        durationMs = playbackState.durationMs,
                        onPlayPauseClick = { playerViewModel.togglePlayPause() },
                        onNextClick = { playerViewModel.next() },
                        onClick = { navController.navigate(Screen.NowPlaying.route) }
                    )
                }

                if (isTopLevelDestination) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 8.dp
                    ) {
                        Screen.bottomNavItems.forEach { screen ->
                            val selected = currentRoute == screen.route
                            NavigationBarItem(
                                selected = selected,
                                onClick = {
                                    if (currentRoute != screen.route) {
                                        navController.navigate(screen.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                },
                                icon = {
                                    screen.icon?.let {
                                        Icon(imageVector = it, contentDescription = screen.title)
                                    }
                                },
                                label = { Text(screen.title) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = AccentPrimary,
                                    selectedTextColor = AccentPrimary,
                                    indicatorColor = AccentPrimary.copy(alpha = 0.12f)
                                )
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(
                    songRepository = songRepository,
                    playlistRepository = playlistRepository,
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Home.route) {
                val homeViewModel: HomeViewModel = hiltViewModel()
                HomeScreen(
                    viewModel = homeViewModel,
                    onPlaylistClick = { playlistId ->
                        navController.navigate(Screen.PlaylistDetail.createRoute(playlistId))
                    },
                    onSettingsClick = {
                        navController.navigate(Screen.Settings.route)
                    }
                )
            }

            composable(Screen.Search.route) {
                val searchViewModel: SearchViewModel = hiltViewModel()
                SearchScreen(viewModel = searchViewModel)
            }

            composable(Screen.Library.route) {
                val libraryViewModel: LibraryViewModel = hiltViewModel()
                LibraryScreen(
                    viewModel = libraryViewModel,
                    onPlaylistClick = { playlistId ->
                        navController.navigate(Screen.PlaylistDetail.createRoute(playlistId))
                    }
                )
            }

            composable(Screen.Settings.route) {
                val settingsViewModel: SettingsViewModel = hiltViewModel()
                SettingsScreen(
                    viewModel = settingsViewModel,
                    onEqualizerClick = { navController.navigate(Screen.Equalizer.route) }
                )
            }

            composable(Screen.NowPlaying.route) {
                NowPlayingScreen(
                    viewModel = playerViewModel,
                    onBackClick = { navController.popBackStack() },
                    onLyricsClick = { songId ->
                        navController.navigate(Screen.Lyrics.createRoute(songId))
                    },
                    onEqualizerClick = {
                        navController.navigate(Screen.Equalizer.route)
                    },
                    onQueueClick = {
                        navController.navigate(Screen.Queue.route)
                    }
                )
            }

            composable(Screen.Queue.route) {
                QueueScreen(
                    viewModel = playerViewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.Lyrics.route,
                arguments = listOf(navArgument("songId") { type = NavType.StringType })
            ) {
                val lyricsViewModel: LyricsViewModel = hiltViewModel()
                LyricsScreen(
                    viewModel = lyricsViewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.Equalizer.route) {
                val equalizerViewModel: EqualizerViewModel = hiltViewModel()
                EqualizerScreen(
                    viewModel = equalizerViewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.PlaylistDetail.route,
                arguments = listOf(navArgument("playlistId") { type = NavType.LongType })
            ) {
                val playlistDetailViewModel: PlaylistDetailViewModel = hiltViewModel()
                PlaylistDetailScreen(
                    viewModel = playlistDetailViewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
