package com.example.android_kotlin_project.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android_kotlin_project.databinding.FragmentHomeBinding
import com.example.android_kotlin_project.viewmodels.HomeViewModel

class HomeFragment : Fragment() {
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var quoteTextView: TextView

    private var _fragmentBinding: FragmentHomeBinding? = null
    private val fragmentBinding get() = _fragmentBinding!!

    /**
     * Create the home view
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragmentBinding = FragmentHomeBinding.inflate(inflater, container, false)

        return fragmentBinding.root
    }

    /**
     * Set up the view
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]

        quoteTextView = fragmentBinding.quoteCard.quoteText

        homeViewModel.quote.observe(viewLifecycleOwner) { quote ->
            updateQuote(quote)
        }

        homeViewModel.fetchRandomQuote()

    }

    /**
     * Update the quote text view with the new quote
     */
    private fun updateQuote(quote: String) {
        quoteTextView.text = quote
    }

    /**
     * Clean up the view
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _fragmentBinding = null
    }
}
