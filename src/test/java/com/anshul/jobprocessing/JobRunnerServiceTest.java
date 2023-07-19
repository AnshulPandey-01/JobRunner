package com.anshul.jobprocessing;

import com.anshul.jobprocessing.entity.JobDetails;
import com.anshul.jobprocessing.repository.JobDetailsRepo;
import com.anshul.jobprocessing.service.JobRunnerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

import static com.anshul.jobprocessing.constants.GeneralConstants.*;

@AutoConfigureMockMvc
@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class JobRunnerServiceTest {

    @InjectMocks
    private JobRunnerService jobRunnerService;

    @Mock
    private JobDetailsRepo jobDetailsRepo;

    @Spy
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    private String actionId;

    private String triggerActionUrl;
    private String actionStatusUrl;
    private String actionResponseUrl;

    @BeforeEach
    void setBefore(){
        mockServer = MockRestServiceServer.createServer(restTemplate);
        actionId = "abc123";
        triggerActionUrl = "http://localhost:8081/trigger-action";
        actionStatusUrl = "http://localhost:8081/abc123/action-status";
        actionResponseUrl = "http://localhost:8081/abc123/action-response";
    }

    @Test
    @DisplayName("Validate job creation, save and status check")
    void testExecuteForJobCreation() {
        JobDetails jobInProgress = new JobDetails();
        jobInProgress.setActionId(actionId);
        jobInProgress.setStatus(IN_PROGRESS);

        // step 1
        mockServer.expect(requestTo(triggerActionUrl)).andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK).headers(new HttpHeaders()).body(actionId));
        when(jobDetailsRepo.save(any(JobDetails.class))).thenReturn(jobInProgress);
        when(jobDetailsRepo.findByStatus(IN_PROGRESS)).thenReturn(List.of(jobInProgress));
        // step 2
        mockServer.expect(requestTo(actionStatusUrl)).andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK).headers(new HttpHeaders()).body(IN_PROGRESS));

        jobRunnerService.executeJob();

        mockServer.verify();
        verify(jobDetailsRepo, times(2)).save(argThat(jobDetails ->
                jobDetails.getActionId().equals(actionId) && jobDetails.getStatus().equals("IN_PROGRESS")
        ));
    }

    @Test
    @DisplayName("Validate for completed")
    public void testExecuteForCompletedJob() {
        JobDetails jobInProgress = new JobDetails();
        jobInProgress.setActionId(actionId);
        jobInProgress.setStatus(IN_PROGRESS);

        JobDetails jobCompleted = new JobDetails();
        jobCompleted.setStatus(COMPLETED);
        jobCompleted.setResponse("Task Completed");

        // step 1
        mockServer.expect(requestTo(triggerActionUrl)).andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK).headers(new HttpHeaders()).body(actionId));
        when(jobDetailsRepo.save(any(JobDetails.class))).thenReturn(jobInProgress).thenReturn(jobCompleted);
        when(jobDetailsRepo.findByStatus(IN_PROGRESS)).thenReturn(List.of(jobInProgress));

        // step 2
        mockServer.expect(requestTo(actionStatusUrl)).andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK).headers(new HttpHeaders()).body(COMPLETED));

        // step 3
        mockServer.expect(requestTo(actionResponseUrl)).andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK).headers(new HttpHeaders()).body("Task Completed"));

        jobRunnerService.executeJob();

        mockServer.verify();
        verify(jobDetailsRepo, times(1)).save(argThat(jobDetails ->
                jobDetails.getActionId().equals(actionId) && jobDetails.getStatus().equals(IN_PROGRESS)
        ));
        verify(jobDetailsRepo, times(1)).save(argThat(jobDetails ->
                jobDetails.getActionId().equals(actionId) && jobDetails.getStatus().equals(COMPLETED) && jobDetails.getResponse().equals("Task Completed")
        ));
    }

}
