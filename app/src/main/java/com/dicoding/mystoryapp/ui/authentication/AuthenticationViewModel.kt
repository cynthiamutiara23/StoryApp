package com.dicoding.mystoryapp.ui.authentication

import androidx.lifecycle.*
import com.dicoding.mystoryapp.data.Repository
import kotlinx.coroutines.launch

class AuthenticationViewModel(private val repository: Repository) : ViewModel() {
    fun register(personName: String, email: String, password: String) = repository.register(personName, email, password)

    fun login(email: String, password: String) = repository.login(email, password)

    fun logout(){
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun setToken(token: String, sessionId: String){
        viewModelScope.launch {
            repository.setToken(token, sessionId)
        }
    }

    fun getToken() = repository.getToken()

    fun getSessionId() = repository.getSessionId()
}