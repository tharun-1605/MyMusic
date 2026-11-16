package com.tharun.mymusic.feature.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tharun.mymusic.core.theme.AccentCyan
import com.tharun.mymusic.core.theme.AccentPrimary
import com.tharun.mymusic.core.theme.PrimaryGradient
import com.tharun.mymusic.core.ui.AlbumArt
import com.tharun.mymusic.core.ui.EmptyStateView
import com.tharun.mymusic.core.ui.GlowingPlayButton
import com.tharun.mymusic.core.util.TimeUtils
import com.tharun.mymusic.domain.model.RepeatMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NowPlayingScreen(
    viewModel: PlayerViewModel,
    onBackClick: () -> Unit,
    onLyricsClick: (String) -> Unit,
    onEqualizerClick: () -> Unit,
    onQueueClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.playbackState.collectAsState()
    val song = state.currentSong

    if (song == null) {
        Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.padding(16.dp).align(Alignment.TopStart)
            ) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Close")
            }
            EmptyStateView(
                message = "No track currently playing",
                modifier = Modifier.align(Alignment.Center)
            )
        }
        return
    }

    var isUserSeeking by remember { mutableStateOf(false) }
    var seekPositionSlider by remember { mutableFloatStateOf(0f) }

    val currentPosition = if (isUserSeeking) {
        seekPositionSlider.toLong()
    } else {
        state.currentPositionMs
    }

    val totalDuration = state.durationMs.coerceAtLeast(song.durationMs)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp)
            .padding(top = 16.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Collapse",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(32.dp)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "PLAYING FROM",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                Text(
                    text = song.album.ifEmpty { "MyMusic Library" },
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(onClick = onEqualizerClick) {
                Icon(
                    imageVector = Icons.Default.GraphicEq,
                    contentDescription = "Equalizer",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Large Center Artwork with subtle shadow and border
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .aspectRatio(1f)
                .shadow(elevation = 24.dp, shape = RoundedCornerShape(24.dp), spotColor = AccentPrimary)
                .clip(RoundedCornerShape(24.dp))
        ) {
            AlbumArt(
                uri = song.artworkUri,
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Title & Artist + Favorite Action
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = song.artist,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(
                onClick = { viewModel.toggleFavorite(song) },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = if (song.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (song.isFavorite) AccentPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Progress Slider & Timestamps
        Column(modifier = Modifier.fillMaxWidth()) {
            Slider(
                value = if (totalDuration > 0) currentPosition.toFloat() else 0f,
                onValueChange = {
                    isUserSeeking = true
                    seekPositionSlider = it
                },
                onValueChangeFinished = {
                    viewModel.seekTo(seekPositionSlider.toLong())
                    isUserSeeking = false
                },
                valueRange = 0f..(if (totalDuration > 0) totalDuration.toFloat() else 1f),
                colors = SliderDefaults.colors(
                    thumbColor = AccentPrimary,
                    activeTrackColor = AccentPrimary,
                    inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = TimeUtils.formatDurationMs(currentPosition),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                Text(
                    text = TimeUtils.formatDurationMs(totalDuration),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Playback Controls Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.toggleShuffle() }) {
                Icon(
                    imageVector = Icons.Default.Shuffle,
                    contentDescription = "Shuffle",
                    tint = if (state.isShuffleEnabled) AccentPrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(26.dp)
                )
            }

            IconButton(onClick = { viewModel.previous() }, modifier = Modifier.size(48.dp)) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = "Previous",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(36.dp)
                )
            }

            GlowingPlayButton(
                isPlaying = state.isPlaying,
                onClick = { viewModel.togglePlayPause() },
                size = 72.dp
            )

            IconButton(onClick = { viewModel.next() }, modifier = Modifier.size(48.dp)) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Next",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(36.dp)
                )
            }

            IconButton(onClick = { viewModel.toggleRepeat() }) {
                Icon(
                    imageVector = if (state.repeatMode == RepeatMode.ONE) Icons.Default.RepeatOne else Icons.Default.Repeat,
                    contentDescription = "Repeat",
                    tint = if (state.repeatMode != RepeatMode.OFF) AccentPrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bottom Action Bar: Lyrics & Queue
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.weight(1f).padding(end = 8.dp),
                onClick = { onLyricsClick(song.id) }
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Subtitles,
                        contentDescription = "Lyrics",
                        tint = AccentCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Lyrics",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.weight(1f).padding(start = 8.dp),
                onClick = onQueueClick
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.QueueMusic,
                        contentDescription = "Queue",
                        tint = AccentPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Queue (${state.queue.size})",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
            }
        }
    }
}
