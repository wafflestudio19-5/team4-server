package wafflestudio.team4.reddit.domain.post.repository

import org.springframework.data.jpa.repository.JpaRepository
import wafflestudio.team4.reddit.domain.post.model.PostImage

interface PostImageRepository : JpaRepository<PostImage, Long?> {

}
