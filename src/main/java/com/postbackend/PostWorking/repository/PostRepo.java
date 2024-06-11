package com.postbackend.PostWorking.repository;

import com.postbackend.PostWorking.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepo extends JpaRepository<Post, Long> {
}
