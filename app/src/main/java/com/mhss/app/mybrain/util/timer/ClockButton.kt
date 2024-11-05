package com.mhss.app.mybrain.util.timer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mhss.app.mybrain.ui.theme.Black
import com.mhss.app.mybrain.ui.theme.Green
import androidx.compose.material.Icon
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter


@Composable
fun ClockButton(
    contentDescription: String,
    painter: Painter,
    onClick: () -> Unit,
    color: Color = MaterialTheme.colors.primary,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Green,
            disabledBackgroundColor = color.copy(alpha = 0.1f),
        ),
        contentPadding = PaddingValues(
            start = 40.dp,
            top = 12.dp,
            end = 40.dp,
            bottom = 12.dp,
        ),
        enabled = enabled,
    ) {
       Icon(
           painter = painter,
           contentDescription = contentDescription,
           tint = Black,
           modifier = Modifier.size(30.dp),
       )
    }
}