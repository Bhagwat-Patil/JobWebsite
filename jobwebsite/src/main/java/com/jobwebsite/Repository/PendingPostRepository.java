package com.jobwebsite.Repository;

import com.jobwebsite.Entity.PendingPost;
import com.jobwebsite.Entity.PostType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PendingPostRepository extends JpaRepository<PendingPost, Long> {
    List<PendingPost> findAllByType(PostType type);
}