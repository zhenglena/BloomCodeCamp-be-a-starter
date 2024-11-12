package com.hcc.services;

import com.hcc.entities.Assignment;
import com.hcc.entities.User;
import com.hcc.exceptions.ResourceNotFoundException;
import com.hcc.exceptions.UnauthorizedUpdateException;
import com.hcc.repositories.AssignmentRepository;
import com.hcc.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private UserService userService;
    /**
     * Retrieves a List of Assignments by User
     * @param userId the username of the user
     * @return List of Assignments if it exists. If it doesn't, it'll return an empty list.
     */
    public List<Assignment> getAssignmentsByUserId(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        Optional<List<Assignment>> assignments;
        if (userOptional.isPresent()) {
             assignments = assignmentRepository.findByUserId(userId);
        } else {
            throw new ResourceNotFoundException("User with ID: " + userId + "does not exist.");
        }
        return assignments.orElseGet(ArrayList::new);
    }

    /**
     * Retrieves an Assignment by its ID
     * @param id the ID of the assignment
     * @return the Assignment
     */
    public Assignment getAssignmentById(Long id) {
        return assignmentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Assignment not " +
                "found with ID: " + id));
    }

    /**
     * Updates the Assignment
     * @param assignment the updated Assignment
     * @param id the ID of the Assignment to update
     * @return updated Assignment
     */
    public Assignment putAssignmentById(Assignment assignment, Long id) {
        //TODO: finish this
        if (!Objects.equals(assignment.getId(), id)) {
            throw new UnauthorizedUpdateException("Updated assignment ID " + assignment.getId() +
                    " does not match argument: " + id);
        }

        return assignment;
    }

    /**
     * Posts a new Assignment
     * @param assignment the new Assignment
     * @return the added Assignment
     */
    public Assignment postAssignment(Assignment assignment) {
        return assignmentRepository.save(assignment);
    }
}
