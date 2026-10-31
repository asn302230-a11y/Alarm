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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.IraqBlack
import com.example.ui.theme.IraqGold
import com.example.ui.theme.IraqGreen
import com.example.ui.theme.IraqRed
import com.example.ui.theme.IraqSurfaceDark
import com.example.ui.theme.IraqWhite
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun IraqFlagHeader(
    currentTimeMillis: Long,
    nextAlarmText: String?,
    onInfoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showBaghdadTime by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "bell_transition")
    val bellRotation by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bell_rotation"
    )

    // Formatted time
    val timeFormat = SimpleDateFormat("hh:mm", Locale.getDefault()).apply {
        if (showBaghdadTime) timeZone = TimeZone.getTimeZone("Asia/Baghdad")
    }
    val secFormat = SimpleDateFormat(":ss", Locale.getDefault()).apply {
        if (showBaghdadTime) timeZone = TimeZone.getTimeZone("Asia/Baghdad")
    }
    val amPmFormat = SimpleDateFormat("a", Locale.getDefault()).apply {
        if (showBaghdadTime) timeZone = TimeZone.getTimeZone("Asia/Baghdad")
    }
    val dateFormat = SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault()).apply {
        if (showBaghdadTime) timeZone = TimeZone.getTimeZone("Asia/Baghdad")
    }

    val date = Date(currentTimeMillis)
    val timeStr = timeFormat.format(date)
    val secStr = secFormat.format(date)
    val amPmStr = amPmFormat.format(date)
    val dateStr = dateFormat.format(date)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        IraqRed,
                        Color(0xFF990D1C),
                        IraqSurfaceDark
                    )
                )
            )
            .padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 18.dp)
    ) {
        // Iraqi Flag Top Accent Line
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
        ) {
            Box(Modifier.weight(1f).height(4.dp).background(IraqRed))
            Box(Modifier.weight(1f).height(4.dp).background(IraqWhite))
            Box(Modifier.weight(1f).height(4.dp).background(IraqGreen))
            Box(Modifier.weight(1f).height(4.dp).background(IraqBlack))
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Top App Bar row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                IraqFlagBadge(width = 44.dp, height = 28.dp)
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "IRAQ ALARM",
                            color = IraqWhite,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.2.sp
                        )
                    }
                    Text(
                        text = "منبه العراق • الله أكبر",
                        color = IraqGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            IconButton(
                onClick = onInfoClick,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f))
                    .testTag("info_button")
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = "About Iraq Alarm",
                    tint = IraqWhite,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Hero Clock Section
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = IraqBlack.copy(alpha = 0.75f),
            shadowElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Zone selector pill (Local vs Baghdad GMT+3)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .padding(horizontal = 4.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (!showBaghdadTime) IraqGreen else Color.Transparent)
                            .clickable { showBaghdadTime = false }
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = IraqWhite,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "Local Time",
                                color = IraqWhite,
                                fontSize = 11.sp,
                                fontWeight = if (!showBaghdadTime) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (showBaghdadTime) IraqGreen else Color.Transparent)
                            .clickable { showBaghdadTime = true }
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(
                                imageVector = Icons.Default.Public,
                                contentDescription = null,
                                tint = IraqWhite,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "Baghdad (UTC+3)",
                                color = IraqWhite,
                                fontSize = 11.sp,
                                fontWeight = if (showBaghdadTime) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Big Clock
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = timeStr,
                        fontSize = 46.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = IraqWhite,
                        letterSpacing = (-1).sp
                    )
                    Text(
                        text = secStr,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = IraqGreen,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = amPmStr,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = IraqGold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Text(
                    text = dateStr,
                    fontSize = 13.sp,
                    color = IraqWhite.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Medium
                )

                // Next Alarm Countdown banner
                AnimatedVisibility(visible = nextAlarmText != null) {
                    if (nextAlarmText != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(IraqRed.copy(alpha = 0.25f))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.NotificationsActive,
                                contentDescription = null,
                                tint = IraqGold,
                                modifier = Modifier
                                    .size(16.dp)
                                    .rotate(bellRotation)
                            )
                            Text(
                                text = nextAlarmText,
                                color = IraqWhite,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
