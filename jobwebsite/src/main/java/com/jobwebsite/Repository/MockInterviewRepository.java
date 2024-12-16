package com.jobwebsite.Repository;

import com.jobwebsite.Entity.MockInterview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MockInterviewRepository extends JpaRepository<MockInterview, Long> {
}
