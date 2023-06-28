package com.dicoding.mystoryapp.ui.story

import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.dicoding.mystoryapp.R
import com.dicoding.mystoryapp.data.remote.response.Story
import com.dicoding.mystoryapp.databinding.ActivityDetailBinding
import com.dicoding.mystoryapp.service.Result
import com.dicoding.mystoryapp.service.ViewModelFactory
import com.google.android.material.snackbar.Snackbar

class DetailActivity : AppCompatActivity() {
    private val storyViewModel: StoryViewModel by viewModels {
        ViewModelFactory.getInstance(application)
    }
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.navy)))
        supportActionBar?.title = "Detail Story"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupViewModel()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupViewModel() {
        val id = intent.getStringExtra(EXTRA_DATA)
        val token = intent.getStringExtra(EXTRA_TOKEN)

        if (token != null && id != null) {
            storyViewModel.getStory(token, id).observe(this) {
                if (it != null) {
                    when (it) {
                        is Result.Loading -> {
                            showLoading(true)
                        }
                        is Result.Success -> {
                            showLoading(false)
                            setStoryDetail(it.data)
                        }
                        is Result.Error -> {
                            showLoading(false)
                            Snackbar.make(
                                window.decorView.rootView,
                                it.error,
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun setStoryDetail(story: Story?) {
        Glide.with(this).load(story?.photoUrl).into(binding.ivDetailPhoto)
        binding.tvDetailName.text = story?.name
        binding.tvDetailDescription.text = story?.description
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val EXTRA_TOKEN = "TOKEN"
        const val EXTRA_DATA = "DATA"
    }
}