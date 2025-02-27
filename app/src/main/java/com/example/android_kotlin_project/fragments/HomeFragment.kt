package com.example.android_kotlin_project.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android_kotlin_project.R
import com.example.android_kotlin_project.databinding.FragmentHomeBinding
import com.example.android_kotlin_project.viewmodels.HomeViewModel

class HomeFragment : Fragment() {
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var tipTextView: TextView
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

        tipTextView = fragmentBinding.tipsCard.tipsText

        quoteTextView = fragmentBinding.quoteCard.quoteText

        homeViewModel.quote.observe(viewLifecycleOwner) { quote ->
            updateQuote(quote)
        }

        homeViewModel.tip.observe(viewLifecycleOwner) { quote ->
            updateTip(quote)
        }

        homeViewModel.fetchRandomTip()
        homeViewModel.fetchRandomQuote()

        fragmentBinding.thoughtCard.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_journalFragment)
        }
    }

    /**
     * Update the quote text view with the new quote
     */
    private fun updateQuote(quote: String) {
        quoteTextView.text = quote
    }

    /**
     * Update the tip text view with the new tip
     */
    private fun updateTip(tip: String) {
        tipTextView.text = tip
    }

    /**
     * Clean up the view
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _fragmentBinding = null
    }
}
