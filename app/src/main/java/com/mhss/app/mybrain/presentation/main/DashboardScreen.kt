package com.mhss.app.mybrain.presentation.main

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.mhss.app.mybrain.R
import com.mhss.app.mybrain.domain.model.CalendarEvent
import com.mhss.app.mybrain.presentation.calendar.CalendarDashboardWidget
import com.mhss.app.mybrain.presentation.diary.MoodCircularBar
import com.mhss.app.mybrain.presentation.tasks.TasksDashboardWidget
import com.mhss.app.mybrain.presentation.util.Screen
import com.mhss.app.mybrain.ui.theme.Black
import com.mhss.app.mybrain.ui.theme.Green
import com.mhss.app.mybrain.util.Constants
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun DashboardScreen(
    navController: NavHostController,
    viewModel: MainViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.dashboard),
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
                backgroundColor = Green,
                elevation = 0.dp,
            )
        }
    ) {paddingValues ->
        LaunchedEffect(true) { viewModel.onDashboardEvent(DashboardEvent.InitAll) }
        LazyColumn(contentPadding = paddingValues) {
            item {
                CalendarDashboardWidget(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.5f),
                    events = viewModel.uiState.dashBoardEvents,
                    onClick = {
                        navController.navigate(
                            Screen.CalendarScreen.route
                        )
                    },
                    onPermission = {
                        viewModel.onDashboardEvent(DashboardEvent.ReadPermissionChanged(it))
                    },
                    onAddEventClicked = {
                        navController.navigate(
                            Screen.CalendarEventDetailsScreen.route.replace(
                                "{${Constants.CALENDAR_EVENT_ARG}}",
                                " "
                            )
                        )
                    },
                    onEventClicked = {
                        val eventJson = Gson().toJson(it, CalendarEvent::class.java)
                        // encoding the string to avoid crashes when the event contains fields that equals a URL
                        val encodedJson = URLEncoder.encode(eventJson, StandardCharsets.UTF_8.toString())
                        navController.navigate(
                            Screen.CalendarEventDetailsScreen.route.replace(
                                "{${Constants.CALENDAR_EVENT_ARG}}",
                                encodedJson
                            )
                        )
                    }
                )
            }
            item {
                TasksDashboardWidget(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.5f),
                    tasks = viewModel.uiState.dashBoardTasks,
                    onCheck = {
                        viewModel.onDashboardEvent(DashboardEvent.UpdateTask(it))
                    },
                    onTaskClick = {
                        navController.navigate(
                            Screen.TaskDetailScreen.route
                                .replace(
                                    "{${Constants.TASK_ID_ARG}}",
                                    it.id.toString()
                                )
                        )
                    },
                    onAddClick = {
                        navController.navigate(
                            Screen.TasksScreen
                                .route
                                .replace(
                                    "{${Constants.ADD_TASK_ARG}}",
                                    "true"
                                )
                        )
                    },
                    onClick = {
                        navController.navigate(
                            Screen.TasksScreen.route
                        )
                    }
                )
            }
            item { Spacer(Modifier.height(65.dp)) }
        }
    }
}