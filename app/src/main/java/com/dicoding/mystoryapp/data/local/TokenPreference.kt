package com.dicoding.mystoryapp.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TokenPreference private constructor(private val dataStore: DataStore<Preferences>){

    private val tokenKey = stringPreferencesKey(tokenNameKey)
    private val sessionIdKey = stringPreferencesKey(sessionNameKey)

    fun getToken(): Flow<String> {
        return dataStore.data.map {
            it[tokenKey] ?: ""
        }
    }

    fun getSession(): Flow<String> {
        return dataStore.data.map {
            it[sessionIdKey] ?: ""
        }
    }

    suspend fun saveToken(token: String, session: String){
        dataStore.edit {
            it[tokenKey] = token
            it[sessionIdKey] = session
        }
    }

    suspend fun deleteToken(){
        dataStore.edit {
            it[tokenKey] = ""
        }
        dataStore.edit {
            it[sessionIdKey] = ""
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: TokenPreference? = null
        fun getInstance(dataStore: DataStore<Preferences>):TokenPreference {
            return INSTANCE ?: synchronized(this){
                val instance = TokenPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }

        private const val tokenNameKey = "token_setting"
        private const val sessionNameKey = "session_setting"
    }
}