package wafflestudio.team4.reddit.domain.post.service

import com.amazonaws.HttpMethod
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import wafflestudio.team4.reddit.domain.community.repository.CommunityRepository
import wafflestudio.team4.reddit.domain.post.dto.PostDto
import wafflestudio.team4.reddit.domain.post.exception.NotPostOwnerException
import wafflestudio.team4.reddit.domain.post.exception.PostNotFoundException
import wafflestudio.team4.reddit.domain.post.model.Post
import wafflestudio.team4.reddit.domain.post.model.PostCommunity
import wafflestudio.team4.reddit.domain.post.model.PostImage
import wafflestudio.team4.reddit.domain.post.model.PostVote
import wafflestudio.team4.reddit.domain.post.repository.PostCommunityRepository
import wafflestudio.team4.reddit.domain.post.repository.PostImageRepository
import wafflestudio.team4.reddit.domain.post.repository.PostRepository
import wafflestudio.team4.reddit.domain.post.repository.PostVoteRepository
import wafflestudio.team4.reddit.domain.user.model.User
import java.util.Date

@Service
class PostService(
    private val postRepository: PostRepository,
    private val postCommunityRepository: PostCommunityRepository,
    private val postVoteRepository: PostVoteRepository,
    private val communityRepository: CommunityRepository,
    private val postImageRepository: PostImageRepository,

    private val amazonS3: AmazonS3,
) {

    fun getPosts(lasPostId: Long, size: Int): List<Post> {
        val pageRequest: PageRequest = PageRequest.of(0, size)
        return postRepository.findByIdLessThanAndDeletedIsFalseOrderByIdDesc(lasPostId, pageRequest).content
    }

    fun getPostById(postId: Long): Post {
        return postRepository.findByIdOrNull(postId) ?: throw PostNotFoundException()
    }

    fun getPresignedUrl(fileName: String): String {
        val expiration: Date = Date()
        var expTimeMillis = expiration.time
        expTimeMillis += (1000 * 60 * 60).toLong() // 1시간
        expiration.time = expTimeMillis

        val request = GeneratePresignedUrlRequest("waffle-team-4-server-s3", "posts/$expiration/$fileName")
            .withMethod(HttpMethod.PUT)
            .withExpiration(expiration)

//        request.addRequestParameter(
//            Headers.S3_CANNED_ACL,
//            CannedAccessControlList.PublicRead.toString()
//        )

        return amazonS3.generatePresignedUrl(request).toString()
    }

    fun createPost(user: User, request: PostDto.CreateRequest): Post {
        val community = communityRepository.findByName(request.community)

        val newPost = Post(
            user = user,
            community = community,
            title = request.title,
            text = request.text,
        )
        postRepository.save(newPost)

        request.images?.forEach {
            val newPostImage: PostImage = PostImage(newPost, it)
            postImageRepository.save(newPostImage)
            newPost.images?.add(newPostImage) // add images if exist
        }

        val newPostCommunity = PostCommunity(newPost, community)
        postCommunityRepository.save(newPostCommunity)

        return postRepository.save(newPost)
    }

    fun deletePost(user: User, postId: Long): Post {
        val post = postRepository.findByIdOrNull(postId) ?: throw PostNotFoundException()

        // 포스트 작성자 확인
        val postOwnerId = post.user.id
        if (postOwnerId != user.id) throw NotPostOwnerException()

        post.deleted = true
        return postRepository.save(post)
    }

//    fun modifyPost(user: User, postId: Long, request: PostDto.CreateRequest): Post{
//        val post = postRepository.findByIdOrNull(postId) ?: throw Exception()
//        val postOwner = post.user
//        if (postOwner != user) throw Exception()
//
//        post.title = request.title
//        post.text = request.text
//        // post.images
//
//        return postRepository.save(post)
//    }

    fun vote(user: User, postId: Long, isUp: Int): Post {
        val post = postRepository.findByIdOrNull(postId) ?: throw PostNotFoundException()

        // if vote history exists, change its isUp attribute
        if (postVoteRepository.existsByPostAndUser(post, user)) {
            val voteHistory = postVoteRepository.findByPostAndUser(post, user)
            if (voteHistory.isUp == isUp) voteHistory.isUp = 1
            else voteHistory.isUp = isUp
            postVoteRepository.save(voteHistory)
            return post
        }
        // else create new vote
        else {
            val newVote = PostVote(
                post = post,
                user = user,
                isUp = isUp
            )
            postVoteRepository.save(newVote)
            post.votes.add(newVote)
            postRepository.save(post)
            return post
        }
    }
}