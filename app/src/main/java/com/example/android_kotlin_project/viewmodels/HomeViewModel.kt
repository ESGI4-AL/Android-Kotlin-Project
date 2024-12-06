package com.example.android_kotlin_project.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android_kotlin_project.repositories.QuoteRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val quoteRepository = QuoteRepository()

    private val _quote = MutableLiveData<String>()
    val quote: LiveData<String> get() = _quote

    fun fetchRandomQuote() {
        viewModelScope.launch {
            val randomQuote = quoteRepository.getRandomQuote()
            randomQuote?.let {
                _quote.postValue("${it["quote"]} - ${it["author"]}")
            }
        }
    }
}