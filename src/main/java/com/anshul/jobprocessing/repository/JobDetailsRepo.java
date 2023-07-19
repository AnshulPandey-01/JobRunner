package com.anshul.jobprocessing.repository;

import com.anshul.jobprocessing.entity.JobDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobDetailsRepo extends JpaRepository<JobDetails, Long> {

    Optional<JobDetails> findByActionId(String actionId);

    List<JobDetails> findByStatus(String status);

}
