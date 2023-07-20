package com.anshul.jobprocessing.service;

import com.anshul.jobprocessing.config.RemoteServiceConfig;
import com.anshul.jobprocessing.entity.JobDetails;
import com.anshul.jobprocessing.repository.JobDetailsRepo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Random;

import static com.anshul.jobprocessing.constants.GeneralConstants.*;

@Slf4j
@Service
public class JobRunnerService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private JobDetailsRepo jobDetailsRepo;

    @Autowired
    private RemoteServiceConfig remoteServiceConfig;

    /**
     * This method will Execute the jobs
     */
    public void executeJob() {
        try {
            String actionId = sendTriggerRequest();
            if (!actionId.equals(ERROR)) {
                JobDetails jobDetails = new JobDetails();
                jobDetails.setActionId(actionId);
                jobDetails.setStatus(IN_PROGRESS);
                jobDetailsRepo.save(jobDetails);
                log.info("Job created");
            }

            List<JobDetails> jobList = jobDetailsRepo.findByStatus(IN_PROGRESS);
            if (!jobList.isEmpty()) {
                log.info("Jobs already exists count " + jobList.size());

                jobList.forEach(job -> {
                    try {
                        String actionStatus = getActionStatus(job.getActionId());
                        String response = null;
                        switch (actionStatus) {
                            case IN_PROGRESS:
                                log.info("Action is still in progress.");
                                break;
                            case COMPLETED:
                                response = getActionResponse(job.getActionId());
                                if (response.equals(ERROR)) {
                                    actionStatus = IN_PROGRESS;
                                    log.error("Error while fetching response for action: " + job.getActionId());
                                    break;
                                }
                                log.info("Action Completed with response: " + response);
                                break;
                            case FAILED:
                                log.info("Action failed.");
                                break;
                            case ERROR:
                                log.info("Error occurred in action status API.");
                                actionStatus = IN_PROGRESS;
                                break;
                            default:
                                log.error("Unknown action status: " + actionStatus);
                                break;
                        }
                        job.setStatus(actionStatus);
                        job.setResponse(response);
                        jobDetailsRepo.save(job);
                    } catch (Exception e) {
                        log.error("Error while processing job: " + job.getActionId(), e);
                    }
                });
            }
        } catch (Exception e) {
            log.error("Error while executing jobs: ", e);
        }
    }

    /**
     * Step 1: Send an HTTP request to trigger the action on the remote service
     */
    private String sendTriggerRequest() {
//        return RandomStringUtils.randomAlphabetic(8);
        try {
            return restTemplate.exchange(remoteServiceConfig.getTriggerActionUrl(), HttpMethod.GET, HttpEntity.EMPTY, String.class).getBody();
        } catch (Exception e) {
            log.error("Error while sending trigger request: ", e);
            return ERROR;
        }
    }

    /**
     * Step 2: Get the status of the request
     */
    private String getActionStatus(String actionId) {
//        int rand = new Random().nextInt(4);
//        return rand == 0 ? COMPLETED : rand == 1 ? FAILED : rand == 2 ? IN_PROGRESS : ERROR;
        String url = remoteServiceConfig.getActionStatusUrl().replaceFirst("<actionId>", actionId);
        try {
            return restTemplate.exchange(url, HttpMethod.GET, HttpEntity.EMPTY, String.class).getBody();
        } catch (Exception e) {
            log.error("Error while getting action status: ", e);
            return ERROR;
        }
    }

    /**
     * Step 3: Get the response for completed request
     */
    private String getActionResponse(String actionId) {
//        return "Action response for " + actionId;
        String url = remoteServiceConfig.getActionResponseUrl().replaceFirst("<actionId>", actionId);
        try {
            return restTemplate.exchange(url, HttpMethod.GET, HttpEntity.EMPTY, String.class).getBody();
        } catch (Exception e) {
            log.error("Error while getting action response: ", e);
            return ERROR;
        }
    }
}
