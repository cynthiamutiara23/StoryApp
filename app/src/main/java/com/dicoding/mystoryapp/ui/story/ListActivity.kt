package com.dicoding.mystoryapp.ui.story

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.mystoryapp.R
import com.dicoding.mystoryapp.adapter.ListStoryAdapter
import com.dicoding.mystoryapp.adapter.LoadingStateAdapter
import com.dicoding.mystoryapp.databinding.ActivityListBinding
import com.dicoding.mystoryapp.service.ViewModelFactory

class ListActivity : AppCompatActivity() {
    private val storyViewModel: StoryViewModel by viewModels {
        ViewModelFactory.getInstance(application)
    }
    private lateinit var binding: ActivityListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.navy)))
        supportActionBar?.title = "List Story"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val layoutManager = LinearLayoutManager(this)
        binding.rvStory.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, 0)
        binding.rvStory.addItemDecoration(itemDecoration)

        setStoryList()
        setupAction()
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

    private fun setStoryList() {
        val adapter = ListStoryAdapter()
        binding.rvStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
        storyViewModel.story.observe(this) {
            adapter.submitData(lifecycle, it)
        }
    }

    private fun setupAction() {
        binding.fabAdd.setOnClickListener{
            val intent = Intent(this, AddActivity::class.java)
            intent.putExtra(AddActivity.EXTRA_TOKEN, this.intent.getStringExtra(EXTRA_TOKEN))
            startActivity(intent)
            finish()
        }
    }

    companion object {
        const val EXTRA_TOKEN = "TOKEN"
    }
}