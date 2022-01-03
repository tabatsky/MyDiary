package jatx.mydiary

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import jatx.mydiary.domain.models.Entry
import jatx.mydiary.domain.models.formatTimeList
import jatx.mydiary.domain.models.formatTimeTop
import jatx.mydiary.viewmodel.MainViewModel

@ExperimentalGraphicsApi
@ExperimentalFoundationApi
@Composable
fun MainScreen(mainViewModel: MainViewModel = viewModel()) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val W = this.maxWidth
        val H = this.maxHeight

        val A = W / 6

        val invalidateCounter by mainViewModel.invalidateCounter.collectAsState()

        val entries by mainViewModel.entries.collectAsState()
        Log.e("entries", entries.map { it.formatTimeList() }.toString())

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
                            type = typeFirst)
                        ItemTop(
                            modifier = modifier,
                            entry = pair.second,
                            type = typeSecond)
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
                        val entry = Entry(
                            type = type,
                            time = System.currentTimeMillis()
                        )
                        mainViewModel.insertEntry(entry)
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
@Composable
private fun ItemTop(modifier: Modifier, entry: Entry?, type: Int) {
    val timeStr = entry?.formatTimeTop() ?: "никогда"

    Box(
        modifier = modifier
            .background(Color.Black)
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(getColorByType(type))
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

private fun List<Entry?>.mapToPairs(): List<Pair<Entry?, Entry?>> {
    val arrayList = arrayListOf<Pair<Entry?, Entry?>>()
    for (i in 0 until size / 2) {
        arrayList.add(this[2*i] to this[2*i+1])
    }
    if (size % 2 == 1) {
        arrayList.add(last() to null)
    }
    return arrayList
}