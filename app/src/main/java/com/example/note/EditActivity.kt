package com.example.note

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

class EditActivity : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var contentEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        titleEditText = findViewById(R.id.UpdateEditText)
        contentEditText = findViewById(R.id.UpdatecontentEditText)

        val noteId = intent.getStringExtra("noteId")
        if (noteId != null) {
            FirebaseFirestore.getInstance().collection("notes").document(noteId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val title = document.getString("title")
                        val content = document.getString("content")
                        titleEditText.setText(title)
                        contentEditText.setText(content)
                    } else {
                        Toast.makeText(this, "Note not found", Toast.LENGTH_SHORT).show()
                        finish() // Kembali ke Activity sebelumnya jika note tidak ditemukan
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error fetching note: ${e.message}", Toast.LENGTH_SHORT).show()
                    finish() // Kembali ke Activity sebelumnya jika terjadi error
                }
        } else {
            Toast.makeText(this, "NoteId not found", Toast.LENGTH_SHORT).show()
            finish() // Kembali ke Activity sebelumnya jika noteId tidak ditemukan
        }

        findViewById<Button>(R.id.saveButton).setOnClickListener {
            val newTitle = titleEditText.text.toString().trim()
            val newContent = contentEditText.text.toString().trim()

            if (newTitle.isEmpty() || newContent.isEmpty()) {
                Toast.makeText(this, "Title and content cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (noteId != null) {
                FirebaseFirestore.getInstance().collection("notes").document(noteId)
                    .update("title", newTitle, "content", newContent)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Note updated successfully", Toast.LENGTH_SHORT).show()
                        finish() // Kembali ke Activity sebelumnya setelah update berhasil
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error updating note: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }

    }
}