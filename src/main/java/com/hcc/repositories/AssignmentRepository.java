package com.hcc.repositories;

import com.hcc.entities.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    //learner assignments
    List<Assignment> findByUserId(Long learnerId);

    //query assignments associated with a Reviewer and a provided status
    List<Assignment> findByCodeReviewerIdAndStatus(Long reviewerId, String status);

    //query assignments by status
    List<Assignment> findByStatus(String status);
}
