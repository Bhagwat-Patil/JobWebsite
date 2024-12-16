package com.jobwebsite.ServiceImpl;

import com.jobwebsite.Entity.MockInterview;
import com.jobwebsite.Repository.MockInterviewRepository;
import com.jobwebsite.Service.MockInterviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MockInterviewServiceImpl implements MockInterviewService {

    private static final Logger log = LoggerFactory.getLogger(MockInterviewServiceImpl.class);

    private final MockInterviewRepository mockInterviewRepository;

    public MockInterviewServiceImpl(MockInterviewRepository mockInterviewRepository) {
        this.mockInterviewRepository = mockInterviewRepository;
    }

    @Override
    public MockInterview createMockInterview(MockInterview mockInterview) {
        log.info("Attempting to create a new MockInterview: {}", mockInterview);
        try {
            MockInterview savedMockInterview = mockInterviewRepository.save(mockInterview);
            log.info("MockInterview created successfully with ID: {}", savedMockInterview.getId());
            return savedMockInterview;
        } catch (Exception e) {
            log.error("Error occurred while creating MockInterview: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public MockInterview getMockInterviewById(Long id) {
        log.info("Fetching MockInterview by ID: {}", id);
        return mockInterviewRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("MockInterview not found with ID: {}", id);
                    return new RuntimeException("MockInterview not found with ID: " + id);
                });
    }

    @Override
    public List<MockInterview> getAllMockInterviews() {
        log.info("Fetching all MockInterviews...");
        try {
            List<MockInterview> mockInterviews = mockInterviewRepository.findAll();
            log.info("Successfully fetched {} MockInterviews.", mockInterviews.size());
            return mockInterviews;
        } catch (Exception e) {
            log.error("Error occurred while fetching MockInterviews: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public MockInterview updateMockInterview(Long id, MockInterview mockInterview) {
        log.info("Attempting to update MockInterview with ID: {}", id);
        try {
            MockInterview existingMockInterview = getMockInterviewById(id);
            existingMockInterview.setText(mockInterview.getText());
            existingMockInterview.setHyperlink(mockInterview.getHyperlink());
            MockInterview updatedMockInterview = mockInterviewRepository.save(existingMockInterview);
            log.info("MockInterview updated successfully with ID: {}", updatedMockInterview.getId());
            return updatedMockInterview;
        } catch (Exception e) {
            log.error("Error occurred while updating MockInterview with ID: {}: {}", id, e.getMessage());
            throw e;
        }
    }

    @Override
    public void deleteMockInterview(Long id) {
        log.info("Attempting to delete MockInterview with ID: {}", id);
        try {
            MockInterview mockInterview = getMockInterviewById(id);
            mockInterviewRepository.delete(mockInterview);
            log.info("MockInterview deleted successfully with ID: {}", id);
        } catch (Exception e) {
            log.error("Error occurred while deleting MockInterview with ID: {}: {}", id, e.getMessage());
            throw e;
        }
    }
}
