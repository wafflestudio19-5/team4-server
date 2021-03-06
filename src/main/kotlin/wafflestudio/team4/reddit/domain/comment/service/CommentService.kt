package wafflestudio.team4.reddit.domain.comment.service

import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import wafflestudio.team4.reddit.domain.comment.dto.CommentDto
import wafflestudio.team4.reddit.domain.comment.exception.CommentNotFoundException
import wafflestudio.team4.reddit.domain.comment.exception.NotCommentOwnerException
import wafflestudio.team4.reddit.domain.comment.model.Comment
import wafflestudio.team4.reddit.domain.comment.model.CommentVote
import wafflestudio.team4.reddit.domain.comment.repository.CommentRepository
import wafflestudio.team4.reddit.domain.comment.repository.CommentVoteRepository
import wafflestudio.team4.reddit.domain.post.exception.PostNotFoundException
import wafflestudio.team4.reddit.domain.post.repository.PostRepository
import wafflestudio.team4.reddit.domain.user.exception.UserNotFoundException
import wafflestudio.team4.reddit.domain.user.model.User
import wafflestudio.team4.reddit.domain.user.repository.UserRepository
import java.util.Collections

@Service
class CommentService(
    private val commentRepository: CommentRepository,
    private val commentVoteRepository: CommentVoteRepository,
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
) {

    fun mergeUser(user: User): User {
        return userRepository.findByIdOrNull(user.id) ?: throw UserNotFoundException()
    }
    fun getCommentById(commentId: Long): Comment {
        return commentRepository.findByIdOrNull(commentId) ?: throw CommentNotFoundException()
    }

    fun getComments(lastCommentId: Long, size: Int, postId: Long): List<Comment> {
        val post = postRepository.findByIdOrNull(postId) ?: throw PostNotFoundException()
        val pageRequest: PageRequest = PageRequest.of(0, size)
        val parentCommentList = commentRepository.findByPostIsAndIdLessThanAndDeletedIsNotAndDepthIsOrderByIdDesc(
            post,
            lastCommentId,
            2,
            1,
            pageRequest
        ).content

        return parentCommentList
    }

    fun getCommentsByPopularity(lastCommentId: Long, size: Int, postId: Long): List<Comment> {
        val post = postRepository.findByIdOrNull(postId) ?: throw PostNotFoundException()
        // get all comments
        val comments = commentRepository.findByPostIsAndDeletedIsNotAndDepthIs(post, 2, 1)

        // sort
        val commentComparator = CommentComparator()
        commentComparator.order = "popular"
        Collections.sort(comments, commentComparator)

        // pagination
        if (comments.isEmpty()) return comments
        val lastComment: Comment? = comments.find { it.id == lastCommentId }
        val lastCommentIndex = comments.indexOf(lastComment)
        val firstIndex = lastCommentIndex + 1
        val lastIndex = kotlin.math.min((firstIndex + (size - 1)), comments.lastIndex)
        val commentPage = comments.slice(IntRange(firstIndex, lastIndex))
        return commentPage
    }

    @Transactional
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

    @Transactional
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
        commentRepository.save(newComment)
        parentComment.childrenComments.add(newComment)

        return newComment
    }

    @Transactional
    fun deleteComment(user: User, commentId: Long): Comment {
        var comment = commentRepository.findByIdOrNull(commentId) ?: throw CommentNotFoundException()

        // ????????? ????????? ??????
        val commentOwnerId = comment.user.id
        if (commentOwnerId != user.id) throw NotCommentOwnerException()

        // ????????? children ?????? ?????? ??? parent??? children????????? ??????
        fun checkAndDelete(comment: Comment): Comment {
            if (comment.childrenComments.isNotEmpty()) {
                comment.deleted = 1 // ?????? ??????
                comment.text = "[deleted]" // reddit ?????????
                return commentRepository.save(comment)
            } else {
                val check = comment.parentComment
                comment.deleted = 2
                comment.parentComment?.childrenComments?.remove(comment)
                comment.parentComment = null
                commentRepository.save(comment)
                if (check != null && check!!.deleted == 1) {
                    commentRepository.save(checkAndDelete(check))
                }
            }
            return comment
        }
        return checkAndDelete(comment)
    }

    @Transactional
    fun modifyComment(user: User, commentId: Long, request: CommentDto.ModifyRequest): Comment {
        val comment = commentRepository.findByIdOrNull(commentId) ?: throw CommentNotFoundException()

        // ????????? ????????? ??????
        val commentOwnerId = comment.user.id
        if (commentOwnerId != user.id) throw NotCommentOwnerException()

        comment.text = request.text

        return commentRepository.save(comment)
    }

    @Transactional
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
