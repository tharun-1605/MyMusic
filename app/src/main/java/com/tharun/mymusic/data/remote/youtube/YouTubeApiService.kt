package com.tharun.mymusic.data.remote.youtube

import retrofit2.http.GET
import retrofit2.http.Query

interface YouTubeApiService {
    @GET("youtube/v3/search")
    suspend fun searchMusic(
        @Query("part") part: String = "snippet",
        @Query("type") type: String = "video",
        @Query("videoCategoryId") videoCategoryId: String = "10",
        @Query("maxResults") maxResults: Int = 20,
        @Query("q") query: String,
        @Query("key") apiKey: String
    ): YouTubeSearchResponseDto

    @GET("youtube/v3/videos")
    suspend fun getVideoDetails(
        @Query("part") part: String = "snippet,contentDetails",
        @Query("id") ids: String,
        @Query("key") apiKey: String
    ): YouTubeVideoListResponseDto
}
