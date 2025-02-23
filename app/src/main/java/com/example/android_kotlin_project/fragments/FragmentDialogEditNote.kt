package com.example.android_kotlin_project.fragments

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.android_kotlin_project.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FragmentDialogEditNote : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.fragment_seemorebutton, null)

        val noteDate = view.findViewById<EditText>(R.id.noteDate)
        val noteTitle = view.findViewById<EditText>(R.id.noteTitle)
        val noteDescription = view.findViewById<EditText>(R.id.noteDescription)
        val cancelButton = view.findViewById<Button>(R.id.cancelButton)
        val saveButton = view.findViewById<Button>(R.id.saveNoteButton)
        val deleteButton = view.findViewById<ImageButton>(R.id.deleteNoteButton)

        val noteId = arguments?.getString("noteId")
        val initialDate = arguments?.getString("date") ?: ""
        val initialTitle = arguments?.getString("title") ?: ""
        val initialDescription = arguments?.getString("description") ?: ""

        noteDate.setText(initialDate)
        noteTitle.setText(initialTitle)
        noteDescription.setText(initialDescription)

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

            if (noteId != null) {
                val db = FirebaseFirestore.getInstance()
                val noteRef = db.collection("notes").document(noteId)

                val updatedNote = hashMapOf(
                    "date" to date,
                    "title" to title,
                    "description" to description
                ) as MutableMap<String, Any>

                noteRef.update(updatedNote)
                    .addOnSuccessListener {
                        val listener = activity as? OnNoteAddedListener
                        listener?.onNoteAdded(date, title, description)
                        dialog.dismiss()
                    }
                    .addOnFailureListener {
                        noteTitle.error = "Error updating note"
                    }
            }
        }

        deleteButton.setOnClickListener {
            if (noteId != null) {
                val db = FirebaseFirestore.getInstance()
                val noteRef = db.collection("notes").document(noteId)

                noteRef.delete()
                    .addOnSuccessListener {
                        val listener = activity as? OnNoteDeletedListener
                        listener?.onNoteDeleted(noteId)
                        dialog.dismiss()
                    }
                    .addOnFailureListener {
                        noteTitle.error = "Error deleting note"
                    }
            }
        }

        return dialog
    }

    interface OnNoteAddedListener {
        fun onNoteAdded(date: String, title: String, description: String)
    }

    interface OnNoteDeletedListener {
        fun onNoteDeleted(noteId: String)
    }

    companion object {
        fun newInstance(noteId: String, date: String, title: String, description: String): FragmentDialogEditNote {
            val fragment = FragmentDialogEditNote()
            val bundle = Bundle()
            bundle.putString("noteId", noteId)
            bundle.putString("date", date)
            bundle.putString("title", title)
            bundle.putString("description", description)
            fragment.arguments = bundle
            return fragment
        }
    }
}
