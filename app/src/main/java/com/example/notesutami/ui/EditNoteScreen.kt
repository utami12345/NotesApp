package com.example.notesutami.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notesutami.model.Note

@Composable
fun EditNoteScreen(
    note: Note,
    onSave: (String, String) -> Unit,
    onCancel: () -> Unit
) {
    var title by remember { mutableStateOf(note.title) }
    var content by remember { mutableStateOf(note.content) }

    val titleUndoStack = remember { mutableStateListOf<String>() }
    val titleRedoStack = remember { mutableStateListOf<String>() }

    val contentUndoStack = remember { mutableStateListOf<String>() }
    val contentRedoStack = remember { mutableStateListOf<String>() }

    LaunchedEffect(Unit) {
        titleUndoStack.add(title)
        contentUndoStack.add(content)
    }

    fun updateTitle(newTitle: String) {
        if (newTitle != title) {
            titleUndoStack.add(newTitle)
            title = newTitle
            titleRedoStack.clear()
        }
    }

    fun updateContent(newContent: String) {
        if (newContent != content) {
            contentUndoStack.add(newContent)
            content = newContent
            contentRedoStack.clear()
        }
    }

    fun undoTitle() {
        if (titleUndoStack.size > 1) {
            val current = titleUndoStack.removeAt(titleUndoStack.size - 1)
            titleRedoStack.add(current)
            title = titleUndoStack.last()
        }
    }

    fun redoTitle() {
        if (titleRedoStack.isNotEmpty()) {
            val redoValue = titleRedoStack.removeAt(titleRedoStack.size - 1)
            titleUndoStack.add(redoValue)
            title = redoValue
        }
    }

    fun undoContent() {
        if (contentUndoStack.size > 1) {
            val current = contentUndoStack.removeAt(contentUndoStack.size - 1)
            contentRedoStack.add(current)
            content = contentUndoStack.last()
        }
    }

    fun redoContent() {
        if (contentRedoStack.isNotEmpty()) {
            val redoValue = contentRedoStack.removeAt(contentRedoStack.size - 1)
            contentUndoStack.add(redoValue)
            content = redoValue
        }
    }

    val lineHeight = 24f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Tombol close di kiri
            IconButton(onClick = { onCancel() }) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Batal")
            }

            // Tombol undo, redo, simpan di kanan
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = {
                        undoTitle()
                        undoContent()
                    },
                    enabled = titleUndoStack.size > 1 || contentUndoStack.size > 1
                ) {
                    Icon(imageVector = Icons.Default.Undo, contentDescription = "Undo")
                }

                IconButton(
                    onClick = {
                        redoTitle()
                        redoContent()
                    },
                    enabled = titleRedoStack.isNotEmpty() || contentRedoStack.isNotEmpty()
                ) {
                    Icon(imageVector = Icons.Default.Redo, contentDescription = "Redo")
                }

                IconButton(onClick = {
                    if (title.isNotBlank() && content.isNotBlank()) {
                        onSave(title, content)
                    }
                }) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = "Simpan")
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        BasicTextField(
            value = title,
            onValueChange = { updateTitle(it) },
            textStyle = TextStyle.Default.copy(
                fontSize = 20.sp,
                lineHeight = lineHeight.sp,
                color = MaterialTheme.colorScheme.onBackground // Warna teks ikut tema
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .drawBehind {
                    drawLine(
                        color = Color.LightGray,
                        start = Offset(0f, size.height - 1f),
                        end = Offset(size.width, size.height - 1f),
                        strokeWidth = 1f
                    )
                }
                .height(50.dp),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            decorationBox = { innerTextField ->
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 4.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (title.isBlank()) {
                        Text(
                            "Judul",
                            color = Color.Gray // placeholder bisa pakai warna abu-abu standar
                        )
                    }
                    innerTextField()
                }
            }
        )

        BasicTextField(
            value = content,
            onValueChange = { updateContent(it) },
            textStyle = TextStyle.Default.copy(
                fontSize = 16.sp,
                lineHeight = lineHeight.sp,
                color = MaterialTheme.colorScheme.onBackground // Warna teks ikut tema
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp)
                .drawBehind {
                    drawLine(
                        color = Color.LightGray,
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        strokeWidth = 1f
                    )
                    drawLine(
                        color = Color.LightGray,
                        start = Offset(0f, size.height - 1f),
                        end = Offset(size.width, size.height - 1f),
                        strokeWidth = 1f
                    )
                },
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            decorationBox = { innerTextField ->
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 4.dp),
                    contentAlignment = Alignment.TopStart
                ) {
                    if (content.isBlank()) {
                        Text(
                            "Tulis isi catatan di sini...",
                            color = Color.Gray // placeholder tetap abu-abu
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}
