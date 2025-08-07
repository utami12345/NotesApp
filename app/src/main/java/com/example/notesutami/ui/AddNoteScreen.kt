package com.example.notesutami.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AddNoteScreen(
    onSave: (String, String) -> Unit,
    onCancel: () -> Unit,
    initialTitle: String = "",
    initialContent: String = "",
    isEditMode: Boolean = false
) {
    var title by remember { mutableStateOf(initialTitle) }
    var content by remember { mutableStateOf(initialContent) }

    // Set lineHeight yang konsisten
    val lineHeight = 24f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top bar with icons
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onCancel() }) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Batal")
            }
            IconButton(onClick = {
                if (title.isNotBlank() && content.isNotBlank()) {
                    onSave(title, content)
                }
            }) {
                Icon(imageVector = Icons.Default.Check, contentDescription = "Simpan")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Judul TextField dengan garis bawah
        BasicTextField(
            value = title,
            onValueChange = { title = it },
            textStyle = TextStyle.Default.copy(fontSize = 20.sp, lineHeight = lineHeight.sp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .drawBehind {
                    // Garis di bawah teks
                    drawLine(
                        color = Color.LightGray,
                        start = androidx.compose.ui.geometry.Offset(0f, size.height - 1f),
                        end = androidx.compose.ui.geometry.Offset(size.width, size.height - 1f),
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
                        Text("Judul", color = Color.Gray)
                    }
                    innerTextField()
                }
            }
        )

        // Isi TextField dengan garis-garis buku
        BasicTextField(
            value = content,
            onValueChange = { content = it },
            textStyle = TextStyle.Default.copy(fontSize = 16.sp, lineHeight = lineHeight.sp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp)
                .drawBehind {
                    // Garis di atas dan bawah area teks
                    drawLine(
                        color = Color.LightGray,
                        start = androidx.compose.ui.geometry.Offset(0f, 0f),
                        end = androidx.compose.ui.geometry.Offset(size.width, 0f),
                        strokeWidth = 1f
                    )
                    drawLine(
                        color = Color.LightGray,
                        start = androidx.compose.ui.geometry.Offset(0f, size.height - 1f),
                        end = androidx.compose.ui.geometry.Offset(size.width, size.height - 1f),
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
                        Text("Tulis isi catatan di sini...", color = Color.Gray)
                    }
                    innerTextField()
                }
            }
        )
    }
}

