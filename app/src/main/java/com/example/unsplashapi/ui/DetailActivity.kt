package com.example.unsplashapi.ui

import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.unsplashapi.R
import com.example.unsplashapi.data.UnsplashPhoto
import com.example.unsplashapi.databinding.ActivityDetailBinding
import com.example.unsplashapi.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity(){

 private lateinit  var binding: ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val photo = intent.extras?.get(HomeFragment.INTENT_DETAIL_KEY) as UnsplashPhoto
        title=photo.user.name

      Glide.with(this)
          .load(photo.urls.regular)
          .error(R.drawable.ic_error)
          .listener(object: RequestListener<Drawable>{
              override fun onLoadFailed(
                  e: GlideException?,
                  model: Any?,
                  target: Target<Drawable>?,
                  isFirstResource: Boolean
              ): Boolean {
                  progress_bar.isVisible=false
                  return false
              }

              override fun onResourceReady(
                  resource: Drawable?,
                  model: Any?,
                  target: Target<Drawable>?,
                  dataSource: DataSource?,
                  isFirstResource: Boolean
              ): Boolean {
                  progress_bar.isVisible=false
                  text_view_creator.isVisible=true
                  text_view_description.isVisible= photo.description != null
                  return false
              }

          })
          .into(binding.imageView)

        binding.apply {
            textViewCreator.text=photo.user.name
            textViewDescription.text=photo.description
        }



    }
}