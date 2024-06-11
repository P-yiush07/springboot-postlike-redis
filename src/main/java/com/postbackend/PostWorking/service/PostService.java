package com.postbackend.PostWorking.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import com.postbackend.PostWorking.entity.Post;
import com.postbackend.PostWorking.repository.PostRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class PostService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private PostRepo postRepo;

//    private List<Post> posts = new ArrayList<>();
    private Map<String, Set<String>> postLikes = new HashMap<>();

    public List<Post> getAllPosts() {

        return postRepo.findAll();
    }

    // Method to add a post
    public boolean addPost(Post post) {
        Optional<Post> existingPost = postRepo.findById(post.getId());
        if (existingPost.isPresent()) {
            return false; // Post with the same ID already exists
        }

        postRepo.save(post);
        postLikes.put(String.valueOf(post.getId()), new HashSet<>()); // Initialize likes set for the post
        return true;
    }

    @Cacheable(value = "likes", key = "#postId")
    public int getLikes(long postId) {
        Post post = postRepo.findById(postId).orElse(null);
        if (post == null) {
            throw new RuntimeException("Post not found");
        }
        return post.getLikes();
    }

    @CacheEvict(value = "likes", key = "#postId")
    @Transactional
    public void incrementLikes(long postId, long userId) {
        String postLikeKey = "post:" + postId + ":likes";
        String userLikeKey = "user:" + userId + ":liked:" + postId;

        // Check if user already liked the post
        if (redisTemplate.opsForValue().get(userLikeKey) != null) {
            throw new RuntimeException("User already liked this post");
        }

        // Increment post likes in Redis
        redisTemplate.opsForValue().increment(postLikeKey);

        // Mark post as liked by the user in Redis
        redisTemplate.opsForValue().set(userLikeKey, true);

        // Update post likes count in the database
        Post post = postRepo.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
        post.setLikes(post.getLikes() + 1);
        postRepo.save(post);
    }


}
