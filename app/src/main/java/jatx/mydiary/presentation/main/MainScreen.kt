package jatx.mydiary.presentation.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import jatx.mydiary.R
import jatx.mydiary.domain.models.Entry
import jatx.mydiary.domain.models.formatTimeList
import jatx.mydiary.domain.models.formatTimeTop
import jatx.mydiary.navigation.Router
import jatx.mydiary.navigation.ScreenVariant

@ExperimentalGraphicsApi
@ExperimentalFoundationApi
@Composable
fun MainScreen(mainViewModel: MainViewModel = viewModel()) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val W = this.maxWidth

        val A = W / 6

        val entries by mainViewModel.entries.collectAsState()

        val topEntries = (1 .. 6)
            .toList()
            .map { type ->
                entries.firstOrNull { it.type == type }
            }

        Column(
            modifier = Modifier
                .background(Color.Black)
                .fillMaxSize()
        ) {
            TopAppBar(
                title = {
                    Text(
                        text = "MyDiary",
                        color = Color.White
                    )
                },
                actions = {
                    MainScreenActions()
                },
                backgroundColor = Color.Black
            )
            LazyColumn(
                modifier = Modifier
                    .height(A * 3)
            ) {
                itemsIndexed(topEntries.mapToPairs()) { index, pair ->
                    val typeFirst = pair.first?.type ?: (2 * index + 1)
                    val typeSecond = pair.second?.type ?: (2 * index + 2)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(A)
                    ) {
                        val modifier = Modifier
                            .weight(1.0f)
                            .height(A)
                        ItemTop(
                            modifier = modifier,
                            entry = pair.first,
                            type = typeFirst,
                            mainViewModel = mainViewModel
                        )
                        ItemTop(
                            modifier = modifier,
                            entry = pair.second,
                            type = typeSecond,
                            mainViewModel = mainViewModel
                        )
                    }
                }
            }
            LazyColumn(
                modifier = Modifier
                    .weight(1.0f)
                    .padding(top = 10.dp, bottom = 10.dp)
            ) {
                items(entries.mapToPairs()) { pair ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(A)
                    ) {
                        val modifier = Modifier
                            .weight(1.0f)
                            .height(A)
                        ItemList(
                            modifier = modifier,
                            entry = pair.first,
                            mainViewModel = mainViewModel
                        )
                        ItemList(
                            modifier = modifier,
                            entry = pair.second,
                            mainViewModel = mainViewModel
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(A)
            ) {
                val modifier = Modifier
                    .size(width = A, height = A)

                for (type in types) {
                    FooterButton(
                        modifier = modifier,
                        type = type,
                        mainViewModel = mainViewModel
                    )
                }
            }
        }

        DeleteDialog()
        DeleteByTypeDialog()
    }
}


@Composable
private fun DeleteDialog(mainViewModel: MainViewModel = viewModel()) {

    val showDeleteDialog by mainViewModel.showDeleteDialog.collectAsState()
    val entryToDelete by mainViewModel.entryToDelete.collectAsState()

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                mainViewModel.setShowDeleteDialog(false)
            },
            title = {
                Text("Удалить запись?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        mainViewModel.setShowDeleteDialog(false)
                        mainViewModel.deleteEntry()
                    },
                ) {
                    Text("Да")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        mainViewModel.setShowDeleteDialog(false)
                    },
                ) {
                    Text("Нет")
                }
            },
            text = {
                Text(entryToDelete?.formatTimeList() ?: "")
            }
        )
    }
}

@Composable
private fun DeleteByTypeDialog(mainViewModel: MainViewModel = viewModel()) {
    val showDeleteByTypeDialog by mainViewModel.showDeleteByTypeDialog.collectAsState()

    if (showDeleteByTypeDialog) {
        AlertDialog(
            onDismissRequest = {
                mainViewModel.setShowDeleteByTypeDialog(false)
            },
            title = {
                Text("Удалить все записи этого типа?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        mainViewModel.setShowDeleteByTypeDialog(false)
                        mainViewModel.deleteByType()
                    },
                ) {
                    Text("Да")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        mainViewModel.setShowDeleteByTypeDialog(false)
                    },
                ) {
                    Text("Нет")
                }
            },
            text = {}
        )
    }
}

@ExperimentalFoundationApi
@ExperimentalGraphicsApi
@Composable
private fun FooterButton(modifier: Modifier, type: Int, mainViewModel: MainViewModel) {
    Box(
        modifier = modifier
            .background(Color.Black)
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(getColorByType(type))
                .combinedClickable(
                    onClick = {
                        mainViewModel.setTypeForEntry(type)
                        mainViewModel.createEntry()
                    },
                    onLongClick = {
                        mainViewModel.setTypeForEntry(type)
                        mainViewModel.showDateTimePicker()
                    }
                )
        ) {
            Text(
                text = type.toString(),
                fontSize = 24.sp,
                color = Color.Black,
                modifier = Modifier
                    .background(Color.Transparent)
                    .align(Alignment.Center)
            )
        }
    }
}

@ExperimentalGraphicsApi
@ExperimentalFoundationApi
@Composable
private fun ItemTop(modifier: Modifier, entry: Entry?, type: Int, mainViewModel: MainViewModel) {
    val timeStr = entry?.formatTimeTop() ?: "никогда"

    val currentType by mainViewModel.currentType.collectAsState()

    val fontWeight = if (type == currentType) FontWeight.Bold else FontWeight.Normal

    Box(
        modifier = modifier
            .background(Color.Black)
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(getColorByType(type))
                .combinedClickable(
                    onClick = {
                        mainViewModel.setCurrentType(type)
                    },
                    onLongClick = {
                        mainViewModel.setTypeToDelete(type)
                        mainViewModel.setShowDeleteByTypeDialog(true)
                    }
                )
        ) {
            Text(
                text = timeStr,
                fontSize = 18.sp,
                fontWeight = fontWeight,
                color = Color.Black,
                modifier = Modifier
                    .background(Color.Transparent)
                    .align(Alignment.CenterStart)
            )
        }
    }
}

@ExperimentalGraphicsApi
@ExperimentalFoundationApi
@Composable
private fun ItemList(modifier: Modifier, entry: Entry?, mainViewModel: MainViewModel) {
    val timeStr = entry?.formatTimeList() ?: ""
    val type = entry?.type ?: 0

    Box(
        modifier = modifier
            .background(Color.Black)
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(getColorByType(type))
                .combinedClickable(
                    onClick = {},
                    onLongClick = {
                        mainViewModel.setEntryToDelete(entry)
                        mainViewModel.setShowDeleteDialog(true)
                    }
                )
        ) {
            Text(
                text = timeStr,
                fontSize = 18.sp,
                color = Color.Black,
                modifier = Modifier
                    .background(Color.Transparent)
                    .align(Alignment.CenterStart)
            )
        }
    }
}

@Composable
private fun MainScreenActions() {
    val mainViewModel: MainViewModel = viewModel()
    var expanded by remember { mutableStateOf(false) }

    IconButton(
        onClick = { expanded = !expanded }
    ) {
        Icon(painterResource(id = R.drawable.ic_question),
            "", tint = Color.White)
    }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = {
            expanded = false
        }
    ) {
        DropdownMenuItem(onClick = {
            mainViewModel.loadData()
        }) {
            Text(
                text = stringResource(id = R.string.item_load)
            )
        }
        DropdownMenuItem(onClick = {
            mainViewModel.saveData()
        }) {
            Text(
                text = stringResource(id = R.string.item_save)
            )
        }
        DropdownMenuItem(onClick = {
            Router.push(ScreenVariant.AuthScreenVariant)
        }) {
            Text(
                text = stringResource(R.string.item_auth)
            )
        }
    }
}

private fun <T> List<T>.mapToPairs(): List<Pair<T?, T?>> = chunked(2)
    .map { it[0] to it.getOrNull(1) }