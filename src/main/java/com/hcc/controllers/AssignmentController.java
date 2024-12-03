package com.hcc.controllers;
import com.hcc.dtos.AssignmentResponseDto;
import com.hcc.entities.Assignment;
import com.hcc.entities.User;
import com.hcc.exceptions.ResourceNotFoundException;
import com.hcc.repositories.UserRepository;
import com.hcc.services.AssignmentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> getAssignmentsByUserId(@RequestBody Long userId) {
        List<AssignmentResponseDto> dtoList = assignmentService.getAssignmentsByUserId(userId);
        if (dtoList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getAssignmentById(@PathVariable("id") Long id) {
        AssignmentResponseDto dto = assignmentService.getAssignmentById(id);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("{id}")
    public ResponseEntity<?> putAssignmentById(@RequestBody Assignment assignment, @PathVariable("id") Long id,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required. User is not " +
                    "authenticated");
        }
        Optional<User> user = userRepository.findByUsername(userDetails.getUsername());
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found or unauthorized");
        }
        AssignmentResponseDto dto = assignmentService.putAssignmentById(assignment, id, user.get());
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<?> postAssignment(@RequestBody Assignment assignment) {
        AssignmentResponseDto dto = assignmentService.postAssignment(assignment);
        if (dto == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }
}
