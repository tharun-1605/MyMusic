package com.tharun.mymusic.feature.lyrics

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsDraggedAsState
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardDoubleArrowDown
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tharun.mymusic.core.theme.AccentCyan
import com.tharun.mymusic.core.theme.AccentPrimary
import com.tharun.mymusic.core.ui.AlbumArt
import com.tharun.mymusic.core.ui.EmptyStateView

@Composable
fun LyricsScreen(
    viewModel: LyricsViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val isDragged by listState.interactionSource.collectIsDraggedAsState()

    // Detect user manual scroll
    LaunchedEffect(isDragged) {
        if (isDragged) {
            viewModel.onUserScrolled()
        }
    }

    // Auto-scroll to active line when not paused by user scroll
    LaunchedEffect(state.activeLineIndex, state.isAutoScrollPaused) {
        if (!state.isAutoScrollPaused && state.activeLineIndex >= 0) {
            val target = (state.activeLineIndex - 2).coerceAtLeast(0)
            listState.animateScrollToItem(target)
        }
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
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                state.song?.let { song ->
                    AlbumArt(uri = song.artworkUri, size = 40.dp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = song.title,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = song.artist,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            when {
                state.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = AccentPrimary)
                    }
                }
                state.hasNoLyrics -> {
                    EmptyStateView(
                        message = "No lyrics found for this track",
                        modifier = Modifier.fillMaxSize()
                    )
                }
                state.lyrics?.isSynced == true -> {
                    val lines = state.lyrics?.lines.orEmpty()
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 60.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        itemsIndexed(lines) { index, line ->
                            val isActive = index == state.activeLineIndex

                            Text(
                                text = line.text,
                                style = if (isActive) {
                                    MaterialTheme.typography.headlineMedium.copy(
                                        color = AccentCyan,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 24.sp,
                                        lineHeight = 32.sp
                                    )
                                } else {
                                    MaterialTheme.typography.titleLarge.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 18.sp,
                                        lineHeight = 26.sp
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        viewModel.seekToLine(line.timestampMs)
                                    }
                                    .padding(vertical = 4.dp)
                            )
                        }
                    }
                }
                else -> {
                    // Plain text lyrics
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp, vertical = 16.dp)
                    ) {
                        item {
                            Text(
                                text = state.lyrics?.plainText.orEmpty(),
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    lineHeight = 28.sp,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            )
                        }
                    }
                }
            }
        }

        // Return to current synced line button
        AnimatedVisibility(
            visible = state.isAutoScrollPaused && state.lyrics?.isSynced == true,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = AccentPrimary,
                shadowElevation = 8.dp,
                onClick = { viewModel.resumeAutoScroll() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = "Sync",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Sync with audio",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}
