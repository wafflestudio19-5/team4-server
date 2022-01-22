package wafflestudio.team4.reddit.domain.post.service

import wafflestudio.team4.reddit.domain.post.model.Post

class PostComparator : Comparator<Post> {
    var order: String = "recent"

    override fun compare(p0: Post, p1: Post): Int {
        if (order == "popular") {
            val p0Popularity = p0.votes.map { if (it.isUp == 2) 2 else if (it.isUp == 0) -1 else 0 }.sum()
            val p1Popularity = p1.votes.map { if (it.isUp == 2) 2 else if (it.isUp == 0) -1 else 0 }.sum()
            if (p0Popularity < p1Popularity) return 1
            else if (p0Popularity == p1Popularity) return 0
            else return -1
        }

        if (p0.id < p1.id) return 1
        else if (p0.id == p1.id) return 0
        else return -1
    }
}
