package com.example.android_kotlin_project.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.android_kotlin_project.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.appcompat.widget.SearchView

class JournalFragment : Fragment() {

    private var allNotes = mutableListOf<Map<String, String>>()
    private lateinit var notesContainer: GridLayout
    private lateinit var inflater: LayoutInflater
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_journal, container, false)

        this.inflater = inflater
        notesContainer = view.findViewById(R.id.notesContainer)
        val addNoteButton: CardView = view.findViewById(R.id.addNoteButton)
        val searchView: SearchView = view.findViewById(R.id.searchView)

        notesContainer.columnCount = 3

        addNoteButton.setOnClickListener {
            val dialog = FragmentDialogAddNote()
            dialog.show(parentFragmentManager, "FragmentDialogAddNote")
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterNotes(newText)
                return true
            }
        })

        // Utiliser un listener en temps réel pour mettre à jour l'affichage automatiquement
        fetchNotes(notesContainer)
        listenForNoteChanges()  // Ecouter les changements en temps réel

        return view
    }

    private fun fetchNotes(container: GridLayout) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        db.collection("notes")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                container.removeAllViews()
                allNotes.clear()

                if (documents.isEmpty) {
                    Log.d("Firestore", "Aucune note trouvée pour cet utilisateur.")
                } else {
                    for (document in documents) {
                        val noteTitle = document.getString("title") ?: "Untitled"
                        val noteId = document.getString("noteId") ?: ""
                        val noteDescription = document.getString("description") ?: ""
                        val noteDate = document.getString("date") ?: ""

                        val noteData = mapOf("title" to noteTitle, "noteId" to noteId)
                        allNotes.add(noteData)

                        val noteView = inflater.inflate(R.layout.fragment_note_card, container, false)
                        val titleTextView: TextView = noteView.findViewById(R.id.noteTitle)
                        val seeMoreButton: TextView = noteView.findViewById(R.id.seeMoreButton)

                        val layoutParams = GridLayout.LayoutParams().apply {
                            width = 0
                            height = GridLayout.LayoutParams.WRAP_CONTENT
                            columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                            marginEnd = resources.getDimensionPixelSize(R.dimen.note_card_margin)
                            bottomMargin = resources.getDimensionPixelSize(R.dimen.note_card_margin)
                        }
                        noteView.layoutParams = layoutParams

                        titleTextView.text = noteTitle
                        seeMoreButton.setOnClickListener {
                            val editDialog = FragmentDialogEditNote.newInstance(
                                noteId, noteDate, noteTitle, noteDescription
                            )
                            editDialog.show(parentFragmentManager, "FragmentDialogEditNote")
                        }
                        container.addView(noteView)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Erreur lors de la récupération des notes", exception)
            }
    }

    private fun listenForNoteChanges() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        db.collection("notes")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { documents, exception ->
                if (exception != null) {
                    Log.e("Firestore", "Erreur lors de l'écoute des notes", exception)
                    return@addSnapshotListener
                }

                documents?.let {
                    fetchNotes(notesContainer)
                }
            }
    }

    private fun filterNotes(query: String?) {
        val filteredNotes = allNotes.filter { note ->
            val title = note["title"] ?: ""
            query?.let {
                title.contains(it, ignoreCase = true)
            } ?: true
        }
        updateNotesDisplay(filteredNotes)
    }

    private fun updateNotesDisplay(filteredNotes: List<Map<String, String>>) {
        notesContainer.removeAllViews()

        filteredNotes.forEach { note ->
            val noteTitle = note["title"] ?: "Untitled"

            val noteView = inflater.inflate(R.layout.fragment_note_card, notesContainer, false)
            val titleTextView: TextView = noteView.findViewById(R.id.noteTitle)
            val seeMoreButton: TextView = noteView.findViewById(R.id.seeMoreButton)

            val layoutParams = GridLayout.LayoutParams().apply {
                width = 0
                height = GridLayout.LayoutParams.WRAP_CONTENT
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                marginEnd = resources.getDimensionPixelSize(R.dimen.note_card_margin)
                bottomMargin = resources.getDimensionPixelSize(R.dimen.note_card_margin)
            }
            noteView.layoutParams = layoutParams

            titleTextView.text = noteTitle
            seeMoreButton.setOnClickListener {
                val editDialog = FragmentDialogEditNote.newInstance(
                    note["noteId"] ?: "", "", noteTitle, ""
                )
                editDialog.show(parentFragmentManager, "FragmentDialogEditNote")
            }
            notesContainer.addView(noteView)
        }
    }
}
