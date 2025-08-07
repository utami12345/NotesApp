package com.example.notesutami.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.notesutami.model.Note
import java.text.SimpleDateFormat
import java.util.*


data class ChecklistItem(
    val text: String,
    val isChecked: Boolean = false
)

val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListScreen(
    notes: List<Note>,
    onDelete: (Note) -> Unit,
    onMoveToTrash: (Note) -> Unit,
    onAddClick: () -> Unit,
    onAddClickChecklist: () -> Unit,
    onEditChecklist: (Note) -> Unit,
    onEdit: (Note) -> Unit,
    onViewTrashClick: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedSortOption by remember { mutableStateOf("Tanggal Terbaru") }
    var fabMenuExpanded by remember { mutableStateOf(false) }

    val sortOptions = listOf("Tanggal Terbaru", "Tanggal Terlama", "Judul A-Z", "Judul Z-A")

    val sortedFilteredNotes = notes
        .filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
                    it.content.contains(searchQuery, ignoreCase = true)
        }
        .sortedWith(
            when (selectedSortOption) {
                "Tanggal Terbaru" -> compareByDescending { it.timestamp }
                "Tanggal Terlama" -> compareBy { it.timestamp }
                "Judul A-Z" -> compareBy { it.title.lowercase() }
                "Judul Z-A" -> compareByDescending { it.title.lowercase() }
                else -> compareByDescending { it.timestamp }
            }
        )

    Scaffold(
        floatingActionButton = {
            Box(modifier = Modifier.fillMaxWidth()) {
                FloatingActionButton(
                    onClick = onViewTrashClick,
                    containerColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 55.dp, bottom = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Lihat Sampah",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 16.dp, bottom = 16.dp)
                ) {
                    FloatingActionButton(
                        onClick = { fabMenuExpanded = true },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Tambah Catatan",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    DropdownMenu(
                        expanded = fabMenuExpanded,
                        onDismissRequest = { fabMenuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Catatan Biasa") },
                            onClick = {
                                fabMenuExpanded = false
                                onAddClick()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.NoteAdd, contentDescription = null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Daftar Centang") },
                            onClick = {
                                fabMenuExpanded = false
                                onAddClickChecklist()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.List, contentDescription = null)
                            }
                        )
                    }
                }
            }
        },
        content = { paddingValues ->
            val customPadding = PaddingValues(
                start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                top = 8.dp,
                end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
                bottom = paddingValues.calculateBottomPadding()
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(customPadding)
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = MaterialTheme.shapes.large,
                        modifier = Modifier.weight(1f)
                    ) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Cari catatan...") },
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = "Search")
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                unfocusedIndicatorColor = MaterialTheme.colorScheme.surface
                            ),
                            shape = MaterialTheme.shapes.large,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Box {
                        IconButton(onClick = { expanded = true }) {
                            Icon(
                                imageVector = Icons.Default.Sort,
                                contentDescription = "Urutkan",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            sortOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        selectedSortOption = option
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                LazyColumn {
                    items(sortedFilteredNotes) { note ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    if (note.content.startsWith("checklist:")) {
                                        onEditChecklist(note)
                                    } else {
                                        onEdit(note)
                                    }
                                },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = note.title, style = MaterialTheme.typography.titleMedium)

                                        if (note.content.startsWith("checklist:")) {
                                            val checklistItems = note.content.removePrefix("checklist:")
                                                .split("|||")
                                                .mapNotNull {
                                                    val parts = it.split("::")
                                                    if (parts.size == 2) {
                                                        val text = parts[0]
                                                        val isChecked = parts[1].toBooleanStrictOrNull() ?: false
                                                        ChecklistItem(text, isChecked)
                                                    } else null
                                                }

                                            val maxPreviewItems = 3
                                            val previewItems = checklistItems.take(maxPreviewItems)

                                            previewItems.forEach { item ->
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                                                ) {
                                                    Checkbox(
                                                        checked = item.isChecked,
                                                        onCheckedChange = null,
                                                        enabled = false
                                                    )
                                                    Text(
                                                        text = item.text,
                                                        style = MaterialTheme.typography.bodyMedium.copy(
                                                            textDecoration = if (item.isChecked) TextDecoration.LineThrough else TextDecoration.None,
                                                            color = if (item.isChecked) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                                            else MaterialTheme.colorScheme.onSurface
                                                        )
                                                    )
                                                }
                                            }

                                            if (checklistItems.size > maxPreviewItems) {
                                                Text(
                                                    text = "+${checklistItems.size - maxPreviewItems} item lainnya",
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    ),
                                                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                                                )
                                            }
                                        } else {
                                            Text(
                                                text = note.content,
                                                style = MaterialTheme.typography.bodyMedium,
                                                maxLines = 3
                                            )
                                        }
                                    }

                                    IconButton(onClick = { onMoveToTrash(note) }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Pindahkan ke Sampah"
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = formatter.format(Date(note.timestamp)),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}
