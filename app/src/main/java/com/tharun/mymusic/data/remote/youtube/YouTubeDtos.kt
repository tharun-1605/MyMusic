package com.tharun.mymusic.data.remote.youtube

import kotlinx.serialization.Serializable

@Serializable
data class YouTubeSearchResponseDto(
    val items: List<YouTubeSearchItemDto> = emptyList()
)

@Serializable
data class YouTubeSearchItemDto(
    val id: YouTubeIdDto? = null,
    val snippet: YouTubeSnippetDto? = null
)

@Serializable
data class YouTubeIdDto(
    val videoId: String? = null,
    val channelId: String? = null,
    val playlistId: String? = null
)

@Serializable
data class YouTubeSnippetDto(
    val title: String = "",
    val channelTitle: String = "",
    val description: String = "",
    val thumbnails: YouTubeThumbnailsDto? = null
)

@Serializable
data class YouTubeThumbnailsDto(
    val high: YouTubeThumbnailDto? = null,
    val medium: YouTubeThumbnailDto? = null,
    val default: YouTubeThumbnailDto? = null
)

@Serializable
data class YouTubeThumbnailDto(
    val url: String = ""
)

@Serializable
data class YouTubeVideoListResponseDto(
    val items: List<YouTubeVideoItemDto> = emptyList()
)

@Serializable
data class YouTubeVideoItemDto(
    val id: String,
    val snippet: YouTubeSnippetDto? = null,
    val contentDetails: YouTubeContentDetailsDto? = null
)

@Serializable
data class YouTubeContentDetailsDto(
    val duration: String? = null
)
