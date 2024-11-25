package com.hcc.services;

import com.hcc.dtos.AssignmentResponseDto;
import com.hcc.entities.Assignment;
import com.hcc.entities.User;
import com.hcc.exceptions.ResourceNotFoundException;
import com.hcc.exceptions.UnauthorizedUpdateException;
import com.hcc.mappers.AssignmentMapper;
import com.hcc.repositories.AssignmentRepository;
import com.hcc.repositories.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
public class AssignmentService {
    @Autowired
    private AssignmentRepository assignmentRepository;
    @Autowired
    private UserRepository userRepository;

    private final AssignmentMapper mapper = new AssignmentMapper();

    private final Logger log = LogManager.getLogger(AssignmentService.class);

    /**
     * Retrieves the list of Assignments that are associated with a user ID.
     * First checks if the user exists. If user does not exist, then it throws a ResourceNotFoundException.
     * if user does exist, then it will retrieve the list of assignments for that user.
     * If the list comes back empty, then ResponseEntity will return a 204 No Content.
     * @param userId the user ID of where to retrieve the assignments
     * @return 200 OK response with List of Assignments.
     */
    public List<AssignmentResponseDto> getAssignmentsByUserId(Long userId) {
        boolean exists = userRepository.findById(userId).isPresent();
        if (!exists) {
            log.error("User ID does not exist");
            throw new ResourceNotFoundException("User with ID " + userId + " does not exist");
        }

        List<Assignment> assignments = assignmentRepository.findByUserId(userId);
        if (assignments.isEmpty()) {
            log.info("No assignments found for user");
        }

        return mapper.toDtoList(assignments);
    }

    /**
     * Retrieves an Assignment by its ID. If the assignment does not exist, then ResourceNotFoundException will be thrown.
     * @param id the ID of the assignment
     * @return 200 OK response with Assignment
     */
    public AssignmentResponseDto getAssignmentById(Long id) {
        Optional<Assignment> assignmentOptional = assignmentRepository.findById(id);
        if (assignmentOptional.isEmpty()) {
            log.error("Assignment doesn't exist");
            throw new ResourceNotFoundException("Assignment with ID " + id + " does not exist");
        } else {
            return mapper.toDto(assignmentOptional.get());
        }
    }


    public AssignmentResponseDto putAssignmentById(Assignment updatedAssignment, Long id, User user) {
        if (!Objects.equals(updatedAssignment.getId(), id)) {
            log.error("Updated assignment ID does not match ID provided");
            throw new UnauthorizedUpdateException("Updated assignment ID " + updatedAssignment.getId() +
                    " does not match argument: " + id);
        }

        Assignment assignment = mapper.toAssignment(getAssignmentById(id));
        assignment.setId(id);

        if (user.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            updateAdminFields(assignment, updatedAssignment);
        }

        if (user.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_REVIEWER"))) {
            updateReviewerFields(assignment, updatedAssignment);
        }

        if (user.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_LEARNER"))) {
            updateLearnerFields(assignment, updatedAssignment);

        }

        assignmentRepository.save(assignment);
        return mapper.toDto(assignment);
    }

    /**
     * Posts a new Assignment
     * @param assignment the new Assignment
     * @return the added Assignment
     */
    public AssignmentResponseDto postAssignment(Assignment assignment) {
        try {
            assignmentRepository.save(assignment);
        } catch (IllegalArgumentException e) {
            log.error("Empty body in the assignment: ", e);
            return null;
        }
        return mapper.toDto(assignment);
    }

    /**
     * Allows Admin roles to update everything on an assignment other than ID
     * @param assignment the assignment to update
     * @param updatedAssignment the assignment with updated fields
     */
    private void updateAdminFields(Assignment assignment, Assignment updatedAssignment) {
        if (updatedAssignment.getBranch() != null) {
            assignment.setBranch(updatedAssignment.getBranch());
        }
        if (updatedAssignment.getCodeReviewer() != null) {
            assignment.setCodeReviewer(updatedAssignment.getCodeReviewer());
        }
        if (updatedAssignment.getNumber() != null) {
            assignment.setNumber(updatedAssignment.getNumber());
        }
        if (updatedAssignment.getGithubUrl() != null) {
            assignment.setGithubUrl(updatedAssignment.getGithubUrl());
        }
        if (updatedAssignment.getStatus() != null) {
            assignment.setStatus(updatedAssignment.getStatus());
        }
        if (updatedAssignment.getReviewVideoUrl() != null) {
            assignment.setReviewVideoUrl(updatedAssignment.getReviewVideoUrl());
        }
        if (updatedAssignment.getUser() != null) {
            assignment.setUser(updatedAssignment.getUser());
        }
    }

    /**
     * Allows Reviewer roles to update the Code Reviewer (attaching their name to the assignment), the status of the
     * assignment, or update the review video URL.
     * @param assignment the assignment to update
     * @param updatedAssignment the assignment with updated fields
     */
    private void updateReviewerFields(Assignment assignment, Assignment updatedAssignment) {
        if (updatedAssignment.getCodeReviewer() != null) {
            assignment.setCodeReviewer(updatedAssignment.getCodeReviewer());
        }
        if (updatedAssignment.getStatus() != null) {
            assignment.setStatus(updatedAssignment.getStatus());
        }
        if (updatedAssignment.getReviewVideoUrl() != null) {
            assignment.setReviewVideoUrl(updatedAssignment.getReviewVideoUrl());
        }
    }

    /**
     * Allows Learner roles to update only the branch or the GitHub URL
     * @param assignment the assignment to update
     * @param updatedAssignment the assignment with updated fields
     */
    private void updateLearnerFields(Assignment assignment, Assignment updatedAssignment) {
        if (updatedAssignment.getBranch() != null) {
            assignment.setBranch(updatedAssignment.getBranch());
        }
        if (updatedAssignment.getGithubUrl() != null) {
            assignment.setGithubUrl(updatedAssignment.getGithubUrl());
        }
    }
}
