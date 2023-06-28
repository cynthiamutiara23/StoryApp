package com.dicoding.mystoryapp.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.mystoryapp.databinding.ItemStoryBinding
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.dicoding.mystoryapp.data.remote.response.Story
import com.dicoding.mystoryapp.ui.story.DetailActivity
import com.dicoding.mystoryapp.ui.story.ListActivity.Companion.EXTRA_TOKEN

class ListStoryAdapter: PagingDataAdapter<Story, ListStoryAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    class ViewHolder(private val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(data: Story) {
            binding.tvItemName.text = data.name
            Glide.with(binding.root.context)
                .load(data.photoUrl)
                .into(binding.ivItemPhoto)

            itemView.setOnClickListener {
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    itemView.context as Activity,
                    Pair(binding.ivItemPhoto, "photo"),
                    Pair(binding.tvItemName, "name")
                )
                val intent = Intent(itemView.context as Activity, DetailActivity::class.java)
                intent.putExtra(DetailActivity.EXTRA_TOKEN, (itemView.context as Activity).intent.getStringExtra(EXTRA_TOKEN))
                intent.putExtra(DetailActivity.EXTRA_DATA, data.id)
                itemView.context.startActivity(intent, options.toBundle())
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}