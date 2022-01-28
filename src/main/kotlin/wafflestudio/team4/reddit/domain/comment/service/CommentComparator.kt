package wafflestudio.team4.reddit.domain.comment.service

import wafflestudio.team4.reddit.domain.comment.model.Comment

class CommentComparator : Comparator<Comment> {
    var order: String = ""

    override fun compare(c0: Comment, c1: Comment): Int {
        if (order == "popular") {
            val c0Popularity = c0.votes.map { if (it.isUp == 2) 2 else if (it.isUp == 0) -1 else 0 }.sum()
            val c1Popularity = c1.votes.map { if (it.isUp == 2) 2 else if (it.isUp == 0) -1 else 0 }.sum()
            if (c0Popularity < c1Popularity) return 1
            else if (c0Popularity == c1Popularity) {
                // 같으면 나머진 최신순
                if (c0.id < c1.id) return 1
                else if (c0.id == c1.id) return 0
                else return -1
            } else return -1
        }

        if (c0.id < c1.id) return 1
        else if (c0.id == c1.id) return 0
        else return -1
    }
}
