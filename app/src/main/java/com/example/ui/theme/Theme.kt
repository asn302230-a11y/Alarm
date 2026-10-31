package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Iraq Flag Dark Theme (Default bold aesthetic: Black background, Red & Green accents, White text)
val IraqDarkColorScheme = darkColorScheme(
    primary = IraqRed,
    onPrimary = IraqWhite,
    primaryContainer = IraqRedDark,
    onPrimaryContainer = IraqWhite,
    secondary = IraqGreen,
    onSecondary = IraqWhite,
    secondaryContainer = IraqGreenDark,
    onSecondaryContainer = IraqWhite,
    tertiary = IraqGold,
    onTertiary = IraqBlack,
    background = IraqBlackDark,
    onBackground = IraqTextPrimary,
    surface = IraqSurfaceDark,
    onSurface = IraqTextPrimary,
    surfaceVariant = IraqCardDark,
    onSurfaceVariant = IraqTextSecondary,
    outline = IraqRed.copy(alpha = 0.35f),
    outlineVariant = IraqGreen.copy(alpha = 0.3f),
    error = IraqRedLight,
    onError = IraqWhite
)

// Iraq Flag Light Theme (White background, Red headers, Black details, Green action accents)
val IraqLightColorScheme = lightColorScheme(
    primary = IraqRed,
    onPrimary = IraqWhite,
    primaryContainer = IraqRedLight.copy(alpha = 0.2f),
    onPrimaryContainer = IraqRedDark,
    secondary = IraqGreen,
    onSecondary = IraqWhite,
    secondaryContainer = IraqGreen.copy(alpha = 0.15f),
    onSecondaryContainer = IraqGreenDark,
    tertiary = IraqGold,
    onTertiary = IraqBlack,
    background = IraqOffWhite,
    onBackground = IraqTextDarkPrimary,
    surface = IraqWhite,
    onSurface = IraqTextDarkPrimary,
    surfaceVariant = IraqSurfaceLight,
    onSurfaceVariant = IraqTextDarkSecondary,
    outline = IraqRed.copy(alpha = 0.3f),
    outlineVariant = IraqGreen.copy(alpha = 0.3f),
    error = IraqRed,
    onError = IraqWhite
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) IraqDarkColorScheme else IraqLightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = IraqRed.toArgb()
                window.navigationBarColor = (if (darkTheme) IraqBlackDark else IraqBlack).toArgb()
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = false
                insetsController.isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
