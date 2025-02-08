package com.example.android_kotlin_project.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.android_kotlin_project.R

class JournalFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_journal, container, false)

        val addNoteButton: CardView = view.findViewById(R.id.addNoteButton)

        addNoteButton.setOnClickListener {
            val dialog = FragmentDialogAddNote()
            dialog.show(parentFragmentManager, "FragmentDialogAddNote")
        }

        return view
    }
}
