package com.jobwebsite.Repository;

import com.jobwebsite.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserName(String userName);
    User findByEmailId(String emailId);
}