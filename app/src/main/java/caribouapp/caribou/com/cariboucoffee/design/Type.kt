package caribouapp.caribou.com.cariboucoffee.design

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import caribouapp.caribou.com.cariboucoffee.R

private val defaultFontFamily = FontFamily(
    Font(R.font.primary_font_family, FontWeight.Bold),
)

private val toolbarFontFamily = FontFamily(
    Font(R.font.toolbar_title_font, FontWeight.Bold),
)

private val secondaryFontFamily = FontFamily(
    Font(R.font.heading_secondary_font_family, FontWeight.Bold)
)

private val captionFontFamily = FontFamily(
    Font(R.font.description_font_family, FontWeight.Normal)
)

val Typography = Typography(
    defaultFontFamily = defaultFontFamily,
    h4 = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp,
        letterSpacing = 0.25.sp,
        fontFamily = secondaryFontFamily,
    ),
    h6 = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        letterSpacing = 0.15.sp,
        fontFamily = toolbarFontFamily,
    ),
    body1 = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 19.sp,
        letterSpacing = 0.5.sp
    ),
    caption = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        letterSpacing = 0.4.sp,
        fontFamily = captionFontFamily,
    )
)
