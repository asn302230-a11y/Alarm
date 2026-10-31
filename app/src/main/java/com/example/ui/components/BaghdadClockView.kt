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
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Brightness5
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.WbSunny
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class IraqiCity(
    val nameEn: String,
    val nameAr: String,
    val subtitle: String,
    val timeZoneId: String = "Asia/Baghdad"
)

data class PrayerEstimate(
    val nameEn: String,
    val nameAr: String,
    val hour: Int,
    val minute: Int,
    val isPast: Boolean = false
)

@Composable
fun BaghdadClockView(
    currentTimeMillis: Long,
    onAddPrayerAlarm: (AlarmItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val baghdadTz = TimeZone.getTimeZone("Asia/Baghdad")
    val cal = Calendar.getInstance(baghdadTz).apply { timeInMillis = currentTimeMillis }
    val hour24 = cal.get(Calendar.HOUR_OF_DAY)
    val minute = cal.get(Calendar.MINUTE)
    val second = cal.get(Calendar.SECOND)

    val timeFmt = SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).apply { timeZone = baghdadTz }
    val dateFmt = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault()).apply { timeZone = baghdadTz }
    val timeString = timeFmt.format(Date(currentTimeMillis))
    val dateString = dateFmt.format(Date(currentTimeMillis))

    val localTz = TimeZone.getDefault()
    val diffHours = (baghdadTz.getOffset(currentTimeMillis) - localTz.getOffset(currentTimeMillis)) / (1000 * 60 * 60)

    val iraqiCities = listOf(
        IraqiCity("Baghdad", "بغداد", "The Capital • العاصمة"),
        IraqiCity("Basra", "البصرة", "Fayhaa of the South • الفيحاء"),
        IraqiCity("Erbil", "أربيل", "The Ancient Citadel • القلعة"),
        IraqiCity("Najaf", "النجف الأشرف", "City of Wisdom"),
        IraqiCity("Karbala", "كربلاء المقدسة", "Sacred Heritage"),
        IraqiCity("Mosul", "الموصل", "Umm al-Rabi'ayn • أم الربيعين")
    )

    // Standard prayer estimation for Baghdad
    val prayerTimes = listOf(
        PrayerEstimate("Fajr", "الفجر", 5, 0, hour24 > 5 || (hour24 == 5 && minute > 0)),
        PrayerEstimate("Sunrise", "الشروق", 6, 25, hour24 > 6 || (hour24 == 6 && minute > 25)),
        PrayerEstimate("Dhuhr", "الظهر", 12, 10, hour24 > 12 || (hour24 == 12 && minute > 10)),
        PrayerEstimate("Asr", "العصر", 15, 20, hour24 > 15 || (hour24 == 15 && minute > 20)),
        PrayerEstimate("Maghrib", "المغرب", 17, 35, hour24 > 17 || (hour24 == 17 && minute > 35)),
        PrayerEstimate("Isha", "العشاء", 19, 0, hour24 > 19 || (hour24 == 19 && minute > 0))
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Baghdad Main Clock Hero Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = IraqCardDark)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IraqFlagBadge(width = 36.dp, height = 24.dp)
                        Text(
                            text = "BAGHDAD TIME (GMT+3)",
                            color = IraqGold,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = timeString,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Black,
                        color = IraqWhite
                    )

                    Text(
                        text = dateString,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = IraqWhite.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.08f)
                    ) {
                        Text(
                            text = if (diffHours == 0) "Same as your local time" else "Baghdad is ${if (diffHours > 0) "+$diffHours hrs ahead" else "$diffHours hrs behind"} your device",
                            fontSize = 12.sp,
                            color = IraqGreen,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Baghdad Prayers Section with 1-click alarm setting
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = IraqCardDark)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "BAGHDAD PRAYER TIMINGS",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = IraqGold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Tap bell to schedule alarm",
                                fontSize = 11.sp,
                                color = IraqWhite.copy(alpha = 0.6f)
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.WbSunny,
                            contentDescription = null,
                            tint = IraqGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    prayerTimes.forEach { prayer ->
                        val displayHour = if (prayer.hour > 12) prayer.hour - 12 else prayer.hour
                        val amPm = if (prayer.hour < 12) "AM" else "PM"
                        val timeFormatted = String.format("%02d:%02d %s", displayHour, prayer.minute, amPm)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (prayer.isPast) Color.Transparent else Color.White.copy(alpha = 0.04f))
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "${prayer.nameEn} • ${prayer.nameAr}",
                                    color = if (prayer.isPast) IraqWhite.copy(alpha = 0.4f) else IraqWhite,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = timeFormatted,
                                    color = if (prayer.isPast) IraqWhite.copy(alpha = 0.3f) else IraqGreen,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Button(
                                onClick = {
                                    val alarm = AlarmItem(
                                        hour = prayer.hour,
                                        minute = prayer.minute,
                                        label = "${prayer.nameEn} Prayer • ${prayer.nameAr}",
                                        isEnabled = true,
                                        daysOfWeek = listOf(
                                            Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY,
                                            Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY
                                        ),
                                        soundTone = "Mesopotamian Dawn"
                                    )
                                    onAddPrayerAlarm(alarm)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = IraqRed.copy(alpha = 0.25f),
                                    contentColor = IraqWhite
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("set_prayer_alarm_${prayer.nameEn}")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.AddAlert, contentDescription = null, modifier = Modifier.size(14.dp), tint = IraqGold)
                                    Text("Set Alarm", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Iraqi Cities List
        item {
            Text(
                text = "IRAQI CITIES • مدن العراق",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = IraqWhite.copy(alpha = 0.9f),
                letterSpacing = 1.sp
            )
        }

        items(iraqiCities) { city ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = IraqSurfaceDark)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(IraqRed.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationCity,
                                contentDescription = null,
                                tint = IraqRed,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "${city.nameEn} • ${city.nameAr}",
                                color = IraqWhite,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = city.subtitle,
                                color = IraqWhite.copy(alpha = 0.5f),
                                fontSize = 12.sp
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = String.format("%02d:%02d", if (hour24 > 12) hour24 - 12 else if (hour24 == 0) 12 else hour24, minute),
                            color = IraqWhite,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = if (hour24 >= 12) "PM" else "AM",
                            color = IraqGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
