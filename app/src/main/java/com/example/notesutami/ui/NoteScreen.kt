package com.example.notesutami.ui

import androidx.compose.runtime.*
import com.example.notesutami.model.Note
import com.example.notesutami.model.NoteViewModel
import kotlin.collections.joinToString

@Composable
fun NoteScreen(viewModel: NoteViewModel) {
    var currentScreen by remember { mutableStateOf(Screen.NoteList) }
    var noteToEdit by remember { mutableStateOf<Note?>(null) }

    when (currentScreen) {
        Screen.NoteList -> {
            val notes by viewModel.notes.collectAsState()

            NoteListScreen(
                notes = notes,
                onDelete = { note -> viewModel.deleteNote(note) },
                onMoveToTrash = { note -> viewModel.moveToTrash(note) },
                onAddClick = { currentScreen = Screen.AddNote },
                onAddClickChecklist = { currentScreen = Screen.AddChecklist },
                onEdit = { note ->
                    noteToEdit = note
                    currentScreen = Screen.EditNote
                },
                onEditChecklist = { note ->
                    noteToEdit = note
                    currentScreen = Screen.EditChecklist
                },
                onViewTrashClick = { currentScreen = Screen.Trash }
            )
        }

        Screen.AddNote -> {
            AddNoteScreen(
                onSave = { title, content ->
                    viewModel.addNote(title, content)
                    currentScreen = Screen.NoteList
                },
                onCancel = { currentScreen = Screen.NoteList }
            )
        }

        Screen.AddChecklist -> {
            AddChecklistScreen(
                onSave = { title, content ->
                    viewModel.addNote(title, content)
                    currentScreen = Screen.NoteList
                },
                onCancel = { currentScreen = Screen.NoteList }
            )
        }

        Screen.EditNote -> {
            noteToEdit?.let { note ->
                EditNoteScreen(
                    note = note,
                    onSave = { title, content ->
                        viewModel.updateNote(note.copy(title = title, content = content))
                        noteToEdit = null
                        currentScreen = Screen.NoteList
                    },
                    onCancel = {
                        noteToEdit = null
                        currentScreen = Screen.NoteList
                    }
                )
            } ?: run {
                currentScreen = Screen.NoteList
            }
        }

        Screen.EditChecklist -> {
            noteToEdit?.let { note ->
                val checklistItems = note.content
                    .removePrefix("checklist:")
                    .split("|||")
                    .mapNotNull {
                        val parts = it.split("::")
                        if (parts.size == 2) {
                            val text = parts[0]
                            val isChecked = parts[1].toBooleanStrictOrNull() ?: false
                            ChecklistItem(text, isChecked)
                        } else null
                    }

                EditChecklistScreen(
                    note = note,
                    initialItems = checklistItems,
                    onSave = { title, items ->
                        val content = "checklist:" + items.joinToString("|||") {
                            "${it.text}::${it.isChecked}"
                        }
                        viewModel.updateNote(note.copy(title = title, content = content))
                        noteToEdit = null
                        currentScreen = Screen.NoteList
                    },
                    onCancel = {
                        noteToEdit = null
                        currentScreen = Screen.NoteList
                    }
                )
            } ?: run {
                currentScreen = Screen.NoteList
            }
        }

        Screen.Trash -> {
            val trashNotes by viewModel.trashNotes.collectAsState()

            TrashScreen(
                trashedNotes = trashNotes,
                onRestore = { note -> viewModel.restoreNote(note) },
                onDeletePermanent = { note -> viewModel.deleteNotePermanently(note) },
                onBackClick = { currentScreen = Screen.NoteList }
            )
        }
    }
}
