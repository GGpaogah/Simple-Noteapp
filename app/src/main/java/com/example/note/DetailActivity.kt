package com.example.note

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.note.databinding.ActivityDetailBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    private lateinit var fabmenu: FloatingActionButton
    private lateinit var fabdelete: FloatingActionButton
    private lateinit var fabedit: FloatingActionButton

    private lateinit var delete_action: TextView
    private lateinit var edit_action: TextView

    private var isAllfabVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = intent.extras
        if(bundle != null) {
            binding.titleTextView.text = bundle.getString("title")
            binding.contentTextView.text = bundle.getString("content")
        }

        // untuk menu fab
        fabmenu = findViewById(R.id.fabmenu)

        // untuk sub menu fab
        fabdelete = findViewById(R.id.fabdelete)
        fabedit   = findViewById(R.id.fabedit)

        // text dari fab
        delete_action= findViewById(R.id.delete_action)
        edit_action  = findViewById(R.id.edit_action)

        // ubah agar text dan fab hilang
        fabdelete.visibility = View.GONE
        fabedit.visibility   = View.GONE
        delete_action.visibility= View.GONE
        edit_action.visibility= View.GONE


        fabmenu.setOnClickListener {
            isAllfabVisible = !isAllfabVisible // Change visibility state first
            fabdelete.toggle()
            fabedit.toggle()
            delete_action.toggle()
            edit_action.toggle()
        }

        fabdelete.setOnClickListener {
            val noteId = intent.getStringExtra("noteId") ?: return@setOnClickListener
            FirebaseFirestore.getInstance().collection("notes").document(noteId)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "Note deleted successfully", Toast.LENGTH_SHORT).show()
                    finish() // Return to MainActivity
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error deleting note: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        fabedit.setOnClickListener {
            val noteId = intent.getStringExtra("noteId") ?: return@setOnClickListener
            val intent = Intent(this, EditActivity::class.java)
            intent.putExtra("noteId", noteId)
            startActivity(intent)
        }
    }
}

private fun TextView.toggle() {
    visibility = if (visibility == View.VISIBLE) {
        View.GONE // toggle text menjadi menghilang
    } else {
        View.VISIBLE // toggle text menjadi muncul
    }
}

private fun FloatingActionButton.toggle() {
    if (visibility == View.VISIBLE) {
        hide()
    } else {
        show()
    }
}
