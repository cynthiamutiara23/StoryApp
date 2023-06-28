package com.dicoding.mystoryapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.asLiveData
import androidx.paging.*
import com.dicoding.mystoryapp.data.database.StoryDatabase
import com.dicoding.mystoryapp.data.local.TokenPreference
import com.dicoding.mystoryapp.data.paging.StoryPagingSource
import com.dicoding.mystoryapp.data.remote.response.*
import com.dicoding.mystoryapp.data.remote.retrofit.ApiConfig
import com.dicoding.mystoryapp.data.remote.retrofit.ApiService
import com.dicoding.mystoryapp.service.Result
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Repository private constructor(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService,
    private val tokenPreference: TokenPreference
) {
    fun getStories(token: String): LiveData<PagingData<Story>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService, token)
            }
        ).liveData
    }

    private val registerResult = MediatorLiveData<Result<CommonResponse>>()
    private val loginResult = MediatorLiveData<Result<LoginResult>>()
    private val storyListResult = MediatorLiveData<Result<List<Story?>>>()
    private val storyDetailResult = MediatorLiveData<Result<Story?>>()
    private val uploadResult = MediatorLiveData<Result<CommonResponse>>()

    fun register(name: String, email: String, password: String): LiveData<Result<CommonResponse>> {
        registerResult.value = Result.Loading

        val client = ApiConfig.getApiService().register(name, email, password)
        client.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(
                call: Call<CommonResponse>,
                response: Response<CommonResponse>)
            {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if(responseBody?.error == false){
                        registerResult.value = Result.Success(responseBody)
                    } else {
                        registerResult.value = Result.Error(responseBody?.message.toString())
                    }
                } else {
                    registerResult.value = Result.Error(response.message())
                }
            }

            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                registerResult.value = Result.Error(t.message.toString())
                Log.e(AuthenticationTAG, "onFailure: ${t.message}")
            }
        })
        return registerResult
    }

    fun login(email: String, password: String) : LiveData<Result<LoginResult>>{
        loginResult.value = Result.Loading
        val client = apiService.login(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>)
            {
                if(response.isSuccessful){
                    val responseBody = response.body()
                    if(responseBody?.loginResult != null){
                        loginResult.value = Result.Success(responseBody.loginResult)
                    } else {
                        loginResult.value = Result.Error(responseBody?.message.toString())
                    }
                } else{
                    loginResult.value = Result.Error(response.message())
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                loginResult.value = Result.Error(t.message.toString())
                Log.e(AuthenticationTAG, "onFailure: ${t.message}")
            }

        })
        return loginResult
    }

    suspend fun setToken(token: String, sessionId: String){
        tokenPreference.saveToken(token, sessionId)
    }

    fun getToken(): LiveData<String> {
        return tokenPreference.getToken().asLiveData()
    }

    fun getSessionId(): LiveData<String> {
        return tokenPreference.getSession().asLiveData()
    }

    suspend fun logout(){
        tokenPreference.deleteToken()
    }

    fun getLocations(token: String) : LiveData<Result<List<Story?>>> {
        storyListResult.value = Result.Loading

        val client = apiService.getLocations(token = "Bearer $token", 1, 20)

        client.enqueue(object : Callback<ListStoryResponse> {
            override fun onResponse(
                call: Call<ListStoryResponse>,
                response: Response<ListStoryResponse>
            ) {
                if(response.isSuccessful){
                    val responseBody = response.body()
                    if(responseBody?.listStory != null){
                        storyListResult.value = Result.Success(responseBody.listStory)
                    } else {
                        storyListResult.value = Result.Error(response.message())
                    }
                } else {
                    storyListResult.value = Result.Error(response.message())
                }
            }

            override fun onFailure(call: Call<ListStoryResponse>, t: Throwable) {
                storyListResult.value = Result.Error(t.message.toString())
                Log.e(StoryTAG, "onFailure: ${t.message}")
            }

        })
        return storyListResult
    }

    fun getStory(token: String, id: String): LiveData<Result<Story?>> {
        storyDetailResult.value = Result.Loading

        val client = ApiConfig.getApiService().getStory("Bearer $token", id)
        client.enqueue(object : Callback<DetailStoryResponse> {
            override fun onResponse(
                call: Call<DetailStoryResponse>,
                response: Response<DetailStoryResponse>
            ) {
                if(response.isSuccessful){
                    val responseBody = response.body()
                    if(responseBody?.story != null){
                        storyDetailResult.value = Result.Success(responseBody.story)
                    } else {
                        storyDetailResult.value = Result.Error(response.message())
                    }
                } else {
                    storyDetailResult.value = Result.Error(response.message())
                }
            }

            override fun onFailure(call: Call<DetailStoryResponse>, t: Throwable) {
                storyDetailResult.value = Result.Error(t.message.toString())
                Log.e(StoryTAG, "onFailure: ${t.message}")
            }
        })
        return storyDetailResult
    }

    fun addStory(token: String, image: MultipartBody.Part, description: RequestBody): LiveData<Result<CommonResponse>>{
        uploadResult.value = Result.Loading

        val client = ApiConfig.getApiService().addStory("Bearer $token", image, description)
        client.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(
                call: Call<CommonResponse>,
                response: Response<CommonResponse>
            ) {
                if(response.isSuccessful){
                    val responseBody = response.body()
                    if(responseBody?.error == false){
                        uploadResult.value = Result.Success(responseBody)
                    } else {
                        uploadResult.value = Result.Error(response.message())
                    }
                } else {
                    uploadResult.value = Result.Error(response.message())
                }
            }

            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                uploadResult.value = Result.Error(t.message.toString())
                Log.e(AuthenticationTAG, "onFailure: ${t.message}")
            }
        })
        return uploadResult
    }

    companion object {
        private const val AuthenticationTAG = "Authentication"
        private const val StoryTAG = "Story"

        @Volatile
        private var instance: Repository? = null
        fun getInstance(
            storyDatabase: StoryDatabase,
            apiService: ApiService,
            tokenPreference: TokenPreference
        ): Repository = instance ?: synchronized(this){
            instance ?: Repository(storyDatabase, apiService, tokenPreference)
        }.also {
            instance = it
        }
    }
}