package com.mhss.app.mybrain.util.timer
//
//import android.Manifest
//import android.app.AlarmManager
//import android.content.Context
//import android.content.Intent
//import android.net.Uri
//import android.os.Build
//import android.provider.Settings
//import androidx.annotation.RequiresApi
//import androidx.compose.material.Text
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Star
//import androidx.compose.material.icons.filled.Notifications
//import androidx.compose.material.AlertDialog
//import androidx.compose.material.Icon
//import androidx.compose.material.MaterialTheme
//import androidx.compose.material.TextButton
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.window.DialogProperties
//import com.mhss.app.mybrain.R
//import com.google.accompanist.permissions.ExperimentalPermissionsApi
//import com.google.accompanist.permissions.isGranted
//import com.google.accompanist.permissions.rememberPermissionState
//import com.google.accompanist.permissions.shouldShowRationale
//import kotlinx.coroutines.launch
//
//
//
//@RequiresApi(Build.VERSION_CODES.S)
//@Composable
//fun CheckExactAlarmPermission() {
//    val context = LocalContext.current
//    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//    val openDialog = remember { mutableStateOf(true) }
//    if (!alarmManager.canScheduleExactAlarms() && openDialog.value) {
//        AlertDialog(
//            properties = DialogProperties(
//                dismissOnClickOutside = false,
//                dismissOnBackPress = false,
//            ),
//            icon = { Icon(Icons.Filled.Star, contentDescription = null) },
//            title = {
//                Text(
//                    text = stringResource(id = R.string.timer),
//                    style = MaterialTheme.typography.h3,
//                )
//            },
//            text = {
//                Text(
//                    text = stringResource(id = R.string.timer),
//                    style = MaterialTheme.typography.h3,
//                )
//            },
//            confirmButton = {
//                TextButton(
//                    onClick = {
//                        val alarmPermissionIntent = Intent(
//                            Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
//                            Uri.parse("package:com.example.clock"),
//                        )
//                        context.startActivity(alarmPermissionIntent)
//                        openDialog.value = false
//                    },
//                ) {
//                    Text(stringResource(id = R.string.okay))
//                }
//            },
//            onDismissRequest = {},
//        )
//    }
//}
import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.AlertDialog
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogProperties
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch
import com.mhss.app.mybrain.R

// Request Notification Permission (for Android TIRAMISU and above)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestNotificationPermission() {
    val permissionState = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)

    if (!permissionState.hasPermission) {
        if (permissionState.shouldShowRationale) {
            AlertDialog(
                properties = DialogProperties(
                    dismissOnClickOutside = false,
                    dismissOnBackPress = false,
                ),
               // icon = { Icon(Icons.Filled.Notifications, contentDescription = null) },
                title = {
                    Text(
                        text = stringResource(id = R.string.about),
                        style = MaterialTheme.typography.h1,
                    )
                },
                text = {
                    Text(
                        text = stringResource(id = R.string.about),
                        style = MaterialTheme.typography.h2,
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = { permissionState.launchPermissionRequest() },
                    ) {
                        Text(stringResource(id = R.string.content))
                    }
                },
                onDismissRequest = {},
            )
        } else {
            LaunchedEffect(Unit) {
                permissionState.launchPermissionRequest()
            }
        }
    }
}

// Request Exact Alarm Permission (for Android S and above)
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun CheckExactAlarmPermission() {
    val context = LocalContext.current
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val openDialog = remember { mutableStateOf(true) }

    if (!alarmManager.canScheduleExactAlarms() && openDialog.value) {
        // Custom dialog to ask for exact alarm permission
        CustomAlertDialog(
            title = stringResource(id = R.string.timer),
            text = stringResource(id = R.string.timer),
            confirmButtonText = stringResource(id = R.string.okay),
            //icon = Icons.Filled.Star,
            onConfirm = {
                val intent = Intent(
                    Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                    Uri.parse("package:${context.packageName}")
                )
                context.startActivity(intent)
                openDialog.value = false
            }
        )
    }
}

// Custom AlertDialog used for both notifications and alarm permission requests
@Composable
fun CustomAlertDialog(
    title: String,
    text: String,
    confirmButtonText: String,
    //icon: ImageVector,
    onConfirm: () -> Unit
) {
    AlertDialog(
        properties = DialogProperties(
            dismissOnClickOutside = false,
            dismissOnBackPress = false
        ),
      //  icon = { Icon(icon, contentDescription = null) },
        title = { Text(text = title, style = MaterialTheme.typography.h3) },
        text = { Text(text = text, style = MaterialTheme.typography.body1) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(confirmButtonText)
            }
        },
        onDismissRequest = {} // Keep empty to prevent the dialog from being dismissed accidentally
    )
}