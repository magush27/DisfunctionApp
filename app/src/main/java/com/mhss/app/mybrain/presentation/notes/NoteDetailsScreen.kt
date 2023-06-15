package com.mhss.app.mybrain.presentation.notes

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.accompanist.flowlayout.FlowRow
import com.mhss.app.mybrain.R
import com.mhss.app.mybrain.domain.model.Note
import com.mhss.app.mybrain.domain.model.NoteFolder
import com.mhss.app.mybrain.presentation.util.Screen
import com.mhss.app.mybrain.ui.theme.Orange
import dev.jeziellago.compose.markdowntext.MarkdownText

@Composable
fun NoteDetailsScreen(
    navController: NavHostController,
    noteId: Int,
    folderId: Int,
    viewModel: NotesViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        if (noteId != -1) viewModel.onEvent(NoteEvent.GetNote(noteId))
        if (folderId != -1) viewModel.onEvent(NoteEvent.GetFolder(folderId))
    }
    val state = viewModel.notesUiState
    val scaffoldState = rememberScaffoldState()
    var openDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var openFolderDialog by rememberSaveable { mutableStateOf(false) }

    var title by rememberSaveable { mutableStateOf(state.note?.title ?: "") }
    var content by rememberSaveable { mutableStateOf(state.note?.content ?: "") }
    var pinned by rememberSaveable { mutableStateOf(state.note?.pinned ?: false) }
    val readingMode = state.readingMode
    var folder: NoteFolder? by remember { mutableStateOf(state.folder) }

    LaunchedEffect(state.note) {
        if (state.note != null) {
            title = state.note.title
            content = state.note.content
            pinned = state.note.pinned
            folder = state.folder
        }
    }
    LaunchedEffect(state) {
        if (state.navigateUp) {
            openDeleteDialog = false
            navController.popBackStack(route = Screen.NotesScreen.route, inclusive = false)
        }
        if (state.error != null) {
            scaffoldState.snackbarHostState.showSnackbar(
                state.error
            )
            viewModel.onEvent(NoteEvent.ErrorDisplayed)
        }
        if (state.folder != folder) folder = state.folder
    }
    BackHandler {
        addOrUpdateNote(
            Note(
                title = title,
                content = content,
                pinned = pinned,
                folderId = folder?.id
            ),
            state.note,
            onNotChanged = {
                navController.popBackStack(
                    route = Screen.NotesScreen.route,
                    inclusive = false
                )
            },
            onUpdate = {
                val canciones = arrayOf(
                    arrayOf("Pelotuda - Dillom", "Mi money go dumb, tengo plata pelotuda\n" +
                            "Tomé siete Rivo', creo que necesito ayuda\n" +
                            "En Argentina yo y Los Ramone' en NYC, ah\n" +
                            "La vida es triste, lo siento, pero es así\n" +
                            "Mi money go dumb, tengo plata pelotuda (pelotuda)\n" +
                            "Tomé siete Rivo', creo que necesito ayuda\n" +
                            "En Argentina yo y Los Ramone' en NYC, ah (NYC)\n" +
                            "La vida es triste, lo siento, pero es así\n" +
                            "Mi plata se mueve sola, como un poltergeist\n" +
                            "En el stage me vuelvo tonto como Frankestein (uah)\n" +
                            "Soy rubiecito y carilindo como Seven Kayne\n" +
                            "Y la pancita e' por la Heineken\n" +
                            "De mi torta todo' quieren un mordiscón\n" +
                            "Voy afuera y me tratan como si fuera un Rolling Stone\n" +
                            "Caemo' a tu casa, ding-ding-dong\n" +
                            "Mi zapatilla cuesta un Dom Peri\n" +
                            "One por el money (one), do' por el show (two)\n" +
                            "Three por mis whoadie' que están ready pa' la acción\n" +
                            "Todo lo que quiero son sneakers y weapons\n" +
                            "Money and power, y una bitchie con un septum, hmm\n" +
                            "Otra city nos espera, así que let's go, hmm\n" +
                            "Yo tengo conexiones, como redstone\n" +
                            "Hay raperos que me quieren en su Death Note, ah (Death Note)\n" +
                            "Ni en pedo me regalo, I gotta lay low, hmm\n" +
                            "Shorty se parece a J.Lo\n" +
                            "Mi plata se hizo larga, bitch, yo le digo Jake Long\n" +
                            "El Dillom una masa, como Play-Doh (Play-Doh)\n" +
                            "Los dejamos en bola', como en Playboy\n" +
                            "Mi money go dumb, tengo plata pelotuda\n" +
                            "Tomé siete Rivo', creo que necesito ayuda\n" +
                            "En Argentina yo y Los Ramone' en NYC\n" +
                            "La vida es triste, lo siento, pero es así\n" +
                            "Mi money go dumb, tengo plata pelotuda\n" +
                            "Tomé siete Rivo', creo que necesito ayuda\n" +
                            "En Argentina yo y Los Ramone' en NYC (en NYC)\n" +
                            "La vida es triste, lo siento, pero es así\n" +
                            "Mi money go dumb, tengo plata pelotuda\n" +
                            "Tomé siete Rivo', creo que necesito ayuda\n" +
                            "En Argentina yo y Los Ramone' en NYC\n" +
                            "La vida es triste, lo siento, pero es así\n" +
                            "Ah, ah\n" +
                            "POST MORTEM\n" +
                            "POST MORTEM, ah\n" +
                            "Yo no tengo sueños, tengo planes\n" +
                            "No tengo enemigo', tengo fane'\n" +
                            "Me nombran y aparezco, igual que Bloody Mary\n" +
                            "To' mi vida la persigo como Tom y Jerry\n" +
                            "La venganza es dulce como un flan con crema\n" +
                            "Y pareciera que yo pago una condena\n" +
                            "Porque pa' lo malo tengo un imán\n" +
                            "Y lo bueno siempre tarda o nunca llega\n" +
                            "Puede que a veces yo esté en crisis\n" +
                            "Y que tenga arruiná' la psiquis\n" +
                            "Pero si hay que meter mano, yo hago fisting\n" +
                            "Cuando la costa está tensa como en lifting (ah)\n" +
                            "Tengo un par de hijo', le' voy a hacer un baby shower\n" +
                            "Pa' nosotro' todo el día es happy hour\n" +
                            "Antes nadie venía a mi cumple\n" +
                            "Ahora todos quieren venir a mi cumple\n" +
                            "Cuando hablo, nadie me interrumpe\n" +
                            "Me gusta porque hace que mi mundo se derrumbe\n" +
                            "Hace un par de años que vivo en la incertidumbre, ah\n" +
                            "Espero no se me haga una costumbre")
                )
                val cancion = canciones.random()
                val nombre = cancion[0]
                val letra = cancion[1]

                if (state.note != null) {
                    viewModel.onEvent(
                        NoteEvent.UpdateNote(
                            state.note.copy(
                                title = nombre,
                                content = letra,
                                folderId = folder?.id
                            )
                        )
                    )
                } else {
                    viewModel.onEvent(
                        NoteEvent.AddNote(
                            Note(
                                title = nombre,
                                content = letra,
                                pinned = pinned,
                                folderId = folder?.id
                            )
                        )
                    )
                }

            }
        )
    }
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {
                    if (folder != null) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(25.dp))
                                .border(1.dp, Color.Gray, RoundedCornerShape(25.dp))
                                .clickable { openFolderDialog = true },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painterResource(R.drawable.ic_folder),
                                stringResource(R.string.folders),
                                modifier = Modifier.padding(start = 8.dp, top = 8.dp, bottom = 8.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = folder?.name!!,
                                modifier = Modifier.padding(end = 8.dp, top = 8.dp, bottom = 8.dp),
                                style = MaterialTheme.typography.body1
                            )
                        }
                    } else {
                        IconButton(onClick = { openFolderDialog = true }) {
                            Icon(
                                painterResource(R.drawable.ic_create_folder),
                                stringResource(R.string.folders),
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                },
                actions = {
                    if (state.note != null) IconButton(onClick = { openDeleteDialog = true }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_delete),
                            contentDescription = stringResource(R.string.delete_task)
                        )
                    }
                    IconButton(onClick = {
                        pinned = !pinned
                        if (state.note != null) {
                            viewModel.onEvent(NoteEvent.PinNote)
                        }
                    }) {
                        Icon(
                            painter = if (pinned) painterResource(id = R.drawable.ic_pin_filled)
                            else painterResource(id = R.drawable.ic_pin),
                            contentDescription = stringResource(R.string.pin_note),
                            modifier = Modifier.size(24.dp),
                            tint = Orange
                        )
                    }
                    IconButton(onClick = {
                        viewModel.onEvent(NoteEvent.ToggleReadingMode)
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_read_mode),
                            contentDescription = stringResource(R.string.reading_mode),
                            modifier = Modifier.size(24.dp),
                            tint = if (readingMode) Color.Green else Color.Gray
                        )
                    }
                },
                backgroundColor = MaterialTheme.colors.background,
                elevation = 0.dp,
            )
        },
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(text = stringResource(R.string.title)) },
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier.fillMaxWidth(),
            )
            if (readingMode)
                MarkdownText(
                    markdown = content.ifBlank { stringResource(R.string.note_content) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(vertical = 6.dp)
                        .border(1.dp, Color.Gray, RoundedCornerShape(20.dp))
                        .padding(10.dp),
                    onClick = {
                        viewModel.onEvent(NoteEvent.ToggleReadingMode)
                    }
                )
            else
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = {
                        Text(text = stringResource(R.string.note_content))
                    },
                    shape = RoundedCornerShape(15.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                )
        }
        if (openDeleteDialog)
            AlertDialog(
                shape = RoundedCornerShape(25.dp),
                onDismissRequest = { openDeleteDialog = false },
                title = { Text(stringResource(R.string.delete_note_confirmation_title)) },
                text = {
                    Text(
                        stringResource(
                            R.string.delete_note_confirmation_message,
                            state.note?.title!!
                        )
                    )
                },
                confirmButton = {
                    Button(
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                        shape = RoundedCornerShape(25.dp),
                        onClick = {
                            viewModel.onEvent(NoteEvent.DeleteNote(state.note!!))
                        },
                    ) {
                        Text(stringResource(R.string.delete_note), color = Color.White)
                    }
                },
                dismissButton = {
                    Button(
                        shape = RoundedCornerShape(25.dp),
                        onClick = {
                            openDeleteDialog = false
                        }) {
                        Text(stringResource(R.string.cancel), color = Color.White)
                    }
                }
            )
        if (openFolderDialog)
            AlertDialog(
                shape = RoundedCornerShape(25.dp),
                onDismissRequest = { openFolderDialog = false },
                title = { Text(stringResource(R.string.change_folder)) },
                text = {
                    FlowRow {
                        Row(
                            modifier = Modifier
                                .padding(4.dp)
                                .clip(RoundedCornerShape(25.dp))
                                .border(1.dp, Color.Gray, RoundedCornerShape(25.dp))
                                .clickable {
                                    folder = null
                                    openFolderDialog = false
                                }
                                .background(if (folder == null) MaterialTheme.colors.onBackground else Color.Transparent),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.none),
                                modifier = Modifier.padding(8.dp),
                                style = MaterialTheme.typography.body1,
                                color = if (folder == null) MaterialTheme.colors.background else MaterialTheme.colors.onBackground
                            )
                        }
                        state.folders.forEach {
                            Row(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .clip(RoundedCornerShape(25.dp))
                                    .border(1.dp, Color.Gray, RoundedCornerShape(25.dp))
                                    .clickable {
                                        folder = it
                                        openFolderDialog = false
                                    }
                                    .background(if (folder?.id == it.id) MaterialTheme.colors.onBackground else Color.Transparent),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painterResource(R.drawable.ic_folder),
                                    stringResource(R.string.folders),
                                    modifier = Modifier.padding(
                                        start = 8.dp,
                                        top = 8.dp,
                                        bottom = 8.dp
                                    ),
                                    tint = if (folder?.id == it.id) MaterialTheme.colors.background else MaterialTheme.colors.onBackground
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = it.name,
                                    modifier = Modifier.padding(
                                        end = 8.dp,
                                        top = 8.dp,
                                        bottom = 8.dp
                                    ),
                                    style = MaterialTheme.typography.body1,
                                    color = if (folder?.id == it.id) MaterialTheme.colors.background else MaterialTheme.colors.onBackground
                                )
                            }
                        }
                    }
                },
                buttons = {}
            )
    }
}

private fun addOrUpdateNote(
    newNote: Note,
    note: Note? = null,
    onNotChanged: () -> Unit = {},
    onUpdate: (Note) -> Unit,
) {
    if (note != null) {
        if (noteChanged(newNote, note))
            onUpdate(note)
        else
            onNotChanged()
    } else {
        onUpdate(newNote)
    }
}

private fun noteChanged(
    note: Note,
    newNote: Note
): Boolean {
    return note.title != newNote.title ||
            note.content != newNote.content ||
            note.folderId != newNote.folderId
}