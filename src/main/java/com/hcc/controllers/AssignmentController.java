package com.hcc.controllers;
import com.hcc.entities.Assignment;
import com.hcc.entities.User;
import com.hcc.services.AssignmentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {
    @Autowired
    AssignmentService assignmentService;

    @GetMapping
    public ResponseEntity<?> getAssignmentsByUser(Long userId) {
        return assignmentService.getAssignmentsByUserId(userId);
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getAssignmentById(@PathVariable("id") Long id) {
        return assignmentService.getAssignmentById(id);
    }

    @PutMapping("{id}")
    public ResponseEntity<?> putAssignmentById(@RequestBody Assignment assignment, @PathVariable("id") Long id, User user) {
        return assignmentService.putAssignmentById(assignment, id, user);
    }

    @PostMapping
    public ResponseEntity<?> postAssignment(@RequestBody Assignment assignment) {
        return assignmentService.postAssignment(assignment);
    }
}
