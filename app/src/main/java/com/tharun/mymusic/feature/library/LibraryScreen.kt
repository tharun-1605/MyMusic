package com.tharun.mymusic.feature.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tharun.mymusic.core.theme.AccentCyan
import com.tharun.mymusic.core.theme.AccentPrimary
import com.tharun.mymusic.core.ui.AlbumArt
import com.tharun.mymusic.core.ui.EmptyStateView
import com.tharun.mymusic.core.ui.SongItemRow

@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel,
    onPlaylistClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    var showCreatePlaylistDialog by remember { mutableStateOf(false) }
    var newPlaylistName by remember { mutableStateOf("") }
    var newPlaylistDesc by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.dismissMessage()
        }
    }

    if (showCreatePlaylistDialog) {
        AlertDialog(
            onDismissRequest = { showCreatePlaylistDialog = false },
            title = { Text("New Playlist") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newPlaylistName,
                        onValueChange = { newPlaylistName = it },
                        label = { Text("Playlist Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newPlaylistDesc,
                        onValueChange = { newPlaylistDesc = it },
                        label = { Text("Description (Optional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPlaylistName.isNotBlank()) {
                            viewModel.createPlaylist(newPlaylistName.trim(), newPlaylistDesc.trim().ifEmpty { null })
                            newPlaylistName = ""
                            newPlaylistDesc = ""
                            showCreatePlaylistDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentPrimary)
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreatePlaylistDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your Library",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                )

                Row {
                    IconButton(onClick = { viewModel.scanDeviceFiles() }) {
                        if (state.isScanning) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = AccentPrimary)
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Scan Device Audio",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }

                    IconButton(onClick = { showCreatePlaylistDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.PlaylistAdd,
                            contentDescription = "New Playlist",
                            tint = AccentPrimary
                        )
                    }
                }
            }

            // Tabs
            val tabs = LibraryTab.entries
            TabRow(
                selectedTabIndex = state.selectedTab.ordinal,
                containerColor = Color.Transparent,
                contentColor = AccentPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[state.selectedTab.ordinal]),
                        color = AccentPrimary
                    )
                }
            ) {
                tabs.forEach { tab ->
                    val title = when (tab) {
                        LibraryTab.SONGS -> "Songs"
                        LibraryTab.PLAYLISTS -> "Playlists"
                        LibraryTab.FAVORITES -> "Favorites"
                        LibraryTab.ARTISTS -> "Artists"
                    }
                    Tab(
                        selected = state.selectedTab == tab,
                        onClick = { viewModel.selectTab(tab) },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = if (state.selectedTab == tab) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = if (state.selectedTab == tab) AccentPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
            }

            // Tab Content
            when (state.selectedTab) {
                LibraryTab.SONGS -> {
                    if (state.songs.isEmpty()) {
                        EmptyStateView(
                            message = "No songs found. Tap refresh to scan device files or enjoy the sample classical library.",
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 120.dp)
                        ) {
                            item {
                                PlayAllHeader(
                                    songCount = state.songs.size,
                                    onPlayAll = { viewModel.playAll(state.songs) }
                                )
                            }
                            items(state.songs) { song ->
                                SongItemRow(
                                    song = song,
                                    isPlaying = false,
                                    onSongClick = { viewModel.playSong(song, state.songs) },
                                    onFavoriteToggle = { viewModel.toggleFavorite(song) },
                                    modifier = Modifier.padding(horizontal = 12.dp)
                                )
                            }
                        }
                    }
                }
                LibraryTab.PLAYLISTS -> {
                    if (state.playlists.isEmpty()) {
                        EmptyStateView(
                            message = "No playlists yet. Create one with the + icon above!",
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            items(state.playlists) { playlist ->
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { onPlaylistClick(playlist.id) },
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AlbumArt(uri = playlist.artworkUri, size = 52.dp)
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = playlist.name,
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = playlist.description ?: "Playlist",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                ),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                LibraryTab.FAVORITES -> {
                    if (state.favoriteSongs.isEmpty()) {
                        EmptyStateView(
                            message = "No favorite tracks yet. Heart songs to add them here!",
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 120.dp)
                        ) {
                            item {
                                PlayAllHeader(
                                    songCount = state.favoriteSongs.size,
                                    onPlayAll = { viewModel.playAll(state.favoriteSongs) }
                                )
                            }
                            items(state.favoriteSongs) { song ->
                                SongItemRow(
                                    song = song,
                                    isPlaying = false,
                                    onSongClick = { viewModel.playSong(song, state.favoriteSongs) },
                                    onFavoriteToggle = { viewModel.toggleFavorite(song) },
                                    modifier = Modifier.padding(horizontal = 12.dp)
                                )
                            }
                        }
                    }
                }
                LibraryTab.ARTISTS -> {
                    if (state.artists.isEmpty()) {
                        EmptyStateView(
                            message = "No artists found in library.",
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            items(state.artists) { artist ->
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(12.dp)),
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(CircleShape)
                                                .background(AccentPrimary.copy(alpha = 0.2f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Person,
                                                contentDescription = null,
                                                tint = AccentPrimary,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Column {
                                            Text(
                                                text = artist.name,
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                            )
                                            Text(
                                                text = "${artist.trackCount} tracks",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 90.dp)
        )
    }
}

@Composable
fun PlayAllHeader(
    songCount: Int,
    onPlayAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$songCount Songs",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        Button(
            onClick = onPlayAll,
            colors = ButtonDefaults.buttonColors(containerColor = AccentPrimary),
            shape = RoundedCornerShape(20.dp)
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Play All")
        }
    }
}
