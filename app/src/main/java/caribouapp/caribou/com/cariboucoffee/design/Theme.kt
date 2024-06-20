package caribouapp.caribou.com.cariboucoffee.design

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import caribouapp.caribou.com.cariboucoffee.R

@Composable
private fun lightColorScheme() = lightColors(
    primary = colorResource(id = R.color.primaryColor),
    onPrimary = colorResource(id = R.color.toolbarControlColor),
    secondary = colorResource(id = R.color.buttonActionNeededColor),
    onSecondary = Color.White,
    onSurface = colorResource(id = R.color.textSecondaryColor),
    background = colorResource(id = R.color.grayBackgroundMenu),
)

@Composable
fun BagelBrandsTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colors = lightColorScheme(),
        typography = Typography,
        shapes = Shapes(small = RoundedCornerShape(20.dp)),
        content = content,
    )
}
