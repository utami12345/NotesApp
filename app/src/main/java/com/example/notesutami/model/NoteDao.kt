package com.example.notesutami.model

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Insert
    suspend fun insert(note: Note)

    @Update
    suspend fun update(note: Note)

    @Delete
    suspend fun delete(note: Note)

    // Ambil semua catatan (termasuk yang di-trash)
    @Query("SELECT * FROM notes")
    fun getAllNotes(): Flow<List<Note>>

    // Ambil catatan aktif (tidak di-trash), tanpa urutan favorit karena kolom dihapus
    @Query("SELECT * FROM notes WHERE isTrashed = 0 ORDER BY timestamp DESC")
    fun getActiveNotes(): Flow<List<Note>>

    // Ambil catatan yang sudah dihapus (di trash)
    @Query("SELECT * FROM notes WHERE isTrashed = 1 ORDER BY timestamp DESC")
    fun getTrashedNotes(): Flow<List<Note>>
}
