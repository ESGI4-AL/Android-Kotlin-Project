package com.example.android_kotlin_project.fragments

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.android_kotlin_project.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FragmentDialogAddNote : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.fragment_dialog_add_note, null)

        val noteDate = view.findViewById<EditText>(R.id.noteDate)
        val noteTitle = view.findViewById<EditText>(R.id.noteTitle)
        val noteDescription = view.findViewById<EditText>(R.id.noteDescription)
        val cancelButton = view.findViewById<Button>(R.id.cancelButton)
        val saveButton = view.findViewById<Button>(R.id.saveNoteButton)

        builder.setView(view)
        val dialog = builder.create()

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        saveButton.setOnClickListener {
            val date = noteDate.text.toString().trim()
            val title = noteTitle.text.toString().trim()
            val description = noteDescription.text.toString().trim()
            val userId = FirebaseAuth.getInstance().currentUser?.uid

            if (title.isEmpty()) {
                noteTitle.error = "Title is required"
                return@setOnClickListener
            }

            if (userId == null) {
                noteTitle.error = "User not authenticated"
                return@setOnClickListener
            }

            val db = FirebaseFirestore.getInstance()
            val newNoteRef = db.collection("notes").document()

            val note = hashMapOf(
                "noteId" to newNoteRef.id,
                "userId" to userId,
                "date" to date,
                "title" to title,
                "description" to description,
                "timestamp" to System.currentTimeMillis()
            )

            newNoteRef.set(note)
                .addOnSuccessListener {
                    val listener = activity as? OnNoteAddedListener
                    listener?.onNoteAdded(date, title, description)
                    dialog.dismiss()
                }
                .addOnFailureListener {
                    noteTitle.error = "Error saving note"
                }
        }

        return dialog
    }

    interface OnNoteAddedListener {
        fun onNoteAdded(date: String, title: String, description: String)
    }
}