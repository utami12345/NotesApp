package com.example.notesutami.model

import kotlinx.coroutines.flow.Flow

class NoteRepository(private val noteDao: NoteDao) {

    // Ambil semua catatan (aktif & sampah) → biasanya untuk ViewModel filter manual
    val notes: Flow<List<Note>> = noteDao.getAllNotes()

    // Ambil hanya catatan di sampah (opsional)
    val trashNotes: Flow<List<Note>> = noteDao.getTrashedNotes()

    // Tambah catatan baru
    suspend fun insert(note: Note) = noteDao.insert(note)

    // Update catatan (untuk pindah ke sampah / pulihkan)
    suspend fun update(note: Note) = noteDao.update(note)

    // Hapus permanen
    suspend fun delete(note: Note) = noteDao.delete(note)
}
