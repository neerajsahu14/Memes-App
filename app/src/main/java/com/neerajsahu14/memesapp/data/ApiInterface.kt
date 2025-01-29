package com.neerajsahu14.memesapp.data

import com.neerajsahu14.memesapp.model.AllMemes
import com.neerajsahu14.memesapp.model.Meme
import retrofit2.Response
import retrofit2.http.GET

interface ApiInterface {
    @GET("get_memes")
    suspend fun getMemeList(): Response<AllMemes>
}