package com.dicoding.mystoryapp.service

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.dicoding.mystoryapp.data.Repository
import com.dicoding.mystoryapp.data.database.StoryDatabase
import com.dicoding.mystoryapp.data.local.TokenPreference
import com.dicoding.mystoryapp.data.remote.retrofit.ApiConfig

object Injection {
    private const val TOKEN = "token"
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = TOKEN)

    fun provideRepository(context: Context): Repository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        val tokenPreference = TokenPreference.getInstance(context.dataStore)
        return Repository.getInstance(database, apiService, tokenPreference)
    }
}