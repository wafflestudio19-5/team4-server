package wafflestudio.team4.reddit.domain.post.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import wafflestudio.team4.reddit.domain.post.dto.PostDto
import wafflestudio.team4.reddit.domain.post.model.Post
import wafflestudio.team4.reddit.domain.post.model.PostCommunity
import wafflestudio.team4.reddit.domain.post.model.PostVote
import wafflestudio.team4.reddit.domain.post.repository.PostCommunityRepository
import wafflestudio.team4.reddit.domain.post.repository.PostRepository
import wafflestudio.team4.reddit.domain.post.repository.PostVoteRepository
import wafflestudio.team4.reddit.domain.user.model.User

@Service
class PostService(
    private val postRepository: PostRepository,
    private val postCommunityRepository: PostCommunityRepository,
    private val postVoteRepository: PostVoteRepository,
) {
    fun getPosts(lasPostId: Int, size: Int): List<Post> {
        val pageRequest : PageRequest = PageRequest.of(0,size);
        return postRepository.findByIdLessThanAndOrderByIdDesc(lasPostId, pageRequest).content
    }

    fun getPostById(postId: Long): Post {
        return postRepository.findByIdOrNull(postId) ?: throw Exception() // TODO: 예외처리
    }

    fun createPost (user: User, request: PostDto.CreateRequest): Post{
        // val community = communityRepository.findByName(request.communityName)
        val newPost = Post(
            user = user,
            title = request.title,
            content = request.content,
            // community = communityRepository.findByName(request.communityName)
            // images = request.images
        )
        // val newPostCommunity = PostCommunity(newPost, community)
        // postCommunityRepository.save(newPostCommunity)

        // val newPostImage
        // postImageRepository ..
        return postRepository.save(newPost)
    }

    fun deletePost(user: User, postId: Long): Post{
        val post = postRepository.findByIdOrNull(postId) ?: throw Exception() // TODO: 예외처리

        // 포스트 작성자 확인
        val postOwner = post.user
        if (postOwner != user) throw Exception()

        post.isDeleted = true
        return postRepository.save(post)
    }

    fun modifyPost(user: User, postId: Long, request: PostDto.CreateRequest): Post{
        val post = postRepository.findByIdOrNull(postId) ?: throw Exception()
        val postOwner = post.user
        if (postOwner != user) throw Exception()

        post.title = request.title
        post.content = request.content
        // post.images

        return postRepository.save(post)
    }

    fun vote(user: User, postId: Long, isUp: Boolean): Post{
        val post = postRepository.findByIdOrNull(postId) ?: throw Exception()

        val newVote = PostVote(
            post = post,
            voter = user,
            isUp = isUp
        )
        postVoteRepository.save(newVote)
        return post
    }


}
