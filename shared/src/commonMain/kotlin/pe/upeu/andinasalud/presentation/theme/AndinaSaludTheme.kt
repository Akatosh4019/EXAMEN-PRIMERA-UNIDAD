package pe.upeu.andinasalud.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AzulAndino = Color(0xFF205A91)
private val Turquesa = Color(0xFF087D83)
private val FondoClaro = Color(0xFFF4F8FB)

private val Claro = lightColorScheme(
    primary = AzulAndino,
    onPrimary = Color.White,
    secondary = Turquesa,
    onSecondary = Color.White,
    background = FondoClaro,
    onBackground = Color(0xFF172B3B),
    surface = Color.White,
    onSurface = Color(0xFF172B3B),
    surfaceVariant = Color(0xFFE4EDF3),
    onSurfaceVariant = Color(0xFF485F6E),
    error = Color(0xFFB3261E),
)

private val Oscuro = darkColorScheme(
    primary = Color(0xFFA4C9F4),
    onPrimary = Color(0xFF073454),
    secondary = Color(0xFF7DD4D3),
    onSecondary = Color(0xFF003738),
    background = Color(0xFF101C25),
    onBackground = Color(0xFFE1EAF0),
    surface = Color(0xFF192A35),
    onSurface = Color(0xFFE1EAF0),
    surfaceVariant = Color(0xFF2D414D),
    onSurfaceVariant = Color(0xFFB6C8D2),
    error = Color(0xFFFFB4AB),
)

@Composable
fun AndinaSaludTheme(oscuro: Boolean, contenido: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (oscuro) Oscuro else Claro, content = contenido)
}
