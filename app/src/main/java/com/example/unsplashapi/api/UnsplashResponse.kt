package com.example.unsplashapi.api

import com.example.unsplashapi.data.UnsplashPhoto

data class UnsplashResponse(
    val results: List<UnsplashPhoto>
)