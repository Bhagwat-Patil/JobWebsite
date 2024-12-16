package com.jobwebsite.Service;

import com.jobwebsite.Entity.MockInterview;
import java.util.List;

public interface MockInterviewService {
    MockInterview createMockInterview(MockInterview mockInterview);
    MockInterview getMockInterviewById(Long id);
    List<MockInterview> getAllMockInterviews();
    MockInterview updateMockInterview(Long id, MockInterview mockInterview);
    void deleteMockInterview(Long id);
}