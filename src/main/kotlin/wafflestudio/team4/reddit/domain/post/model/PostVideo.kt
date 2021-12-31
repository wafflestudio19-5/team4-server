package wafflestudio.team4.reddit.domain.post.model

import wafflestudio.team4.reddit.domain.model.BaseTimeEntity
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "reddit_post_video")
class PostVideo() : BaseTimeEntity()
