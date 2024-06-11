package com.postbackend.PostWorking.controller;

import com.postbackend.PostWorking.entity.Post;
import com.postbackend.PostWorking.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping("/posts")
    public ResponseEntity<?> createPost(@RequestBody Post post) {
        boolean isAdded = postService.addPost(post);
        if (isAdded) {
            return new ResponseEntity<>(post, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("A post with the same id already exists", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/posts")
    public ResponseEntity<List<Post>> getAllPosts() {
        List<Post> posts = postService.getAllPosts();

        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @GetMapping("/{postId}/likes")
    public int getLikes(@PathVariable long postId) {
        return postService.getLikes(postId);
    }

    @PostMapping("/{postId}/like/{userId}")
    public ResponseEntity<String> likePost(@PathVariable long postId, @PathVariable long userId) {
        try {
            postService.incrementLikes(postId, userId);
            return ResponseEntity.ok("Post liked successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("User already liked this post");
        }
    }

//    @PutMapping("/posts/{postId}/like/{userId}")
//    public ResponseEntity<?> likePost(@PathVariable String postId, @PathVariable String userId) {
//        // Increase likes for the post by 1
//        boolean success = postService.increaseLikes(postId, userId);
//
//        if (success) {
//            return new ResponseEntity<>("Like increased for post with ID: " + postId + " by user with ID: " + userId, HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>("Post not found with ID: " + postId, HttpStatus.NOT_FOUND);
//        }
//    }
}
