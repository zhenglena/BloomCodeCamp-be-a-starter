package com.hcc.controllers;
import com.hcc.dtos.AssignmentCreateDto;
import com.hcc.dtos.AssignmentDto;
import com.hcc.entities.User;
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
            dtoList = assignmentService.getAssignmentsByStatus(user, status);
        }

        if (dtoList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(dtoList);

    }

    @GetMapping("{id}")
    public ResponseEntity<?> getAssignmentById(@PathVariable("id") Long id) {
        AssignmentDto dto = assignmentService.getAssignmentById(id);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateAssignmentById(@RequestBody AssignmentDto updateDto,
                                                  @PathVariable("id") Long id,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        User user = checkUser(userDetails);
        AssignmentDto dto = assignmentService.updateAssignmentById(updateDto, id, user);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @PostMapping
    public ResponseEntity<?> createAssignment(@RequestBody AssignmentCreateDto createDto,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        User user = checkUser(userDetails);
        AssignmentDto dto = assignmentService.createAssignment(createDto, user);
        if (dto == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

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
