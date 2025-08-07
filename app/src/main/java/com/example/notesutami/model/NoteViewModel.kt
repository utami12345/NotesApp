package com.example.notesutami.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private val noteDao = NoteDatabase.getDatabase(application).noteDao()
    private val repository = NoteRepository(noteDao)

    // Semua catatan aktif (bukan sampah), tanpa fitur favorit
    val notes: StateFlow<List<Note>> = repository.notes
        .map { notes ->
            notes.filter { !it.isTrashed }
                .sortedByDescending { it.timestamp }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Semua catatan di sampah
    val trashNotes: StateFlow<List<Note>> = repository.trashNotes
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addNote(title: String, content: String) {
        viewModelScope.launch {
            val note = Note(
                title = title,
                content = content,
                timestamp = System.currentTimeMillis()
            )
            repository.insert(note)
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.delete(note)
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            repository.update(note)
        }
    }

    fun moveToTrash(note: Note) {
        viewModelScope.launch {
            val trashedNote = note.copy(isTrashed = true)
            repository.update(trashedNote)
        }
    }

    fun restoreNote(note: Note) {
        viewModelScope.launch {
            val restoredNote = note.copy(isTrashed = false)
            repository.update(restoredNote)
        }
    }

    fun deleteNotePermanently(note: Note) {
        viewModelScope.launch {
            repository.delete(note)
        }
    }
}
