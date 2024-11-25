package com.hcc.controllers;
import com.hcc.dtos.AssignmentResponseDto;
import com.hcc.entities.Assignment;
import com.hcc.entities.User;
import com.hcc.services.AssignmentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {
    @Autowired
    AssignmentService assignmentService;

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
    public ResponseEntity<?> putAssignmentById(@RequestBody Assignment assignment, @PathVariable("id") Long id, User user) {
        AssignmentResponseDto dto = assignmentService.putAssignmentById(assignment, id, user);
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
