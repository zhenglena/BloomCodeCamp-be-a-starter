package com.hcc.mappers;

import com.hcc.dtos.AssignmentResponseDto;
import com.hcc.entities.Assignment;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.stream.Collectors;

public class AssignmentMapper {

    public AssignmentResponseDto toDto(Assignment assignment) {
        AssignmentResponseDto dto = new AssignmentResponseDto();
        dto.setBranch(assignment.getBranch());
        dto.setUser(assignment.getUser());
        dto.setCodeReviewer(assignment.getCodeReviewer());
        dto.setStatus(assignment.getStatus());
        dto.setGithubUrl(assignment.getGithubUrl());
        dto.setReviewVideoUrl(assignment.getReviewVideoUrl());
        dto.setNumber(assignment.getNumber());

        return dto;
    }

    public Assignment toAssignment(AssignmentResponseDto dto) {
        return new Assignment(
                dto.getStatus(),
                dto.getNumber(),
                dto.getGithubUrl(),
                dto.getBranch(),
                dto.getReviewVideoUrl(),
                dto.getUser(),
                dto.getCodeReviewer()
        );
    }

    public List<AssignmentResponseDto> toDtoList(List<Assignment> assignments) {
        return assignments.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<Assignment> toAssignmentList(List<AssignmentResponseDto> dtos) {
        return dtos.stream()
                .map(this::toAssignment)
                .collect(Collectors.toList());
    }
}
