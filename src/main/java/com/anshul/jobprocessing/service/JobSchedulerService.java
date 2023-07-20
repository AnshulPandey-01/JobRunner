package com.anshul.jobprocessing.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@EnableScheduling
public class JobSchedulerService {

    @Autowired
    private JobRunnerService jobRunner;

    /**
     * Scheduler will run the job every five minute
     */
    @Scheduled(cron = "0 0/5 * * * *", zone = "GMT+05:30")
    public void runJob() {
        log.info("Running the job...");
        jobRunner.executeJob();
    }
}
