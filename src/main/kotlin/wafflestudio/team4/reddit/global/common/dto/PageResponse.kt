package wafflestudio.team4.reddit.global.common.dto

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.domain.Page

data class PageResponse<T> (
    val content: List<T>,
    val size: Int,
    @JsonProperty("number_of_elements")
    val numberOfElements: Int,
    val links: PageLinkDto?, // TODO
) {
    constructor(page: Page<T>, links: PageLinkDto?) : this(
        page.content,
        page.size,
        page.numberOfElements,
        links,
    )

    constructor(page: Page<T>) : this(
        page.content,
        page.size,
        page.numberOfElements,
        null,
    )
}
