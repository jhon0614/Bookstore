package com.example.movil.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val DarkColorScheme = darkColorScheme(
    primary = InkBlueDark,
    onPrimary = Color(0xFF0B3555),
    primaryContainer = Color(0xFF294C69),
    onPrimaryContainer = Color(0xFFD3E8FF),
    secondary = BookGoldDark,
    secondaryContainer = Color(0xFF614713),
    tertiary = SageDark,
    background = Night,
    onBackground = NightInk,
    surface = NightSurface,
    onSurface = NightInk,
    surfaceVariant = NightVariant,
    outline = Color(0xFF89929C)
)

private val LightColorScheme = lightColorScheme(
    primary = InkBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD4E7FA),
    onPrimaryContainer = Color(0xFF0A263D),
    secondary = BookGold,
    secondaryContainer = Color(0xFFFFDEA3),
    tertiary = Sage,
    background = Paper,
    onBackground = Ink,
    surface = PaperSurface,
    onSurface = Ink,
    surfaceVariant = PaperVariant,
    outline = Color(0xFF77736D)
)

private val BookstoreShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp)
)

@Composable
fun MovilTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        shapes = BookstoreShapes,
        content = content
    )
}
