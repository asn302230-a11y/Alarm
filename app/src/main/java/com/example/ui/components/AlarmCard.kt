package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AlarmItem
import com.example.ui.theme.IraqBlack
import com.example.ui.theme.IraqCardDark
import com.example.ui.theme.IraqGold
import com.example.ui.theme.IraqGreen
import com.example.ui.theme.IraqRed
import com.example.ui.theme.IraqSurfaceDark
import com.example.ui.theme.IraqWhite
import java.util.Calendar

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AlarmCard(
    alarm: AlarmItem,
    isPlayingPreview: Boolean,
    onToggle: () -> Unit,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onPreviewSound: () -> Unit,
    onTestTrigger: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .testTag("alarm_card_${alarm.id}"),
        colors = CardDefaults.cardColors(
            containerColor = IraqCardDark
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            // Left Iraq Flag Tri-Color accent strip
            Column(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(if (alarm.isEnabled) IraqRed else IraqRed.copy(alpha = 0.4f))
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(if (alarm.isEnabled) IraqWhite else IraqWhite.copy(alpha = 0.4f))
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(if (alarm.isEnabled) IraqBlack else IraqBlack.copy(alpha = 0.4f))
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp, vertical = 14.dp)
            ) {
                // Top row: Time + AM/PM + Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = alarm.formatted12HourTime,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (alarm.isEnabled) IraqWhite else IraqWhite.copy(alpha = 0.45f)
                        )
                        Text(
                            text = alarm.amPm,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (alarm.isEnabled) IraqGold else IraqGold.copy(alpha = 0.45f),
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    Switch(
                        checked = alarm.isEnabled,
                        onCheckedChange = { onToggle() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = IraqWhite,
                            checkedTrackColor = IraqGreen,
                            uncheckedThumbColor = IraqWhite.copy(alpha = 0.6f),
                            uncheckedTrackColor = Color.DarkGray
                        ),
                        modifier = Modifier.testTag("alarm_switch_${alarm.id}")
                    )
                }

                // Label
                Text(
                    text = alarm.label.ifBlank { "Alarm" },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (alarm.isEnabled) IraqWhite.copy(alpha = 0.9f) else IraqWhite.copy(alpha = 0.4f),
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Repeat days pills
                val days = listOf(
                    Calendar.SUNDAY to "S",
                    Calendar.MONDAY to "M",
                    Calendar.TUESDAY to "T",
                    Calendar.WEDNESDAY to "W",
                    Calendar.THURSDAY to "T",
                    Calendar.FRIDAY to "F",
                    Calendar.SATURDAY to "S"
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    days.forEach { (calDay, shortName) ->
                        val isDaySelected = alarm.daysOfWeek.contains(calDay)
                        val isWeekend = calDay == Calendar.FRIDAY || calDay == Calendar.SATURDAY
                        val pillBg = when {
                            !alarm.isEnabled -> Color.White.copy(alpha = 0.05f)
                            isDaySelected && isWeekend -> IraqRed
                            isDaySelected -> IraqGreen
                            else -> Color.White.copy(alpha = 0.08f)
                        }
                        val pillTextColor = when {
                            !alarm.isEnabled -> IraqWhite.copy(alpha = 0.3f)
                            isDaySelected -> IraqWhite
                            else -> IraqWhite.copy(alpha = 0.4f)
                        }

                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(pillBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = shortName,
                                fontSize = 11.sp,
                                fontWeight = if (isDaySelected) FontWeight.Bold else FontWeight.Normal,
                                color = pillTextColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Footer chips and actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        // Sound chip
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White.copy(alpha = 0.08f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MusicNote,
                                    contentDescription = null,
                                    tint = IraqGold,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = alarm.soundTone,
                                    fontSize = 11.sp,
                                    color = IraqWhite.copy(alpha = 0.8f)
                                )
                            }
                        }

                        if (alarm.challengeRequired) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = IraqRed.copy(alpha = 0.2f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Psychology,
                                        contentDescription = null,
                                        tint = IraqRed,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = "Math Puzzle",
                                        fontSize = 11.sp,
                                        color = IraqWhite.copy(alpha = 0.9f)
                                    )
                                }
                            }
                        }
                    }

                    // Quick Actions: Test Ringing, Sound Preview, Delete
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Test Ringing Full Screen Button
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = IraqGreen.copy(alpha = 0.2f),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onTestTrigger() }
                                .testTag("test_alarm_btn_${alarm.id}")
                        ) {
                            Text(
                                text = "Test",
                                color = IraqGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                        }

                        // Preview Sound button
                        IconButton(
                            onClick = onPreviewSound,
                            modifier = Modifier.size(32.dp).testTag("preview_sound_${alarm.id}")
                        ) {
                            Icon(
                                imageVector = if (isPlayingPreview) Icons.Default.Stop else Icons.Default.PlayArrow,
                                contentDescription = "Preview Tone",
                                tint = if (isPlayingPreview) IraqRed else IraqGold,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Delete button
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(32.dp).testTag("delete_alarm_${alarm.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Alarm",
                                tint = IraqWhite.copy(alpha = 0.45f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
