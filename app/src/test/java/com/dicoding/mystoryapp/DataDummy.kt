package com.dicoding.mystoryapp

import com.dicoding.mystoryapp.data.remote.response.Story

object DataDummy {

    fun generateDummyStory(): List<Story> {
        val items: MutableList<Story> = arrayListOf()
        for (i in 0..100) {
            val story = Story(
                i.toString(),
                "author $i",
                "description $i",
                "url $i",
                "2022-02-22T22:22:22Z",
                0.0,
                0.0
            )
            items.add(story)
        }
        return items
    }
}