package com.hcc.services;

import com.hcc.entities.Assignment;
import com.hcc.entities.User;
import com.hcc.exceptions.ResourceNotFoundException;
import com.hcc.exceptions.UnauthorizedUpdateException;
import com.hcc.repositories.AssignmentRepository;
import com.hcc.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class AssignmentService {
    @Autowired
    private AssignmentRepository assignmentRepository;
    @Autowired
    private UserRepository userRepository;

    /**
     * Retrieves the list of Assignments that are associated with a user ID.
     * First checks if the user exists. If user does not exist, then it throws a ResourceNotFoundException.
     * if user does exist, then it will retrieve the list of assignments for that user.
     * If the list comes back empty, then ResponseEntity will return a 204 No Content.
     * @param userId the user ID of where to retrieve the assignments
     * @return 200 OK response with List of Assignments.
     */
    public ResponseEntity<?> getAssignmentsByUserId(Long userId) {
        boolean exists = userRepository.findById(userId).isPresent();
        if (!exists) {
            throw new ResourceNotFoundException("User with ID " + userId + "does not exist.");
        }

        List<Assignment> assignments = assignmentRepository.findByUserId(userId);
        if (assignments.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(assignments);
        }
    }

    /**
     * Retrieves an Assignment by its ID. If the assignment does not exist, then ResourceNotFoundException will be thrown.
     * @param id the ID of the assignment
     * @return 200 OK response with Assignment
     */
    public ResponseEntity<Assignment> getAssignmentById(Long id) {
        Optional<Assignment> assignmentOptional = assignmentRepository.findById(id);
        return assignmentOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }


    public ResponseEntity<?> putAssignmentById(Assignment updatedAssignment, Long id, User user) {
        if (!Objects.equals(updatedAssignment.getId(), id)) {
            throw new UnauthorizedUpdateException("Updated assignment ID " + updatedAssignment.getId() +
                    " does not match argument: " + id);
        }
        Optional<Assignment> optionalAssignment = assignmentRepository.findById(id);
        if (optionalAssignment.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Assignment not found with ID: " + id);
        }
        Assignment assignment = optionalAssignment.get();

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
        return ResponseEntity.ok(assignment);
    }

    /**
     * Posts a new Assignment
     * @param assignment the new Assignment
     * @return the added Assignment
     */
    public ResponseEntity<?> postAssignment(Assignment assignment) {
        try {
            assignmentRepository.save(assignment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(assignment);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(assignment);
    }

    /**
     * Allows Admin roles to update everything on an assignment other than ID
     * @param assignment the assignment to update
     * @param updatedAssignment the assignment with updated fields
     */
    private void updateAdminFields(Assignment assignment, Assignment updatedAssignment) {
        if (!updatedAssignment.getBranch().equals(assignment.getBranch())) {
            assignment.setBranch(updatedAssignment.getBranch());
        }
        if (!updatedAssignment.getCodeReviewer().equals(assignment.getCodeReviewer())) {
            assignment.setCodeReviewer(updatedAssignment.getCodeReviewer());
        }
        if (!updatedAssignment.getNumber().equals(assignment.getNumber())) {
            assignment.setNumber(updatedAssignment.getNumber());
        }
        if (!updatedAssignment.getGithubUrl().equals(assignment.getGithubUrl())) {
            assignment.setGithubUrl(updatedAssignment.getGithubUrl());
        }
        if (!updatedAssignment.getStatus().equals(assignment.getStatus())) {
            assignment.setStatus(updatedAssignment.getStatus());
        }
        if (!updatedAssignment.getReviewVideoUrl().equals(assignment.getReviewVideoUrl())) {
            assignment.setReviewVideoUrl(updatedAssignment.getReviewVideoUrl());
        }
        if (!updatedAssignment.getUser().equals(assignment.getUser())) {
            assignment.setUser(updatedAssignment.getUser());
        }
    }

    /**
     * Allows Reviewer roles to update only the Code Reviewer (attaching their name to the assignment)
     * or the status of the assignment
     * @param assignment the assignment to update
     * @param updatedAssignment the assignment with updated fields
     */
    private void updateReviewerFields(Assignment assignment, Assignment updatedAssignment) {
        if (!updatedAssignment.getCodeReviewer().equals(assignment.getCodeReviewer())) {
            assignment.setCodeReviewer(updatedAssignment.getCodeReviewer());
        }
        if (!updatedAssignment.getStatus().equals(assignment.getStatus())) {
            assignment.setStatus(updatedAssignment.getStatus());
        }
    }

    /**
     * Allows Learner roles to update only the branch or the GitHub URL
     * @param assignment the assignment to update
     * @param updatedAssignment the assignment with updated fields
     */
    private void updateLearnerFields(Assignment assignment, Assignment updatedAssignment) {
        if (!updatedAssignment.getBranch().equals(assignment.getBranch())) {
            assignment.setBranch(updatedAssignment.getBranch());
        }
        if (!updatedAssignment.getGithubUrl().equals(assignment.getGithubUrl())) {
            assignment.setGithubUrl(updatedAssignment.getGithubUrl());
        }
    }
}
