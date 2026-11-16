package com.tharun.mymusic.feature.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tharun.mymusic.core.theme.AccentCyan
import com.tharun.mymusic.core.theme.AccentPrimary
import com.tharun.mymusic.core.theme.PrimaryGradient
import com.tharun.mymusic.data.repository.PlaylistRepositoryImpl
import com.tharun.mymusic.data.repository.SongRepositoryImpl
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    songRepository: SongRepositoryImpl,
    playlistRepository: PlaylistRepositoryImpl,
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale = remember { Animatable(0.4f) }

    LaunchedEffect(Unit) {
        // Initialize sample catalog if first launch
        songRepository.seedSampleCatalogIfEmpty()
        playlistRepository.seedSamplePlaylistsIfEmpty()

        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        delay(600)
        onNavigateToHome()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .scale(scale.value)
                    .shadow(elevation = 32.dp, shape = CircleShape, spotColor = AccentPrimary)
                    .clip(CircleShape)
                    .background(PrimaryGradient),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(56.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "MyMusic",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Discover • Stream • Enjoy",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = AccentCyan,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}
