import com.example.note.Note
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.note.R

class NoteAdapter(

    private val context: Context,  // Menerima context untuk akses ke resource
    private var noteList: ArrayList<Note>  // Menyimpan daftar catatan

) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(context).inflate(
            R.layout.item_note,
            parent,
            false
        )
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = noteList[position]
        holder.titleTextView.text = note.title
        holder.contentTextView.text = note.content
    }

    override fun getItemCount(): Int {
        return noteList.size
    }


    fun addNote(note: Note) {
        noteList.add(note)
        notifyItemInserted(noteList.size - 1)  // Memberi tahu adapter untuk menambahkan item
    }

    fun removeNote(note: Note) {
        val position = noteList.indexOf(note)
        noteList.remove(note)
        notifyItemRemoved(position)  // Memberi tahu adapter untuk menghapus item
    }

    fun searchDataList(searchList: ArrayList<Note>) {
        noteList.clear()
        noteList.addAll(searchList)
        notifyDataSetChanged()  // Memberi tahu adapter untuk memperbarui tampilan
    }

    class NoteViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {

        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)
    }
}
