package wafflestudio.team4.reddit.domain.comment.service

import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import wafflestudio.team4.reddit.domain.comment.dto.CommentDto
import wafflestudio.team4.reddit.domain.comment.model.Comment
import wafflestudio.team4.reddit.domain.comment.model.CommentVote
import wafflestudio.team4.reddit.domain.comment.repository.CommentRepository
import wafflestudio.team4.reddit.domain.comment.repository.CommentVoteRepository
import wafflestudio.team4.reddit.domain.post.repository.PostRepository
import wafflestudio.team4.reddit.domain.user.model.User
import java.lang.Exception

@Service
class CommentService(
    private val commentRepository: CommentRepository,
    private val commentVoteRepository: CommentVoteRepository,
    private val postRepository: PostRepository,
) {
    fun getComments(lastCommentId: Long, size: Int, postId: Long): List<Comment> {
        val post = postRepository.findByIdOrNull(postId) ?: throw Exception()
        val pageRequest: PageRequest = PageRequest.of(0, size)
        return commentRepository.findByPostIsAndIdLessThanAndDeletedIsNotOrderByIdDesc(
            post,
            lastCommentId,
            2,
            pageRequest
        ).content
    }

    fun createComment(user: User, postId: Long, request: CommentDto.CreateRequest): Comment {
        val post = postRepository.findByIdOrNull(postId) ?: throw Exception()
        val parentComment = commentRepository.findByIdOrNull(request.parentId) ?: throw Exception()

        val newComment = Comment(
            user = user,
            post = post,
            text = request.text,
            depth = request.depth,
            parent = parentComment,
        )

        return commentRepository.save(newComment)
    }

    fun deleteComment(user: User, commentId: Long): Comment {
        val comment = commentRepository.findByIdOrNull(commentId) ?: throw Exception()

        // 코멘트 작성자 확인
        val commentOwnerId = comment.user.id
        if (commentOwnerId != user.id) throw Exception()

        // 코멘트 children 유무
        if (commentRepository.existsByParentIs(comment)) {
            comment.deleted = 1 // 삭제 보류
            comment.text = "[deleted]" // reddit에서 이렇게 씀
        } else
            comment.deleted = 2

        return commentRepository.save(comment)
    }

    fun modifyComment(user: User, commentId: Long, request: CommentDto.ModifyRequest): Comment {
        val comment = commentRepository.findByIdOrNull(commentId) ?: throw Exception()

        // 코멘트 작성자 확인
        val commentOwnerId = comment.user.id
        if (commentOwnerId != user.id) throw Exception()

        comment.text = request.text

        return commentRepository.save(comment)
    }

    fun voteComment(user: User, commentId: Long, isUp: Int): Comment {
        val comment = commentRepository.findByIdOrNull(commentId) ?: throw Exception()
        if (commentVoteRepository.existsByCommentAndUser(comment, user)) {
            val voteHistory = commentVoteRepository.findByCommentAndUser(comment, user)
            if (voteHistory.isUp == isUp) voteHistory.isUp = 1
            else voteHistory.isUp = isUp
            commentVoteRepository.save(voteHistory)
            return comment
        }
        // else create new vote
        else {
            val newVote = CommentVote(
                comment = comment,
                user = user,
                isUp = isUp
            )
            commentVoteRepository.save(newVote)
            comment.votes.add(newVote)
            commentRepository.save(comment)
            return comment
        }
    }
}
