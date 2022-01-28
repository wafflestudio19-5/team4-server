package wafflestudio.team4.reddit.domain.comment.service

import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import wafflestudio.team4.reddit.domain.comment.dto.CommentDto
import wafflestudio.team4.reddit.domain.comment.exception.CommentNotFoundException
import wafflestudio.team4.reddit.domain.comment.exception.NotCommentOwnerException
import wafflestudio.team4.reddit.domain.comment.model.Comment
import wafflestudio.team4.reddit.domain.comment.model.CommentVote
import wafflestudio.team4.reddit.domain.comment.repository.CommentRepository
import wafflestudio.team4.reddit.domain.comment.repository.CommentVoteRepository
import wafflestudio.team4.reddit.domain.post.exception.PostNotFoundException
import wafflestudio.team4.reddit.domain.post.repository.PostRepository
import wafflestudio.team4.reddit.domain.user.model.User

@Service
class CommentService(
    private val commentRepository: CommentRepository,
    private val commentVoteRepository: CommentVoteRepository,
    private val postRepository: PostRepository,
) {
    fun getComments(lastCommentId: Long, size: Int, postId: Long): List<Comment> {
        val post = postRepository.findByIdOrNull(postId) ?: throw PostNotFoundException()
        val pageRequest: PageRequest = PageRequest.of(0, size)
        val parentCommentList = commentRepository.findByPostIsAndIdLessThanAndDeletedIsNotAndDepthIsOrderByIdDesc(
            post,
            lastCommentId,
            2,
            0,
            pageRequest
        ).content

        return parentCommentList
    }

    fun createComment(user: User, postId: Long, request: CommentDto.CreateRequest): Comment {
        val post = postRepository.findByIdOrNull(postId) ?: throw PostNotFoundException()
        val newComment = Comment(
            user = user,
            post = post,
            text = request.text,
            depth = 1
        )
        newComment.rootComment = newComment
        return commentRepository.save(newComment)
    }

    fun replyComment(user: User, postId: Long, parentCommentId: Long, request: CommentDto.ReplyRequest): Comment {
        val post = postRepository.findByIdOrNull(postId) ?: throw PostNotFoundException()
        val parentComment = commentRepository.findByIdOrNull(parentCommentId) ?: throw CommentNotFoundException()

        val newComment = Comment(
            user = user,
            post = post,
            text = request.text,
            depth = parentComment.depth + 1,
            rootComment = parentComment.rootComment,
            parentComment = parentComment
        )
        parentComment.childrenComments.add(newComment)
        commentRepository.save(parentComment)

        return newComment
    }

    fun deleteComment(user: User, commentId: Long): Comment {
        var comment = commentRepository.findByIdOrNull(commentId) ?: throw CommentNotFoundException()

        // 코멘트 작성자 확인
        val commentOwnerId = comment.user.id
        if (commentOwnerId != user.id) throw NotCommentOwnerException()

        // 코멘트 children 유무
        fun checkChildAndDelete(comment: Comment): Comment {
            if (commentRepository.existsByParentCommentIsAndDeletedIsNot(comment, 2) &&
                commentRepository.findByParentCommentIsAndDeletedIsNot(comment, 2).count() > 1
            ) {
                comment.deleted = 1 // 삭제 보류
                comment.text = "[deleted]" // reddit 스타일
            } else
                comment.deleted = 2
            return comment
        }
        commentRepository.save(checkChildAndDelete(comment))

        // parent가 삭제 보류 상태일시 확인하고 업데이트
        if (comment.parentComment!!.deleted == 1) {
            commentRepository.save(checkChildAndDelete(comment.parentComment!!))
        }

        return comment
    }

    fun modifyComment(user: User, commentId: Long, request: CommentDto.ModifyRequest): Comment {
        val comment = commentRepository.findByIdOrNull(commentId) ?: throw CommentNotFoundException()

        // 코멘트 작성자 확인
        val commentOwnerId = comment.user.id
        if (commentOwnerId != user.id) throw NotCommentOwnerException()

        comment.text = request.text

        return commentRepository.save(comment)
    }

    fun voteComment(user: User, commentId: Long, isUp: Int): Comment {
        val comment = commentRepository.findByIdOrNull(commentId) ?: throw CommentNotFoundException()
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
