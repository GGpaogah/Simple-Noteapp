package com.example.note

import NoteAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.note.databinding.ActivityMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import java.util.ArrayList
import java.util.Locale
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
import com.google.ai.client.generativeai.type.content


class MainActivity : AppCompatActivity() {

    private lateinit var noteAdapter: NoteAdapter
    private lateinit var firestore: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var fab: FloatingActionButton
    private lateinit var binding: ActivityMainBinding
    private lateinit var noteList: ArrayList<Note>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        noteList = ArrayList()
        noteAdapter = NoteAdapter(this, noteList)
        firestore = FirebaseFirestore.getInstance()

        recyclerView = binding.recyclerView
        fab = binding.fab

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = noteAdapter

        // Ambil data catatan dari Firestore
        firestore.collection("notes")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("Firestore", "Error getting notes", error)
                    return@addSnapshotListener
                }

                noteList.clear()
                for (document in snapshot?.documents!!) {
                    val title = document.getString("title")!!
                    val content = document.getString("content")!!
                    noteList.add(Note(
                        document.id,
                        title,
                        content
                    ))
                }
                noteAdapter.notifyDataSetChanged()
            }

        recyclerView.addOnItemTouchListener(object : OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                // Handle item click here
                val child = rv.findChildViewUnder(e.x, e.y)
                if (child != null) {
                    val position = rv.getChildAdapterPosition(child)
                    val note = noteList[position]
                    val intent = Intent(this@MainActivity, DetailActivity::class.java)
                    intent.putExtra("noteId", note.id)
                    intent.putExtra("title", note.title)
                    intent.putExtra("content", note.content)
                    startActivity(intent)
                    return true
                }
                return false
            }

            // Other override methods for touch events (not necessary for item clicks)
            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
        })

        // Listener untuk FAB
        fab.setOnClickListener {
            val intent = Intent(this, NoteActivity::class.java)
            startActivity(intent)
        }

        // Listener untuk SearchView
        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searchList(newText)
                return true
            }
        })
    }

    fun searchList(text: String?) {
        if (text == null) {
            // Jika text null, tampilkan semua note
            noteAdapter.searchDataList(noteList)
        } else {
            // Lakukan pencarian jika text tidak null
            val searchList = ArrayList<Note>()
            for (note in noteList) {
                if (note.title?.lowercase(Locale.getDefault())?.contains(text.lowercase(Locale.getDefault())) == true) {
                    searchList.add(note)
                }
            }
            noteAdapter.searchDataList(searchList)
        }
    }
    fun onItemClick(note: Note) {
        val intent = Intent(this, DetailActivity::class.java) // Use `this` to refer to the current context
        intent.putExtra("noteId", note.id)
        startActivity(intent)
    }
}
