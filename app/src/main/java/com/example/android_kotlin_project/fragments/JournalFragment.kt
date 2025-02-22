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

class JournalFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_journal, container, false)

        val addNoteButton: CardView = view.findViewById(R.id.addNoteButton)
        val notesContainer: GridLayout = view.findViewById(R.id.notesContainer)

        notesContainer.columnCount = 3

        addNoteButton.setOnClickListener {
            val dialog = FragmentDialogAddNote()
            dialog.show(parentFragmentManager, "FragmentDialogAddNote")
        }

        fetchNotes(notesContainer, inflater)
        return view
    }

    private fun fetchNotes(container: GridLayout, inflater: LayoutInflater) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        db.collection("notes")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                container.removeAllViews()

                if (documents.isEmpty) {
                    Log.d("Firestore", "Aucune note trouvée pour cet utilisateur.")
                } else {
                    for (document in documents) {
                        val noteTitle = document.getString("title") ?: "Untitled"
                        val noteId = document.getString("noteId") ?: ""

                        Log.d("Firestore", "Note trouvée: $noteTitle ($noteId)")

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
                            Log.d("Firestore", "Bouton Voir Plus cliqué pour la note: $noteTitle")
                            // Logique a mettre en place
                        }
                        container.addView(noteView)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Erreur lors de la récupération des notes", exception)
            }
    }
}
