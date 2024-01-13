package com.example.note

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import com.google.firebase.firestore.FirebaseFirestore
import android.view.View
import androidx.appcompat.widget.Toolbar

class NoteActivity : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var contentEditText: EditText
    private lateinit var saveButton: View
    private var noteId: String? = null
    private lateinit var firestore: FirebaseFirestore

    override fun onSupportNavigateUp(): Boolean {
        finish() // Tutup NoteActivity ketika tombol back ditekan
        return super.onSupportNavigateUp()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)

        // Set toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        titleEditText = findViewById(R.id.titleEditText)
        contentEditText = findViewById(R.id.contentEditText)
        saveButton = findViewById(R.id.saveButton) // Ganti R.id.saveButton dengan ID button Anda yang sebenarnya

        firestore = FirebaseFirestore.getInstance() // Inisialisasi Firestore

        val intent = intent
        noteId = intent.getStringExtra("noteId")

        if (noteId != null) {
            // Edit catatan yang ada
            firestore.collection("notes").document(noteId!!).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val title = document.getString("title")
                        val content = document.getString("content")
                        titleEditText.setText(title)
                        contentEditText.setText(content)
                    }
                }
        }

        saveButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val content = contentEditText.text.toString()

            if (noteId == null) {
                // Tambah catatan baru
                firestore.collection("notes").add(
                    mapOf( // Menggunakan mapOf untuk membuat Map di Kotlin
                        "title" to title,
                        "content" to content
                    )
                ).addOnSuccessListener { documentReference ->
                    val noteId = documentReference.id
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("noteId", noteId)
                    startActivity(intent)
                    finish()
                }
            } else {
                // Update catatan yang ada
                firestore.collection("notes").document(noteId!!).update(
                    mapOf( // Menggunakan mapOf untuk membuat Map di Kotlin
                        "title" to title,
                        "content" to content
                    )
                ).addOnSuccessListener {
                    finish()
                }
            }
        }

    }
}
