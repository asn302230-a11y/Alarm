package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.IraqBlack
import com.example.ui.theme.IraqGreen
import com.example.ui.theme.IraqRed
import com.example.ui.theme.IraqWhite

@Composable
fun IraqFlagStripe(
    modifier: Modifier = Modifier,
    height: Dp = 6.dp,
    cornerRadius: Dp = 3.dp
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(cornerRadius))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(IraqRed)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(IraqWhite)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(IraqBlack)
        )
    }
}

@Composable
fun IraqFlagBadge(
    modifier: Modifier = Modifier,
    width: Dp = 48.dp,
    height: Dp = 32.dp
) {
    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(4.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(modifier = Modifier.matchParentSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(IraqRed)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(IraqWhite),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "الله أكبر",
                    color = IraqGreen,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(IraqBlack)
            )
        }
    }
}

@Composable
fun IraqFlagCardBorder(
    modifier: Modifier = Modifier,
    width: Dp = 5.dp
) {
    Column(
        modifier = modifier
            .width(width)
            .clip(RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(IraqRed)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.2f)
                .background(IraqGreen) // Green accent representing Takbir
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(IraqBlack)
        )
    }
}
