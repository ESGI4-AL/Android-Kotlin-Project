package com.example.android_kotlin_project.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android_kotlin_project.repositories.QuoteRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_kotlin_project.repositories.TipRepository
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val tipRepository = TipRepository()
    private val quoteRepository = QuoteRepository()

    private val _tip = MutableLiveData<String>()
    val tip: LiveData<String> get() = _tip

    private val _quote = MutableLiveData<String>()
    val quote: LiveData<String> get() = _quote

    /**
     * Fetch a random tip from the database
     */
    fun fetchRandomTip() {
        viewModelScope.launch {
            val randomTip = tipRepository.getRandomQuote()
            randomTip?.let {
                _tip.postValue(it)
            }
        }
    }

    /**
     * Fetch a random quote from the database
     */
    fun fetchRandomQuote() {
        viewModelScope.launch {
            val randomQuote = quoteRepository.getRandomQuote()
            randomQuote?.let {
                _quote.postValue("${it["quote"]} - ${it["author"]}")
            }
        }
    }
}
