package com.tharun.mymusic.feature.home

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tharun.mymusic.core.theme.AccentCyan
import com.tharun.mymusic.core.theme.AccentPrimary
import com.tharun.mymusic.core.theme.PrimaryGradient
import com.tharun.mymusic.core.ui.AlbumArt
import com.tharun.mymusic.core.ui.CompliantYouTubeDialog
import com.tharun.mymusic.core.ui.EmptyStateView
import com.tharun.mymusic.core.ui.ErrorStateView
import com.tharun.mymusic.core.ui.SongItemRow
import com.tharun.mymusic.domain.model.AudioSourceProvider
import com.tharun.mymusic.domain.model.Playlist
import com.tharun.mymusic.domain.model.Song

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onPlaylistClick: (Long) -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var youtubeHandoffSong by remember { mutableStateOf<Song?>(null) }

    youtubeHandoffSong?.let { song ->
        CompliantYouTubeDialog(
            song = song,
            onDismiss = { youtubeHandoffSong = null },
            onOpenOfficialApp = {
                val videoId = song.youtubeVideoId
                if (videoId != null) {
                    val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$videoId")).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=$videoId")).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    try {
                        context.startActivity(appIntent)
                    } catch (_: Exception) {
                        context.startActivity(webIntent)
                    }
                }
            }
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when (val s = state) {
            is HomeUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AccentPrimary)
                }
            }
            is HomeUiState.Error -> {
                ErrorStateView(
                    message = s.message,
                    onRetry = { viewModel.loadHomeData() },
                    modifier = Modifier.fillMaxSize()
                )
            }
            is HomeUiState.Success -> {
                val data = s.data
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 120.dp)
                ) {
                    // Header Bar
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = data.greeting,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                                Text(
                                    text = "Discover Music",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }

                            IconButton(onClick = onSettingsClick) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Settings",
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }

                    // Featured Playlists Row
                    if (data.featuredPlaylists.isNotEmpty()) {
                        item {
                            Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                                Text(
                                    text = "Featured Playlists",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                                )

                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 20.dp),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    items(data.featuredPlaylists) { playlist ->
                                        FeaturedPlaylistCard(
                                            playlist = playlist,
                                            onClick = { onPlaylistClick(playlist.id) },
                                            onPlayClick = { viewModel.playPlaylist(playlist) }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Recently Played Section
                    if (data.recentlyPlayed.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = "Recently Played",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                            )
                        }

                        items(data.recentlyPlayed.take(5)) { song ->
                            SongItemRow(
                                song = song,
                                isPlaying = false,
                                onSongClick = {
                                    if (song.isPlayable) {
                                        viewModel.playSong(song, data.recentlyPlayed)
                                    } else {
                                        youtubeHandoffSong = song
                                    }
                                },
                                onFavoriteToggle = { viewModel.toggleFavorite(song) },
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                        }
                    }

                    // Discover / Trending Tracks
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Discover & Trending",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    items(data.discoverSongs) { song ->
                        SongItemRow(
                            song = song,
                            isPlaying = false,
                            onSongClick = {
                                if (song.isPlayable) {
                                    viewModel.playSong(song, data.discoverSongs)
                                } else {
                                    youtubeHandoffSong = song
                                }
                            },
                            onFavoriteToggle = { viewModel.toggleFavorite(song) },
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FeaturedPlaylistCard(
    playlist: Playlist,
    onClick: () -> Unit,
    onPlayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(180.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(156.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                AlbumArt(
                    uri = playlist.artworkUri,
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(12.dp)
                )

                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(AccentPrimary)
                        .align(Alignment.BottomEnd)
                        .clickable(onClick = onPlayClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = playlist.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = playlist.description ?: "${playlist.songCount} songs",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
