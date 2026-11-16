package com.tharun.mymusic.feature.equalizer

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tharun.mymusic.core.theme.AccentCyan
import com.tharun.mymusic.core.theme.AccentPrimary
import com.tharun.mymusic.domain.model.EqualizerPreset
import java.util.Locale

@Composable
fun EqualizerScreen(
    viewModel: EqualizerViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val config = state.config
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Equalizer & Effects",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            }

            Switch(
                checked = config.isEnabled,
                onCheckedChange = { viewModel.toggleEnabled(it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = AccentPrimary
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Device note
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = AccentCyan,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Audio effects attach to active playback session. Ensure playback is started for hardware effects.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Presets Chips
        Text(
            text = "Presets",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            EqualizerPreset.entries.forEach { preset ->
                val isSelected = config.preset == preset
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        if (config.isEnabled) viewModel.selectPreset(preset)
                    },
                    label = {
                        Text(
                            text = preset.name.replace("_", " ").lowercase()
                                .replaceFirstChar { it.uppercase() }
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AccentPrimary,
                        selectedLabelColor = Color.White
                    ),
                    enabled = config.isEnabled
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Bass Boost & Virtualizer
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Bass Boost",
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${config.bassBoostLevel / 10}%",
                        style = MaterialTheme.typography.labelSmall.copy(color = AccentPrimary)
                    )
                    Slider(
                        value = config.bassBoostLevel.toFloat(),
                        onValueChange = { viewModel.setBassBoost(it.toInt()) },
                        valueRange = 0f..1000f,
                        enabled = config.isEnabled,
                        colors = SliderDefaults.colors(thumbColor = AccentPrimary, activeTrackColor = AccentPrimary)
                    )
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Virtualizer",
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${config.virtualizerLevel / 10}%",
                        style = MaterialTheme.typography.labelSmall.copy(color = AccentCyan)
                    )
                    Slider(
                        value = config.virtualizerLevel.toFloat(),
                        onValueChange = { viewModel.setVirtualizer(it.toInt()) },
                        valueRange = 0f..1000f,
                        enabled = config.isEnabled,
                        colors = SliderDefaults.colors(thumbColor = AccentCyan, activeTrackColor = AccentCyan)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Frequency Bands
        Text(
            text = "Graphic Frequency Bands",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                state.bands.forEach { band ->
                    val gain = band.gainDb
                    val label = if (band.centerFreqHz >= 1000) {
                        "${band.centerFreqHz / 1000} kHz"
                    } else {
                        "${band.centerFreqHz} Hz"
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            modifier = Modifier.width(64.dp)
                        )

                        Slider(
                            value = gain,
                            onValueChange = { newDb ->
                                viewModel.setBandGain(band.index, (newDb * 100).toInt())
                            },
                            valueRange = band.minDb..band.maxDb,
                            enabled = config.isEnabled,
                            colors = SliderDefaults.colors(
                                thumbColor = AccentPrimary,
                                activeTrackColor = AccentPrimary
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        Text(
                            text = String.format(Locale.getDefault(), "%+.1f dB", gain),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.width(60.dp)
                        )
                    }
                }
            }
        }
    }
}
