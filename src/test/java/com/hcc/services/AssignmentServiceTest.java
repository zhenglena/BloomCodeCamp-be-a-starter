package com.hcc.services;

import com.hcc.dtos.AssignmentResponseDto;
import com.hcc.entities.Assignment;
import com.hcc.entities.Authority;
import com.hcc.entities.User;
import com.hcc.enums.AssignmentStatusEnum;
import com.hcc.enums.AuthorityEnum;
import com.hcc.exceptions.ResourceNotFoundException;
import com.hcc.exceptions.UnauthorizedUpdateException;
import com.hcc.mappers.AssignmentMapper;
import com.hcc.repositories.AssignmentRepository;
import com.hcc.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AssignmentServiceTest {
    @Mock
    private AssignmentRepository assignmentRepo;
    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private AssignmentService service;

    private AssignmentMapper mapper;
    private User learner;
    private User admin;
    private User reviewer;
    private Assignment assignment;
    private Assignment updatedAssignment;
    private Long assignmentID;

    @BeforeEach
    void setup() {
        initMocks(this);
        mapper = new AssignmentMapper();
        learner = new User();
        learner.setId(123L);

        admin = new User();
        reviewer = new User();

        assignmentID = 456L;
        assignment = new Assignment(AssignmentStatusEnum.IN_REVIEW.getStatus(), 3, "github.com", "branch",
                null, learner, null);
        assignment.setId(assignmentID);
        updatedAssignment = new Assignment(AssignmentStatusEnum.COMPLETED.getStatus(), 5, "bithub.com", "branch1",
                "review.com", learner, reviewer);
        updatedAssignment.setId(assignmentID);
    }

    @Test
    public void getAssignmentsByUserId_successful() {
        //GIVEN
        List<Assignment> assignments = initializeAssignmentList();
        List<AssignmentResponseDto> expected = mapper.toDtoList(assignments);
        Long userId = learner.getId();

        when(userRepo.findById(userId)).thenReturn(Optional.of(learner));
        when(assignmentRepo.findByUserId(userId)).thenReturn(assignments);
        //WHEN
        List<AssignmentResponseDto> actual = service.getAssignmentsByUserId(userId);

        //THEN
        assertEquals(expected, actual);
    }

    @Test
    public void getAssignmentsByUserId_userIdNotFound_throwsResourceNotFoundException() {
        //GIVEN
        Long userId = learner.getId();
        when(userRepo.findById(userId)).thenReturn(Optional.empty());
        //WHEN
        //THEN
        assertThrows(ResourceNotFoundException.class, () -> service.getAssignmentsByUserId(userId));
    }

    @Test
    public void getAssignmentsByUserId_noAssignments(){
        //GIVEN
        List<AssignmentResponseDto> expected = mapper.toDtoList(new ArrayList<>());
        Long userId = learner.getId();

        when(userRepo.findById(userId)).thenReturn(Optional.of(learner));
        when(assignmentRepo.findByUserId(userId)).thenReturn(new ArrayList<>());
        //WHEN
        List<AssignmentResponseDto> actual = service.getAssignmentsByUserId(userId);

        //THEN
        assertEquals(expected, actual);
    }

    @Test
    public void getAssignmentById_successful() {
        //GIVEN
        AssignmentResponseDto expected = mapper.toDto(assignment);

        when(assignmentRepo.findById(assignmentID)).thenReturn(Optional.of(assignment));
        //WHEN
        AssignmentResponseDto actual = service.getAssignmentById(assignmentID);

        //THEN
        assertEquals(expected, actual);
    }

    @Test
    public void getAssignmentById_assignmentNotFound_throwsResourceNotFoundException() {
        //GIVEN
        when(assignmentRepo.findById(assignmentID)).thenReturn(Optional.empty());

        //WHEN
        //THEN
        assertThrows(ResourceNotFoundException.class, () -> service.getAssignmentById(assignmentID));
    }

    @Test
    public void putAssignmentById_mismatchedId_throwsUnauthorizedUpdateException() {
        //GIVEN
        Long providedID = 125L;

        //WHEN
        //THEN
        assertThrows(UnauthorizedUpdateException.class, () -> service.putAssignmentById(updatedAssignment, providedID,
                learner));
    }


    @Test
    public void putAssignmentById_adminRole_returnsOK() {
        //GIVEN
        //create admin
        List<Authority> adminAuth = new ArrayList<>();
        adminAuth.add(new Authority(AuthorityEnum.ROLE_ADMIN.name()));
        admin.setAuthorities(adminAuth);

        AssignmentResponseDto expected = mapper.toDto(updatedAssignment);

        when(assignmentRepo.findById(assignmentID)).thenReturn(Optional.of(assignment));

        //WHEN
        AssignmentResponseDto actual = service.putAssignmentById(updatedAssignment, assignmentID, admin);

        //THEN
        verify(assignmentRepo).save(updatedAssignment);
        assertEquals(expected, actual);
    }

    @Test
    public void putAssignmentById_reviewerRole_returnsOK() {
        //GIVEN
        //create reviewer
        List<Authority> reviewerAuth = new ArrayList<>();
        reviewerAuth.add(new Authority(AuthorityEnum.ROLE_REVIEWER.name()));
        reviewer.setAuthorities(reviewerAuth);

        Assignment expectedAssignment = new Assignment(updatedAssignment.getStatus(), assignment.getNumber(),
                assignment.getGithubUrl(), assignment.getBranch(), updatedAssignment.getReviewVideoUrl(),
                assignment.getUser(), updatedAssignment.getCodeReviewer());
        expectedAssignment.setId(updatedAssignment.getId());

        AssignmentResponseDto expected = mapper.toDto(expectedAssignment);

        when(assignmentRepo.findById(assignmentID)).thenReturn(Optional.of(assignment));

        //WHEN
        AssignmentResponseDto actual = service.putAssignmentById(updatedAssignment, assignmentID, reviewer);

        //THEN
        verify(assignmentRepo).save(expectedAssignment);
        assertEquals(expected, actual);
    }

    @Test
    public void putAssignmentById_learnerRole_returnsOK() {
        //GIVEN
        //create learner with authorities
        List<Authority> learnerAuth = new ArrayList<>();
        learnerAuth.add(new Authority(AuthorityEnum.ROLE_LEARNER.name()));
        learner.setAuthorities(learnerAuth);

        Assignment expectedAssignment = new Assignment(assignment.getStatus(), assignment.getNumber(),
                updatedAssignment.getGithubUrl(), updatedAssignment.getBranch(), assignment.getReviewVideoUrl(),
                assignment.getUser(), assignment.getCodeReviewer());
        expectedAssignment.setId(assignment.getId());

        AssignmentResponseDto expected = mapper.toDto(expectedAssignment);

        when(assignmentRepo.findById(assignmentID)).thenReturn(Optional.of(assignment));

        //WHEN
        AssignmentResponseDto actual = service.putAssignmentById(updatedAssignment, assignmentID, learner);

        //THEN
        verify(assignmentRepo).save(expectedAssignment);
        assertEquals(expected, actual);
    }

    @Test
    public void postAssignment_validAssignment_returnsCreated() {
        //GIVEN
        AssignmentResponseDto expected = mapper.toDto(assignment);
        //WHEN
        AssignmentResponseDto actual = service.postAssignment(assignment);
        //THEN
        verify(assignmentRepo).save(assignment);
        assertEquals(expected, actual);
    }

    @Test
    public void postAssignment_emptyAssignment_returnsBadRequest() {
        //GIVEN;
        when(assignmentRepo.save(assignment)).thenThrow(new IllegalArgumentException());
        //WHEN
        AssignmentResponseDto actual = service.postAssignment(assignment);
        //THEN
        verify(assignmentRepo).save(assignment);
        assertNull(actual);
    }

    private List<Assignment> initializeAssignmentList() {
        //create links
        String github = "github.com";
        String branch = "branch";
        String reviewVideoUrl = "review.com";

        List<Assignment> assignments = new ArrayList<>();
        assignments.add(new Assignment(AssignmentStatusEnum.COMPLETED.getStatus(), 1, github, branch,
                reviewVideoUrl, learner, reviewer));
        assignments.add(new Assignment(AssignmentStatusEnum.COMPLETED.getStatus(), 2, github, branch,
                reviewVideoUrl, learner, reviewer));
        assignments.add(new Assignment(AssignmentStatusEnum.IN_REVIEW.getStatus(), 3, github, branch,
                null, learner, reviewer));
        assignments.add(new Assignment(AssignmentStatusEnum.NEEDS_UPDATE.getStatus(), 4, github, branch,
                null, learner, reviewer));
        assignments.add(new Assignment(AssignmentStatusEnum.SUBMITTED.getStatus(), 5, github, branch,
                null, learner, null));
        assignments.add(new Assignment(AssignmentStatusEnum.PENDING_SUBMISSION.getStatus(), 6, null,
                null, null, learner, null));

        return assignments;
    }
}
