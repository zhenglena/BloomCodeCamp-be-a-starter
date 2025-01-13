package com.hcc.controllers;
import com.hcc.dtos.AssignmentCreateDto;
import com.hcc.dtos.AssignmentDto;
import com.hcc.entities.User;
import com.hcc.enums.AssignmentStatusEnum;
import com.hcc.exceptions.ResourceNotFoundException;
import com.hcc.exceptions.UnauthorizedAccessException;
import com.hcc.repositories.UserRepository;
import com.hcc.services.AssignmentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {
    @Autowired
    AssignmentService assignmentService;
    @Autowired
    UserRepository userRepository;

    /**
     * This will retrieve Assignments depending on the User and their authority level.
     * If a Learner is retrieving, then all Assignments under their name will be shown.
     * If a Reviewer is retrieving, it'll need a status to query.
     * @param userDetails The user logged in
     * @param status the status to be queried from the db
     * @return 204 No Content if List is empty, otherwise a 200 OK status
     */
    @GetMapping
    public ResponseEntity<?> getAssignmentsByUser(@AuthenticationPrincipal UserDetails userDetails,
                                                  @RequestParam(name = "status", required = false) String status) {
        List<AssignmentDto> dtoList = new ArrayList<>();

        User user = checkUser(userDetails);
        //If User is LEARNER
        if (user.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_LEARNER"))) {
            dtoList = assignmentService.getAssignmentsByLearner(user);
        }

        //If User is REVIEWER
        if (user.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_REVIEWER"))) {
            if (!status.isEmpty()) {
                dtoList = assignmentService.getAssignmentsByStatus(user, status);
            }
        }

        if (dtoList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(dtoList);

    }

    /**
     * This will retrieve assignments by their ID.
     * @param id the ID of the Assignment
     * @return 200 OK status when retrieved
     */
    @GetMapping("{id}")
    public ResponseEntity<?> getAssignmentById(@PathVariable("id") Long id) {
        AssignmentDto dto = assignmentService.getAssignmentById(id);
        return ResponseEntity.ok(dto);
    }

    /**
     * This will update Assignments according to the User's authority.
     * @param updateDto The assignmentDTO with updated fields
     * @param id the ID of the Assignment to be updated
     * @param userDetails the User logged on
     * @return 200 OK status
     */
    @PutMapping("{id}")
    public ResponseEntity<?> updateAssignmentById(@RequestBody AssignmentDto updateDto,
                                                  @PathVariable("id") Long id,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        User user = checkUser(userDetails);
        AssignmentDto dto = assignmentService.updateAssignmentById(updateDto, id, user);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    /**
     * This will be used by Learners ONLY to create assignments.
     * @param createDto The DTO with specific fields for the Learner to input
     * @param userDetails The logged on User
     * @return 201 Created status
     */
    @PostMapping
    public ResponseEntity<?> createAssignment(@RequestBody AssignmentCreateDto createDto,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        User user = checkUser(userDetails);
        AssignmentDto dto = assignmentService.createAssignment(createDto, user);

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    /**
     * This is a helper method to authenticate UserDetails
     * @param userDetails the user to be authenticated
     * @return the User
     */
    private User checkUser(UserDetails userDetails) {
        if (userDetails == null) {
            throw new UnauthorizedAccessException("Authentication required. User is not authenticated");
        }
        Optional<User> userOptional = userRepository.findByUsername(userDetails.getUsername());
        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }

        return userOptional.get();
    }
}
