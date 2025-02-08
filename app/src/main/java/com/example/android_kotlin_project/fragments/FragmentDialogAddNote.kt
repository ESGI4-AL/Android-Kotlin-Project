package com.example.android_kotlin_project.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.example.android_kotlin_project.R

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

            if (title.isEmpty()) {
                noteTitle.error = "Title is required"
                return@setOnClickListener
            }

            val listener = activity as? OnNoteAddedListener
            listener?.onNoteAdded(date, title, description)

            dialog.dismiss()
        }

        return dialog
    }

    interface OnNoteAddedListener {
        fun onNoteAdded(date: String, title: String, description: String)
    }
}
