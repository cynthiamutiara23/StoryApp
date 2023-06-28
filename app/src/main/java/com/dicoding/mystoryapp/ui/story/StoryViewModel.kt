package com.dicoding.mystoryapp.ui.story

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.mystoryapp.data.Repository
import com.dicoding.mystoryapp.data.remote.response.Story
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryViewModel(private val repository: Repository) : ViewModel() {
    val token: LiveData<String> = repository.getToken()

    val story: LiveData<PagingData<Story>> = token.switchMap { token ->
        repository.getStories(token).cachedIn(viewModelScope)
    }

    fun getLocations(token: String) = repository.getLocations(token)

    fun getStory(token: String, id: String) = repository.getStory(token, id)

    fun addStory(token: String, image: MultipartBody.Part, description: RequestBody) = repository.addStory(token, image, description)
}