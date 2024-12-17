package com.hcc.repositories;

import com.hcc.entities.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    //learner assignments
    List<Assignment> findByUserId(Long learnerId);

    //reviewer assignments for assignments previously rejected
    List<Assignment> findByReviewerIdAndStatus(Long reviewerId, String status);

    //reviewer assignments for any assignments with SUBMITTED status (not necessarily claimed)
    List<Assignment> findByStatus(String status);
}
