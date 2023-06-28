package com.dicoding.mystoryapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class ListStoryResponse(

	@field:SerializedName("listStory")
	val listStory: List<Story>,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)