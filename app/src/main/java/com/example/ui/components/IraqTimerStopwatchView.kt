package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.IraqBlack
import com.example.ui.theme.IraqCardDark
import com.example.ui.theme.IraqGold
import com.example.ui.theme.IraqGreen
import com.example.ui.theme.IraqRed
import com.example.ui.theme.IraqSurfaceDark
import com.example.ui.theme.IraqWhite
import com.example.util.SoundPlayer
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun IraqTimerStopwatchView(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isTimerMode by remember { mutableStateOf(false) }

    // Stopwatch State
    var stopwatchRunning by remember { mutableStateOf(false) }
    var stopwatchElapsedMs by remember { mutableLongStateOf(0L) }
    val laps = remember { mutableStateListOf<Long>() }

    LaunchedEffect(stopwatchRunning) {
        if (stopwatchRunning) {
            val startTime = System.currentTimeMillis() - stopwatchElapsedMs
            while (isActive && stopwatchRunning) {
                stopwatchElapsedMs = System.currentTimeMillis() - startTime
                delay(33) // ~30 fps
            }
        }
    }

    // Timer State
    var timerRunning by remember { mutableStateOf(false) }
    var timerInitialSeconds by remember { mutableIntStateOf(240) } // 4 min default
    var timerRemainingSeconds by remember { mutableIntStateOf(240) }
    var timerFinished by remember { mutableStateOf(false) }

    LaunchedEffect(timerRunning, timerRemainingSeconds) {
        if (timerRunning && timerRemainingSeconds > 0) {
            delay(1000)
            if (timerRemainingSeconds > 1) {
                timerRemainingSeconds--
            } else {
                timerRemainingSeconds = 0
                timerRunning = false
                timerFinished = true
                SoundPlayer.playTone(context, "Mesopotamian Dawn", loop = false)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Mode Switcher (Stopwatch vs Timer)
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(IraqCardDark)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (!isTimerMode) IraqRed else Color.Transparent)
                    .clickable { isTimerMode = false }
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.Timer, contentDescription = null, tint = IraqWhite, modifier = Modifier.size(16.dp))
                    Text(
                        "Stopwatch",
                        color = IraqWhite,
                        fontSize = 13.sp,
                        fontWeight = if (!isTimerMode) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isTimerMode) IraqGreen else Color.Transparent)
                    .clickable { isTimerMode = true }
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.HourglassBottom, contentDescription = null, tint = IraqWhite, modifier = Modifier.size(16.dp))
                    Text(
                        "Timer",
                        color = IraqWhite,
                        fontSize = 13.sp,
                        fontWeight = if (isTimerMode) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (!isTimerMode) {
            // STOPWATCH
            val minutes = (stopwatchElapsedMs / 60000) % 60
            val seconds = (stopwatchElapsedMs / 1000) % 60
            val millis100 = (stopwatchElapsedMs % 1000) / 10

            // Circular Dial with Iraqi Flag colors
            Box(
                modifier = Modifier.size(240.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 10.dp.toPx()
                    // Red arc (top 120 deg)
                    drawArc(
                        color = IraqRed,
                        startAngle = 210f,
                        sweepAngle = 120f,
                        useCenter = false,
                        style = Stroke(strokeWidth, cap = StrokeCap.Round)
                    )
                    // White arc (right 120 deg)
                    drawArc(
                        color = IraqWhite,
                        startAngle = 330f,
                        sweepAngle = 120f,
                        useCenter = false,
                        style = Stroke(strokeWidth, cap = StrokeCap.Round)
                    )
                    // Green arc (Takbir accent on bottom 120 deg)
                    drawArc(
                        color = IraqGreen,
                        startAngle = 90f,
                        sweepAngle = 120f,
                        useCenter = false,
                        style = Stroke(strokeWidth, cap = StrokeCap.Round)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = String.format("%02d:%02d", minutes, seconds),
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Black,
                        color = IraqWhite
                    )
                    Text(
                        text = String.format(".%02d", millis100),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = IraqGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Stopwatch Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        stopwatchRunning = false
                        stopwatchElapsedMs = 0L
                        laps.clear()
                    },
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.1f))
                        .testTag("reset_stopwatch")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = IraqWhite)
                }

                Spacer(modifier = Modifier.width(20.dp))

                Button(
                    onClick = { stopwatchRunning = !stopwatchRunning },
                    modifier = Modifier
                        .height(60.dp)
                        .width(140.dp)
                        .testTag("start_stopwatch"),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (stopwatchRunning) IraqRed else IraqGreen,
                        contentColor = IraqWhite
                    )
                ) {
                    Icon(
                        imageVector = if (stopwatchRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (stopwatchRunning) "Pause" else "Start", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.width(20.dp))

                IconButton(
                    onClick = {
                        if (stopwatchElapsedMs > 0) {
                            laps.add(0, stopwatchElapsedMs)
                        }
                    },
                    enabled = stopwatchRunning,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(if (stopwatchRunning) IraqGold.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f))
                        .testTag("lap_stopwatch")
                ) {
                    Icon(Icons.Default.Flag, contentDescription = "Lap", tint = if (stopwatchRunning) IraqGold else Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Lap List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                itemsIndexed(laps) { index, lapMs ->
                    val lapMin = (lapMs / 60000) % 60
                    val lapSec = (lapMs / 1000) % 60
                    val lapM100 = (lapMs % 1000) / 10
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = IraqCardDark)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Lap ${laps.size - index}", color = IraqGold, fontWeight = FontWeight.SemiBold)
                            Text(
                                String.format("%02d:%02d.%02d", lapMin, lapSec, lapM100),
                                color = IraqWhite,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        } else {
            // TIMER
            val timerMin = timerRemainingSeconds / 60
            val timerSec = timerRemainingSeconds % 60
            val progress = if (timerInitialSeconds > 0) {
                timerRemainingSeconds.toFloat() / timerInitialSeconds.toFloat()
            } else 0f

            Box(
                modifier = Modifier.size(230.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val stroke = 12.dp.toPx()
                    // Background track
                    drawCircle(color = IraqCardDark, style = Stroke(stroke))
                    // Progress arc in Iraq Green
                    drawArc(
                        color = if (timerFinished) IraqGold else IraqGreen,
                        startAngle = -90f,
                        sweepAngle = 360f * progress,
                        useCenter = false,
                        style = Stroke(stroke, cap = StrokeCap.Round)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = String.format("%02d:%02d", timerMin, timerSec),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Black,
                        color = IraqWhite
                    )
                    if (timerFinished) {
                        Text("TIME'S UP!", color = IraqGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Quick Presets
            val presets = listOf(
                "Tea (شاي 4m)" to 240,
                "Power Nap (15m)" to 900,
                "Focus (25m)" to 1500,
                "Break (5m)" to 300
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                presets.forEach { (title, secs) ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (timerInitialSeconds == secs) IraqRed.copy(alpha = 0.35f) else IraqCardDark,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                timerRunning = false
                                timerFinished = false
                                timerInitialSeconds = secs
                                timerRemainingSeconds = secs
                            }
                    ) {
                        Text(
                            text = title,
                            fontSize = 11.sp,
                            color = IraqWhite,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(vertical = 8.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Timer Controls
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        timerRunning = false
                        timerFinished = false
                        timerRemainingSeconds = timerInitialSeconds
                    },
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.1f))
                        .testTag("reset_timer")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = IraqWhite)
                }

                Button(
                    onClick = {
                        timerFinished = false
                        timerRunning = !timerRunning
                    },
                    modifier = Modifier
                        .height(56.dp)
                        .width(160.dp)
                        .testTag("start_timer"),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (timerRunning) IraqRed else IraqGreen,
                        contentColor = IraqWhite
                    )
                ) {
                    Icon(
                        imageVector = if (timerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (timerRunning) "Pause" else "Start", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}
