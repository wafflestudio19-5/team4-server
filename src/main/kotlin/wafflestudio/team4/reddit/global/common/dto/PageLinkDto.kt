package wafflestudio.team4.reddit.global.common.dto

data class PageLinkDto(
    val first: String,
    val prev: String?,
    val self: String,
    val next: String?,
    val last: String,
)
