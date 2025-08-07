package com.example.notesutami.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddChecklistScreen(
    onSave: (String, String) -> Unit,
    onCancel: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var items by remember { mutableStateOf(listOf(ChecklistItem(""))) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Baris atas: tombol batal & simpan
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onCancel) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                }
                IconButton(
                    onClick = {
                        val filteredItems = items.filter { it.text.isNotBlank() }
                        if (title.isNotBlank() && filteredItems.isNotEmpty()) {
                            val content = "checklist:" + filteredItems.joinToString("|||") {
                                "${it.text}::${it.isChecked}"
                            }
                            onSave(title, content)
                        }
                    }
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Simpan")
                }
            }

            // Input judul
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Judul Catatan") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // Daftar checklist
            items.forEachIndexed { index, item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DragHandle,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )

                    Checkbox(
                        checked = item.isChecked,
                        onCheckedChange = { checked ->
                            items = items.toMutableList().also {
                                it[index] = it[index].copy(isChecked = checked)
                            }
                        }
                    )

                    OutlinedTextField(
                        value = item.text,
                        onValueChange = { newText ->
                            items = items.toMutableList().also {
                                it[index] = it[index].copy(text = newText)
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        textStyle = if (item.isChecked)
                            TextStyle(
                                color = Color.Gray,
                                textDecoration = TextDecoration.LineThrough,
                                fontSize = 16.sp
                            )
                        else
                            TextStyle(fontSize = 16.sp),
                        placeholder = { Text("Item ${index + 1}") }
                    )

                    IconButton(onClick = {
                        items = items.toMutableList().also { it.removeAt(index) }
                    }) {
                        Icon(Icons.Default.Clear, contentDescription = "Hapus Item")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = {
                    items = items + ChecklistItem("")
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Tambah")
            }
        }
    }
}
