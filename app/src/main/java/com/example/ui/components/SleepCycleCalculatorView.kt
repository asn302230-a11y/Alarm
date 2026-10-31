package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.AddAlarm
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
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

data class SleepCycleOption(
    val cycles: Int,
    val totalHours: Double,
    val hour: Int,
    val minute: Int,
    val quality: String,
    val isRecommended: Boolean = false
)

@Composable
fun SleepCycleCalculatorView(
    currentTimeMillis: Long,
    onSetAlarm: (AlarmItem) -> Unit,
    modifier: Modifier = Modifier
) {
    // Average human takes ~14 minutes to fall asleep
    val fallAsleepTimeMs = 14 * 60 * 1000L
    val baseTime = currentTimeMillis + fallAsleepTimeMs

    val cycleOptions = listOf(
        3 to ("Short Rest (4.5h)" to false),
        4 to ("Good Energy (6.0h)" to false),
        5 to ("Optimal Sleep (7.5h) - Suggested" to true),
        6 to ("Full Recovery (9.0h)" to false)
    ).map { (cycles, info) ->
        val durationMs = cycles * 90 * 60 * 1000L
        val wakeTarget = baseTime + durationMs
        val cal = Calendar.getInstance().apply { timeInMillis = wakeTarget }
        SleepCycleOption(
            cycles = cycles,
            totalHours = cycles * 1.5,
            hour = cal.get(Calendar.HOUR_OF_DAY),
            minute = cal.get(Calendar.MINUTE),
            quality = info.first,
            isRecommended = info.second
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = IraqCardDark)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(IraqRed.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bedtime,
                                contentDescription = null,
                                tint = IraqGold,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "SLEEP CYCLE ADVISOR",
                                color = IraqGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Wake up refreshed between 90m cycles",
                                color = IraqWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "If you fall asleep right now, human sleep rhythms naturally complete every 90 minutes. Waking up in between cycles prevents grogginess.",
                        color = IraqWhite.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        item {
            Text(
                text = "RECOMMENDED WAKE TIMES • أوقات الاستيقاظ",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = IraqWhite.copy(alpha = 0.9f),
                letterSpacing = 1.sp
            )
        }

        items(cycleOptions) { option ->
            val displayH = if (option.hour > 12) option.hour - 12 else if (option.hour == 0) 12 else option.hour
            val amPm = if (option.hour >= 12) "PM" else "AM"
            val timeString = String.format("%02d:%02d", displayH, option.minute)

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (option.isRecommended) IraqCardDark else IraqSurfaceDark
                ),
                border = if (option.isRecommended) androidx.compose.foundation.BorderStroke(1.5.dp, IraqGreen) else null
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = timeString,
                                color = IraqWhite,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = amPm,
                                color = IraqGold,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "${option.cycles} Cycles (${option.totalHours} hrs sleep)",
                            color = IraqWhite.copy(alpha = 0.8f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )

                        if (option.isRecommended) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = IraqGold, modifier = Modifier.size(13.dp))
                                Text("Most Popular & Restorative", color = IraqGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Button(
                        onClick = {
                            val alarm = AlarmItem(
                                hour = option.hour,
                                minute = option.minute,
                                label = "Sleep Cycle (${option.cycles} cycles)",
                                isEnabled = true,
                                soundTone = "Mesopotamian Dawn"
                            )
                            onSetAlarm(alarm)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (option.isRecommended) IraqGreen else IraqRed.copy(alpha = 0.35f),
                            contentColor = IraqWhite
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("set_cycle_alarm_${option.cycles}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.AddAlarm, contentDescription = null, modifier = Modifier.size(14.dp))
                            Text("Set", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
