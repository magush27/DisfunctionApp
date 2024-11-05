
package com.mhss.app.mybrain.presentation.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.mhss.app.mybrain.R
import com.mhss.app.mybrain.presentation.main.components.SpaceRegularCard
import com.mhss.app.mybrain.presentation.main.components.SpaceWideCard
import com.mhss.app.mybrain.presentation.util.Screen
import com.mhss.app.mybrain.ui.theme.*
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment


@Composable
fun SpacesScreen(
    navController: NavHostController
) {
    var menuExpanded by remember { mutableStateOf(false) }  // For managing menu state
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "DisfuncionApp",
                        style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold),
                        color = Black
                    )
                },
                actions = {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Menu",
                            tint = Black

                        )
                    }
                    // Dropdown menu for the three-dot menu
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(onClick = {
                            navController.navigate(Screen.DashboardScreen.route)
                            menuExpanded = false
                        }) {
                            Text(stringResource(R.string.dashboard))
                        }
                        DropdownMenuItem(onClick = {
                            navController.navigate(Screen.SettingsScreen.route)
                            menuExpanded = false
                        }) {
                            Text(stringResource(R.string.settings))
                        }
                    }
                },
                backgroundColor = Green,
                elevation = 0.dp
            )
        }
    ) { paddingValues ->
        LazyColumn(contentPadding = paddingValues, verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            item { Spacer(Modifier.height(60.dp)) }
            item {
                Row {
                    SpaceRegularCard(
                        modifier = Modifier.weight(1f, fill = true),
                        title = stringResource(R.string.notes),
                        image = R.drawable.note_disfunction_img,
                        backgroundColor = Black
                    ){
                        navController.navigate(Screen.NotesScreen.route)
                    }
                    SpaceRegularCard(
                        modifier = Modifier.weight(1f, fill = true),
                        title = stringResource(R.string.tasks),
                        image = R.drawable.tasks_disfunction_img,
                        backgroundColor = Black
                    ){
                        navController.navigate(
                            Screen.TasksScreen.route
                        )
                    }
                }
            }
            item {
                Row {
                    SpaceRegularCard(
                        modifier = Modifier.weight(1f, fill = true),
                        title = stringResource(R.string.timer),
                        image = R.drawable.timer_disfunction_img,
                        backgroundColor = Black
                    ){
                        navController.navigate(Screen.TimerScreen.route)
                    }
                    SpaceRegularCard(
                        modifier = Modifier.weight(1f, fill = true),
                        title = stringResource(R.string.calendar),
                        image = R.drawable.calendar_disfunction_img,
                        backgroundColor = Black
                    ){
                        navController.navigate(Screen.CalendarScreen.route)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun SpacesScreenPreview() {
    SpacesScreen(
        navController = rememberNavController()
    )
}