package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.util.SoundPlayer
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditAlarmSheet(
    initialAlarm: AlarmItem,
    isCreatingNew: Boolean,
    previewingTone: String?,
    onSave: (AlarmItem) -> Unit,
    onDismiss: () -> Unit,
    onPreviewTone: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var hour by remember { mutableIntStateOf(initialAlarm.hour) }
    var minute by remember { mutableIntStateOf(initialAlarm.minute) }
    var label by remember { mutableStateOf(initialAlarm.label) }
    var isEnabled by remember { mutableStateOf(initialAlarm.isEnabled) }
    var daysOfWeek by remember { mutableStateOf(initialAlarm.daysOfWeek.toSet()) }
    var vibrate by remember { mutableStateOf(initialAlarm.vibrate) }
    var snoozeMinutes by remember { mutableIntStateOf(initialAlarm.snoozeMinutes) }
    var soundTone by remember { mutableStateOf(initialAlarm.soundTone) }
    var challengeRequired by remember { mutableStateOf(initialAlarm.challengeRequired) }

    val is12Hour = true
    val displayHour = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
    val isPm = hour >= 12

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = IraqSurfaceDark,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)
        ) {
            // Iraqi flag stripe on sheet top
            IraqFlagStripe(height = 5.dp, cornerRadius = 0.dp)

            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(36.dp).testTag("close_sheet_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cancel",
                        tint = IraqWhite.copy(alpha = 0.8f)
                    )
                }

                Text(
                    text = if (isCreatingNew) "New Alarm • منبه جديد" else "Edit Alarm • تعديل المنبه",
                    color = IraqWhite,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = {
                        val updated = initialAlarm.copy(
                            hour = hour,
                            minute = minute,
                            label = label.ifBlank { "Alarm" },
                            isEnabled = isEnabled,
                            daysOfWeek = daysOfWeek.toList().sorted(),
                            vibrate = vibrate,
                            snoozeMinutes = snoozeMinutes,
                            soundTone = soundTone,
                            challengeRequired = challengeRequired
                        )
                        onSave(updated)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = IraqGreen,
                        contentColor = IraqWhite
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("save_alarm_button")
                ) {
                    Text("Save", fontWeight = FontWeight.Bold)
                }
            }

            // Time Selector Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = IraqCardDark)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "SET TIME",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = IraqGold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Large Time Adjustment Display
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // Hour Picker
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButton(
                                onClick = {
                                    hour = (hour + 1) % 24
                                },
                                modifier = Modifier.size(36.dp).testTag("hour_up")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Hour Up", tint = IraqWhite)
                            }

                            Text(
                                text = String.format("%02d", displayHour),
                                fontSize = 52.sp,
                                fontWeight = FontWeight.Black,
                                color = IraqWhite
                            )

                            IconButton(
                                onClick = {
                                    hour = if (hour == 0) 23 else hour - 1
                                },
                                modifier = Modifier.size(36.dp).testTag("hour_down")
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Hour Down", tint = IraqWhite)
                            }
                        }

                        Text(
                            text = ":",
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Black,
                            color = IraqRed,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )

                        // Minute Picker
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButton(
                                onClick = {
                                    minute = (minute + 1) % 60
                                },
                                modifier = Modifier.size(36.dp).testTag("minute_up")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Minute Up", tint = IraqWhite)
                            }

                            Text(
                                text = String.format("%02d", minute),
                                fontSize = 52.sp,
                                fontWeight = FontWeight.Black,
                                color = IraqWhite
                            )

                            IconButton(
                                onClick = {
                                    minute = if (minute == 0) 59 else minute - 1
                                },
                                modifier = Modifier.size(36.dp).testTag("minute_down")
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Minute Down", tint = IraqWhite)
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // AM/PM Toggle
                        Column(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(IraqBlack)
                                .padding(4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (!isPm) IraqRed else Color.Transparent)
                                    .clickable {
                                        if (isPm) hour -= 12
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "AM",
                                    color = IraqWhite,
                                    fontSize = 13.sp,
                                    fontWeight = if (!isPm) FontWeight.Bold else FontWeight.Normal
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isPm) IraqRed else Color.Transparent)
                                    .clickable {
                                        if (!isPm) hour += 12
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "PM",
                                    color = IraqWhite,
                                    fontSize = 13.sp,
                                    fontWeight = if (isPm) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Repeat Days Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = IraqCardDark)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "REPEAT DAYS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = IraqGold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val dayInfo = listOf(
                        Calendar.SUNDAY to "Sun",
                        Calendar.MONDAY to "Mon",
                        Calendar.TUESDAY to "Tue",
                        Calendar.WEDNESDAY to "Wed",
                        Calendar.THURSDAY to "Thu",
                        Calendar.FRIDAY to "Fri",
                        Calendar.SATURDAY to "Sat"
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        dayInfo.forEach { (calDay, name) ->
                            val isSelected = daysOfWeek.contains(calDay)
                            val isWeekend = calDay == Calendar.FRIDAY || calDay == Calendar.SATURDAY
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isSelected && isWeekend -> IraqRed
                                            isSelected -> IraqGreen
                                            else -> Color.White.copy(alpha = 0.08f)
                                        }
                                    )
                                    .clickable {
                                        daysOfWeek = if (isSelected) {
                                            daysOfWeek - calDay
                                        } else {
                                            daysOfWeek + calDay
                                        }
                                    }
                                    .testTag("day_btn_$name"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = name.take(2),
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) IraqWhite else IraqWhite.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Shortcut buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color.White.copy(alpha = 0.08f),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    daysOfWeek = setOf(
                                        Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY,
                                        Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY
                                    )
                                }
                        ) {
                            Text(
                                text = "Every day",
                                fontSize = 11.sp,
                                color = IraqWhite,
                                modifier = Modifier.padding(vertical = 8.dp),
                                fontWeight = FontWeight.Medium,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color.White.copy(alpha = 0.08f),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    daysOfWeek = setOf(
                                        Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY,
                                        Calendar.WEDNESDAY, Calendar.THURSDAY
                                    )
                                }
                        ) {
                            Text(
                                text = "Sun - Thu (Work)",
                                fontSize = 11.sp,
                                color = IraqWhite,
                                modifier = Modifier.padding(vertical = 8.dp),
                                fontWeight = FontWeight.Medium,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color.White.copy(alpha = 0.08f),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    daysOfWeek = setOf(Calendar.FRIDAY, Calendar.SATURDAY)
                                }
                        ) {
                            Text(
                                text = "Fri - Sat (Weekend)",
                                fontSize = 11.sp,
                                color = IraqWhite,
                                modifier = Modifier.padding(vertical = 8.dp),
                                fontWeight = FontWeight.Medium,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Label & Quick Presets Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = IraqCardDark)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "ALARM LABEL",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = IraqGold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = label,
                        onValueChange = { label = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("alarm_label_input"),
                        placeholder = { Text("Alarm name (e.g. Morning Wakeup)", color = Color.Gray) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = IraqRed,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                            focusedTextColor = IraqWhite,
                            unfocusedTextColor = IraqWhite
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Preset chips
                    val presets = listOf(
                        "Fajr • الفجر",
                        "Morning • صباح الخير",
                        "Work • عمل",
                        "University • جامعة",
                        "Medicine • دواء",
                        "Baghdad Flight • طيران",
                        "Dhuhr • الظهر"
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        presets.forEach { preset ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (label == preset) IraqRed.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.08f),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { label = preset }
                            ) {
                                Text(
                                    text = preset,
                                    fontSize = 11.sp,
                                    color = IraqWhite,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sound, Vibration & Challenge Settings
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = IraqCardDark)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "TONE & FEATURES",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = IraqGold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Sound Tone selection
                    Text("Alarm Sound Tone", fontSize = 13.sp, color = IraqWhite.copy(alpha = 0.8f))
                    Spacer(modifier = Modifier.height(6.dp))

                    SoundPlayer.AVAILABLE_TONES.forEach { tone ->
                        val isSelected = soundTone == tone
                        val isPreviewing = previewingTone == tone

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) IraqGreen.copy(alpha = 0.2f) else Color.Transparent)
                                .clickable { soundTone = tone }
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MusicNote,
                                    contentDescription = null,
                                    tint = if (isSelected) IraqGreen else IraqWhite.copy(alpha = 0.4f),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = tone,
                                    color = if (isSelected) IraqWhite else IraqWhite.copy(alpha = 0.8f),
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }

                            IconButton(
                                onClick = { onPreviewTone(tone) },
                                modifier = Modifier.size(28.dp).testTag("preview_tone_$tone")
                            ) {
                                Icon(
                                    imageVector = if (isPreviewing) Icons.Default.Stop else Icons.Default.PlayArrow,
                                    contentDescription = "Preview",
                                    tint = if (isPreviewing) IraqRed else IraqGold,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Vibration switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Vibration,
                                contentDescription = null,
                                tint = IraqGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Text("Vibrate on Ring", color = IraqWhite, fontSize = 14.sp)
                        }
                        Switch(
                            checked = vibrate,
                            onCheckedChange = { vibrate = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = IraqWhite,
                                checkedTrackColor = IraqGreen
                            ),
                            modifier = Modifier.testTag("vibrate_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Math Challenge switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Psychology,
                                    contentDescription = null,
                                    tint = IraqRed,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text("Wake-Up Math Challenge", color = IraqWhite, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Text(
                                "Solve a quick math quiz to turn off alarm",
                                fontSize = 11.sp,
                                color = IraqWhite.copy(alpha = 0.6f)
                            )
                        }
                        Switch(
                            checked = challengeRequired,
                            onCheckedChange = { challengeRequired = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = IraqWhite,
                                checkedTrackColor = IraqRed
                            ),
                            modifier = Modifier.testTag("challenge_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Snooze interval
                    Text("Snooze Duration", fontSize = 13.sp, color = IraqWhite.copy(alpha = 0.8f))
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(5, 10, 15).forEach { min ->
                            val isSelected = snoozeMinutes == min
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) IraqGreen else Color.White.copy(alpha = 0.08f),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { snoozeMinutes = min }
                            ) {
                                Text(
                                    text = "$min Minutes",
                                    color = IraqWhite,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
