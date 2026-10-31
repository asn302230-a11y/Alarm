package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.IraqBlack
import com.example.ui.theme.IraqCardDark
import com.example.ui.theme.IraqGold
import com.example.ui.theme.IraqGreen
import com.example.ui.theme.IraqRed
import com.example.ui.theme.IraqSurfaceDark
import com.example.ui.theme.IraqWhite

@Composable
fun AboutIraqAlarmDialog(
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = IraqSurfaceDark)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with Flag badge & Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IraqFlagBadge(width = 44.dp, height = 28.dp)

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp).testTag("close_about_dialog")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = IraqWhite.copy(alpha = 0.8f))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Iraq Alarm • منبه العراق",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = IraqWhite
                )

                Text(
                    text = "Styled with the authentic Flag of Iraq",
                    fontSize = 12.sp,
                    color = IraqGold,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(16.dp))

                // The Flag Colors Explanation
                val colorDetails = listOf(
                    Triple(IraqRed, "Red (أحمر)", "Courage, nobility, and bravery of the Iraqi people."),
                    Triple(IraqWhite, "White (أبيض)", "Peace, purity, generosity, and luminous Mesopotamian heritage."),
                    Triple(IraqGreen, "Green (أخضر)", "The sacred Takbir 'الله أكبر' in authentic Kufic script."),
                    Triple(IraqBlack, "Black (أسود)", "Overcoming oppression and the fertile black soil of the Euphrates and Tigris.")
                )

                colorDetails.forEach { (color, title, desc) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(color)
                        )
                        Column {
                            Text(text = title, color = IraqWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(text = desc, color = IraqWhite.copy(alpha = 0.7f), fontSize = 11.sp, lineHeight = 15.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("dismiss_about_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = IraqGreen,
                        contentColor = IraqWhite
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Done / تم", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
