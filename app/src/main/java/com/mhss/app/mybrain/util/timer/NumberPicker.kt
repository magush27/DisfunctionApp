package com.mhss.app.mybrain.util.timer

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.platform.LocalFocusManager
import com.mhss.app.mybrain.R

@Composable
fun NumberPicker(
    modifier: Modifier = Modifier,
    number: TextFieldValue,
    timeUnit: String,
    onNumberChange: (TextFieldValue) -> Unit,
    textStyle: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.h3,
    backgroundColor: Color = Color.Black,
) {
    val numericKeyboard = KeyboardOptions(keyboardType = KeyboardType.Number)
    val textFieldColors = TextFieldDefaults.textFieldColors(
        backgroundColor = backgroundColor,
        focusedIndicatorColor = backgroundColor,
        unfocusedIndicatorColor = backgroundColor,
        disabledIndicatorColor = backgroundColor,
    )

    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(isFocused) {
        onNumberChange(
            number.copy(
                selection = if (isFocused) {
                    TextRange(
                        start = 0,
                        end = number.text.length,
                    )
                } else {
                    number.selection
                },
            ),
        )
    }

    Surface(modifier = modifier) {
        TextField(
            label = {
                Text(
                    text = timeUnit,
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(start = dimensionResource(id = R.dimen._5sdp)),
                )
            },
            modifier = Modifier
                .onFocusChanged { focusState ->
                    if (!focusState.isFocused && number.text.isEmpty()) {
                        onNumberChange(TextFieldValue("00"))
                    } else if (!focusState.isFocused && number.text.length == 1) {
                        onNumberChange(TextFieldValue(number.text.padStart(2, '0')))
                    }
                },
            value = number,
            onValueChange = { onNumberChange(it) },
            textStyle = textStyle,
            keyboardOptions = numericKeyboard,
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                }
            ),
            colors = textFieldColors,
            interactionSource = interactionSource,
        )

    }
}