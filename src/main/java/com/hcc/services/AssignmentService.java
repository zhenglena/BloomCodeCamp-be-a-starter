package com.hcc.services;

import com.hcc.dtos.AssignmentCreateDto;
import com.hcc.dtos.AssignmentDto;
import com.hcc.entities.Assignment;
import com.hcc.entities.User;
import com.hcc.enums.AssignmentStatusEnum;
import com.hcc.enums.AuthorityEnum;
import com.hcc.exceptions.ResourceNotFoundException;
import com.hcc.exceptions.UnauthorizedAccessException;
import com.hcc.mappers.AssignmentMapper;
import com.hcc.repositories.AssignmentRepository;
import com.hcc.repositories.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class AssignmentService {
    @Autowired
    private AssignmentRepository assignmentRepository;
    @Autowired
    private UserRepository userRepository;

    private AssignmentMapper mapper = Mappers.getMapper(AssignmentMapper.class);

    private final Logger log = LogManager.getLogger(AssignmentService.class);

    /**
     * GET ENDPOINT for Learners
     * Retrieves the list of Assignments that are associated with a Learner
     * If the list comes back empty, then ResponseEntity will return a 204 No Content.
     * @param learner the user of where to retrieve the assignments from
     * @return List of Assignment DTOs
     */
    public List<AssignmentDto> getAssignmentsByLearner(User learner) {
        log.info("Retrieving assignments with user: {}", learner);
        List<Assignment> assignments = assignmentRepository.findByUserId(learner.getId());

        if (assignments.isEmpty()) {
            log.info("No assignments found for user");
        }

        log.info("Successfully retrieved list of assignments");
        return mapper.toDtoList(assignments);
    }

    /**
     * GET ENDPOINT for Reviewers
     * Retrieves the List of Assignments by a status query (either SUBMITTED or RESUBMITTED).
     * If SUBMITTED, any assignments with status SUBMITTED will be returned.
     * IF RESUBMITTED, any assignments that is associated with the Reviewer and has the status RESUBMITTED will be
     * returned.
     * If the list comes back empty, then ResponseEntity will return a 204 No Content.
     * @param status the status that is being queried
     * @return List of Assignment DTOs that have this status
     */
    public List<AssignmentDto> getAssignmentsByStatus(User reviewer, String status) {
        List<Assignment> assignments = new ArrayList<>();
        if (status.equals(AssignmentStatusEnum.SUBMITTED.getStatus())) {
            log.info("Retrieving assignments with status: {}", status);
            assignments = assignmentRepository.findByStatus(status);
        }

        if (status.equals(AssignmentStatusEnum.RESUBMITTED.getStatus())) {
            log.info("Retrieving assignments with status: {} and claimed by reviewer: {}", status, reviewer);
            assignments = assignmentRepository.findByReviewerIdAndStatus(reviewer.getId(), status);
        }

        if (assignments.isEmpty()) {
            log.info("No unclaimed submitted assignments");
        }

        log.info("Returning assignments...");
        return mapper.toDtoList(assignments);
    }

    /**
     * GET ENDPOINT
     * Retrieves an Assignment by its ID. If the assignment does not exist, then ResourceNotFoundException will be thrown.
     * @param id the ID of the assignment
     * @return Assignment DTO
     */
    public AssignmentDto getAssignmentById(Long id) {
        log.info("Retrieving assignment with id: {}", id);
        Optional<Assignment> assignmentOptional = assignmentRepository.findById(id);
        if (assignmentOptional.isEmpty()) {
            log.error("Assignment with ID: {} doesn't exist", id);
            throw new ResourceNotFoundException("Assignment with ID " + id + " does not exist");
        } else {
            log.info("Returning assignment...");
            return mapper.toDto(assignmentOptional.get());
        }
    }


    /**
     * PUT ENDPOINT
     * Updates an Assignment using the Assignment ID and providing an Assignment with updated features. The User is
     * authenticated by Spring Security to determine what they can update. Saves the newly updated Assignment.
     * @param updateDto the AssignmentDTO with updated fields
     * @param id the Assignment ID
     * @param user the User updating the Assignment
     * @return an Updated Assignment DTO
     */
    public AssignmentDto updateAssignmentById(AssignmentDto updateDto, Long id, User user) {
        Assignment assignment = mapper.toAssignment(getAssignmentById(id));
        assignment.setId(id);

        if (user.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_REVIEWER"))) {
            mapper.updateReviewerFields(updateDto, assignment);

            if (updateDto.getReviewVideoUrl() != null && updateDto.getStatus().equals(AssignmentStatusEnum.COMPLETED.getStatus())) {
                log.info("Updating review video url field if assignment is marked completed");
                assignment.setReviewVideoUrl(updateDto.getReviewVideoUrl());
            }

        }

        if (user.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_LEARNER"))) {
            mapper.updateLearnerFields(updateDto, assignment);

            if (assignment.getStatus().equals(AssignmentStatusEnum.NEEDS_UPDATE.getStatus()) && updateDto.getBranch() != null
                    && updateDto.getGithubUrl() != null) {
                log.info("Updating Branch, GitHub url, and changing status from NEEDS_UPDATE to RESUBMITTED");
                assignment.setBranch(updateDto.getBranch());
                assignment.setGithubUrl(updateDto.getGithubUrl());
                assignment.setStatus(AssignmentStatusEnum.RESUBMITTED.getStatus());
            }

            if (updateDto.getStatus().equals(AssignmentStatusEnum.SUBMITTED.getStatus()) && assignment.getStatus().equals(
                    AssignmentStatusEnum.PENDING_SUBMISSION.getStatus())) {
                log.info("Updating status from PENDING_SUBMISSION to SUBMITTED");
                assignment.setStatus(AssignmentStatusEnum.SUBMITTED.getStatus());
            }

        }
        log.info("Successfully updated assignment with updated fields");
        assignmentRepository.save(assignment);
        log.info("Saved assignment");
        return mapper.toDto(assignment);
    }

    /**
     * POST ENDPOINT
     * Posts a new Assignment
     * @param createDto the new Assignment
     * @return the added Assignment
     */
    public AssignmentDto createAssignment(AssignmentCreateDto createDto, User user) {
        boolean isLearner = user.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(AuthorityEnum.ROLE_LEARNER.name()));
        if (!isLearner) {
            log.error("User is not permitted to create Assignments");
            throw new UnauthorizedAccessException("Only learners can create assignments");
        }
        log.info("Assigning proper fields to created Assignment");
        Assignment assignment = mapper.toAssignment(createDto);
        assignment.setUser(user);
        assignment.setStatus(AssignmentStatusEnum.PENDING_SUBMISSION.getStatus());

        assignmentRepository.save(assignment);

        return mapper.toDto(assignment);
    }
}
