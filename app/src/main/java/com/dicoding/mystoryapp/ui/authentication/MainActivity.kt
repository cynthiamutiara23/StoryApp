package com.dicoding.mystoryapp.ui.authentication

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.mystoryapp.R
import com.dicoding.mystoryapp.databinding.ActivityMainBinding
import com.dicoding.mystoryapp.service.ViewModelFactory
import com.dicoding.mystoryapp.ui.story.ListActivity
import com.dicoding.mystoryapp.ui.story.MapsActivity

class MainActivity : AppCompatActivity() {
    private val authenticationViewModel: AuthenticationViewModel by viewModels {
        ViewModelFactory.getInstance(application)
    }
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupViewModel()
        setupAction()
        playAnimation()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupViewModel() {
        authenticationViewModel.getSessionId().observe(this) {
            if (it.isEmpty()) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                binding.nameTextView.text = getString(R.string.greeting)
                Handler(Looper.getMainLooper()).post {
                    authenticationViewModel.getToken().observe(this) { token ->
                        if (token.isNotEmpty()) {
                            binding.actionListStory.setOnClickListener{
                                val intentToList = Intent(this, ListActivity::class.java)
                                intentToList.putExtra(ListActivity.EXTRA_TOKEN, token)
                                startActivity(intentToList)
                            }

                            binding.actionStoryMaps.setOnClickListener{
                                val intentToMaps = Intent(this, MapsActivity::class.java)
                                intentToMaps.putExtra(ListActivity.EXTRA_TOKEN, token)
                                startActivity(intentToMaps)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupAction() {
        binding.actionLogout.setOnClickListener {
            authenticationViewModel.logout()
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val name = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(500)
        val message = ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(500)
        val storyList = ObjectAnimator.ofFloat(binding.actionListStory, View.ALPHA, 1f).setDuration(500)
        val storyMaps = ObjectAnimator.ofFloat(binding.actionStoryMaps, View.ALPHA, 1f).setDuration(500)
        val logout = ObjectAnimator.ofFloat(binding.actionLogout, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(name, message, storyList, storyMaps, logout)
            startDelay = 500
            start()
        }
    }
}