package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AlarmItem
import com.example.ui.theme.IraqBlack
import com.example.ui.theme.IraqGold
import com.example.ui.theme.IraqGreen
import com.example.ui.theme.IraqRed
import com.example.ui.theme.IraqWhite
import kotlin.random.Random

@Composable
fun AlarmRingingOverlay(
    alarm: AlarmItem,
    onDismiss: () -> Unit,
    onSnooze: (Int) -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_transition")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Math challenge state if required
    var num1 by remember { mutableIntStateOf(Random.nextInt(12, 38)) }
    var num2 by remember { mutableIntStateOf(Random.nextInt(8, 29)) }
    var correctAnswer by remember { mutableIntStateOf(num1 + num2) }
    var challengeSolved by remember { mutableStateOf(!alarm.challengeRequired) }
    var challengeError by remember { mutableStateOf(false) }

    val options = remember(num1, num2) {
        val wrong1 = correctAnswer + if (Random.nextBoolean()) Random.nextInt(1, 5) else -Random.nextInt(1, 5)
        val wrong2 = correctAnswer + if (Random.nextBoolean()) Random.nextInt(6, 12) else -Random.nextInt(6, 12)
        val wrong3 = correctAnswer + 10
        listOf(correctAnswer, wrong1, wrong2, wrong3).shuffled()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        IraqRed,
                        Color(0xFF8A0010),
                        IraqBlack
                    )
                )
            )
            .testTag("alarm_ringing_overlay"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Section: Flag banner & Title
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 28.dp)
            ) {
                IraqFlagBadge(width = 64.dp, height = 40.dp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "الله أكبر",
                    color = IraqGreen,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "IRAQ ALARM IS RINGING",
                    color = IraqWhite.copy(alpha = 0.85f),
                    fontSize = 13.sp,
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Center: Big Time & Pulsing Bell
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(IraqGreen.copy(alpha = 0.35f)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(IraqGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.NotificationsActive,
                            contentDescription = "Ringing Bell",
                            tint = IraqWhite,
                            modifier = Modifier.size(46.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = alarm.formatted12HourTime,
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Black,
                    color = IraqWhite
                )

                Text(
                    text = alarm.amPm,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = IraqGold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = alarm.label.ifBlank { "Wake Up!" },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = IraqWhite
                )
            }

            // Bottom Section: Challenge or Actions
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (alarm.challengeRequired && !challengeSolved) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = IraqBlack.copy(alpha = 0.85f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🧠 Wake-Up Math Challenge",
                                color = IraqGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "What is $num1 + $num2 = ?",
                                color = IraqWhite,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 22.sp
                            )

                            if (challengeError) {
                                Text(
                                    text = "Incorrect! Try again to turn off.",
                                    color = IraqRed,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                options.forEach { option ->
                                    Button(
                                        onClick = {
                                            if (option == correctAnswer) {
                                                challengeSolved = true
                                                challengeError = false
                                                onDismiss()
                                            } else {
                                                challengeError = true
                                            }
                                        },
                                        modifier = Modifier.weight(1f).testTag("option_$option"),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = IraqGreen.copy(alpha = 0.8f)
                                        )
                                    ) {
                                        Text(
                                            text = "$option",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = IraqWhite
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Action Buttons: Snooze & Dismiss
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Button(
                        onClick = { onSnooze(alarm.snoozeMinutes) },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .testTag("snooze_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = IraqBlack.copy(alpha = 0.8f),
                            contentColor = IraqGold
                        )
                    ) {
                        Icon(Icons.Default.Snooze, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Snooze (${alarm.snoozeMinutes}m)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = {
                            if (!alarm.challengeRequired || challengeSolved) {
                                onDismiss()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .testTag("dismiss_alarm_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (alarm.challengeRequired && !challengeSolved) Color.Gray else IraqGreen,
                            contentColor = IraqWhite
                        ),
                        enabled = !alarm.challengeRequired || challengeSolved
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Stop / إيقاف",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
