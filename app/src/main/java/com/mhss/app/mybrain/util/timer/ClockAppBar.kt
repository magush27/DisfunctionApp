package com.mhss.app.mybrain.util.timer

import android.util.Log
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mhss.app.mybrain.R
import com.mhss.app.mybrain.presentation.util.Screen
import com.mhss.app.mybrain.ui.theme.Black
import com.mhss.app.mybrain.ui.theme.Green
import com.mhss.app.mybrain.ui.theme.MyBrainTheme

@Composable
fun ClockAppBar(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    navController: NavHostController
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.timer),
                color = Black,
                style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold)
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                navController.popBackStack()
            }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Black
                )
            }
        },
        actions = {
            var expanded by remember { mutableStateOf(false) }
            IconButton(onClick = { expanded = true }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,  // 3-dot icon
                    contentDescription = "More options",
                    tint = Black
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(onClick = {
                    expanded = false
                    try {
                        navController.navigate(Screen.DashboardScreen.route)
                    } catch (e: Exception) {
                        Log.e("NavigationError", "Error navigating to Dashboard: ${e.message}")
                    }
                }) {
                    Text(text = stringResource(R.string.dashboard))
                }

                DropdownMenuItem(onClick = {
                    expanded = false
                    try {
                        navController.navigate(Screen.SettingsScreen.route)
                    } catch (e: Exception) {
                        Log.e("NavigationError", "Error navigating to Settings: ${e.message}")
                    }
                }) {
                    Text(text = stringResource(R.string.settings))
                }
            }
        },
        backgroundColor = Green,
        elevation = 0.dp,
    )
}
//
//@Preview
//@Composable
//private fun ClockAppBarPreview() {
//    MyBrainTheme {
//        ClockAppBar(title = { Text("Alarm") })
//    }
//}
//
//@Preview
//@Composable
//private fun ClockAppBarPreviewDark() {
//    MyBrainTheme(darkTheme = true) {
//        ClockAppBar(title = { Text("Alarm") })
//    }
//}