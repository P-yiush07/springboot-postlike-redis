package com.postbackend.PostWorking.repository;

import com.postbackend.PostWorking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Long> {

}
