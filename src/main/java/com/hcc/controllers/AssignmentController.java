package com.hcc.controllers;
import com.hcc.entities.Assignment;
import com.hcc.services.AssignmentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AssignmentController {
    @Autowired
    AssignmentService assignmentService;

    @GetMapping("/api/assignments")
    public List<Assignment> getAssignmentsByUser(Long userId) {
        return assignmentService.getAssignmentsByUserId(userId);
    }

    @GetMapping("/api/assignments/{id}")
    public Assignment getAssignmentById(@PathVariable("id") Long id) {
        return assignmentService.getAssignmentById(id);
    }

    @PutMapping("/api/assignments/{id}")
    public Assignment putAssignmentById(@RequestBody Assignment assignment, @PathVariable("id") Long id) {
        return assignmentService.putAssignmentById(assignment, id);
    }

    @PostMapping("/api/assignments")
    public Assignment postAssignment(@RequestBody Assignment assignment) {
        return assignmentService.postAssignment(assignment);
    }
}
