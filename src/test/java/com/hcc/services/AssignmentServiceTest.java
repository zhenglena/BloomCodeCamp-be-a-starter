package com.hcc.services;

import com.hcc.dtos.AssignmentCreateDto;
import com.hcc.dtos.AssignmentDto;
import com.hcc.entities.Assignment;
import com.hcc.entities.Authority;
import com.hcc.entities.User;
import com.hcc.enums.AssignmentStatusEnum;
import com.hcc.enums.AuthorityEnum;
import com.hcc.exceptions.ResourceNotFoundException;
import com.hcc.mappers.AssignmentMapper;
import com.hcc.repositories.AssignmentRepository;
import com.hcc.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    private AssignmentMapper mapper = Mappers.getMapper(AssignmentMapper.class);

    private User learner;
    private User reviewer;

    @BeforeEach
    void setup() {
        initMocks(this);
        learner = new User();
        learner.setId(123L);

        reviewer = new User();
    }

    @Test
    public void getAssignmentsByLearner_successful() {
        //GIVEN
        List<Assignment> assignments = initializeAssignmentList();
        List<AssignmentDto> expected = mapper.toDtoList(assignments);
        Long userId = learner.getId();

        when(assignmentRepo.findByUserId(userId)).thenReturn(assignments);
        //WHEN
        List<AssignmentDto> actual = service.getAssignmentsByLearner(learner);

        //THEN
        assertEquals(expected, actual);
    }


    @Test
    public void getAssignmentsByLearner_noAssignments(){
        //GIVEN
        List<AssignmentDto> expected = mapper.toDtoList(new ArrayList<>());
        Long userId = learner.getId();

        when(assignmentRepo.findByUserId(userId)).thenReturn(new ArrayList<>());
        //WHEN
        List<AssignmentDto> actual = service.getAssignmentsByLearner(learner);

        //THEN
        assertEquals(expected, actual);
    }

    @Test
    public void getAssignmentsByStatus_submittedStatus_returnsAllByStatus() {
        //GIVEN
        List<Assignment> expectedList =
                initializeAssignmentList().stream().filter(a -> a.getStatus().equals(AssignmentStatusEnum.SUBMITTED.getStatus())).collect(Collectors.toList());
        when(assignmentRepo.findByStatus(AssignmentStatusEnum.SUBMITTED.getStatus())).thenReturn(expectedList);
        //WHEN
        List<AssignmentDto> actual = service.getAssignmentsByStatus(reviewer,
                AssignmentStatusEnum.SUBMITTED.getStatus());
        //THEN
        assertEquals(mapper.toDtoList(expectedList), actual);
        assertEquals(expectedList.size(), actual.size());
    }

    @Test
    public void getAssignmentsByStatus_resubmittedStatus_returnsAllByStatus() {
        //GIVEN
        List<Assignment> expectedList =
                initializeAssignmentList().stream().filter(a -> a.getStatus().equals(AssignmentStatusEnum.RESUBMITTED.getStatus())
                        && a.getCodeReviewer() != null && a.getCodeReviewer().equals(reviewer)).collect(Collectors.toList());
        when(assignmentRepo.findByReviewerIdAndStatus(reviewer.getId(), AssignmentStatusEnum.RESUBMITTED.getStatus())).thenReturn(expectedList);
        //WHEN
        List<AssignmentDto> actual = service.getAssignmentsByStatus(reviewer,
                AssignmentStatusEnum.RESUBMITTED.getStatus());
        //THEN
        assertEquals(mapper.toDtoList(expectedList), actual);
    }

    @Test
    public void getAssignmentById_successful() {
        Assignment assignment = new Assignment(AssignmentStatusEnum.IN_REVIEW.getStatus(), 3, "github.com", "branch",
                null, learner, null);
        assignment.setId(456L);
        Assignment updatedAssignment = new Assignment(AssignmentStatusEnum.COMPLETED.getStatus(), 5, "bithub.com",
                "branch1",
                "review.com", learner, reviewer);
        updatedAssignment.setId(456L);
        //GIVEN
        AssignmentDto expected = mapper.toDto(assignment);

        when(assignmentRepo.findById(456L)).thenReturn(Optional.of(assignment));
        //WHEN
        AssignmentDto actual = service.getAssignmentById(456L);

        //THEN
        assertEquals(expected, actual);
    }

    @Test
    public void getAssignmentById_assignmentNotFound_throwsResourceNotFoundException() {
        //GIVEN
        when(assignmentRepo.findById(456L)).thenReturn(Optional.empty());

        //WHEN
        //THEN
        assertThrows(ResourceNotFoundException.class, () -> service.getAssignmentById(456L));
    }


    @Test
    public void updateAssignmentById_reviewerRole_returnsOK() {
        //GIVEN
        //create reviewer
        List<Authority> reviewerAuth = new ArrayList<>();
        reviewerAuth.add(new Authority(AuthorityEnum.ROLE_REVIEWER.name()));
        reviewer.setAuthorities(reviewerAuth);

        Assignment assignment = new Assignment(AssignmentStatusEnum.IN_REVIEW.getStatus(), 3, "github.com", "branch",
                null, learner, reviewer);
        assignment.setId(456L);

        AssignmentDto updatedAssignment = new AssignmentDto();
        updatedAssignment.setStatus(AssignmentStatusEnum.COMPLETED.getStatus());
        updatedAssignment.setReviewVideoUrl("review.com");

        Assignment expected = new Assignment(AssignmentStatusEnum.COMPLETED.getStatus(), 3, "github.com", "branch",
                "review.com", learner, reviewer);
        expected.setId(456L);
        AssignmentDto expectedDto = mapper.toDto(expected);


        when(assignmentRepo.findById(456L)).thenReturn(Optional.of(assignment));

        //WHEN
        AssignmentDto actual = service.updateAssignmentById(updatedAssignment, 456L, reviewer);

        //THEN
        ArgumentCaptor<Assignment> assignmentCaptor = ArgumentCaptor.forClass(Assignment.class);
        verify(assignmentRepo).save(assignmentCaptor.capture());
        assertEquals(expectedDto, actual);
    }

    @Test
    public void updateAssignmentById_learnerRole_returnsOK() {
        //GIVEN
        //create learner with authorities
        List<Authority> learnerAuth = new ArrayList<>();
        learnerAuth.add(new Authority(AuthorityEnum.ROLE_LEARNER.name()));
        learner.setAuthorities(learnerAuth);

        Assignment assignment = new Assignment(AssignmentStatusEnum.NEEDS_UPDATE.getStatus(), 3, null, "branch",
                null, learner, reviewer);
        assignment.setId(456L);

        AssignmentDto updatedAssignment = new AssignmentDto();
        updatedAssignment.setBranch("branch1");
        updatedAssignment.setStatus(AssignmentStatusEnum.RESUBMITTED.getStatus());
        updatedAssignment.setGithubUrl("github.com");

        Assignment expected = new Assignment(AssignmentStatusEnum.RESUBMITTED.getStatus(), 3, "github.com",
                "branch1", null, learner, reviewer);
        AssignmentDto expectedDto = mapper.toDto(expected);

        when(assignmentRepo.findById(456L)).thenReturn(Optional.of(assignment));

        //WHEN
        AssignmentDto actual = service.updateAssignmentById(updatedAssignment, 456L, learner);

        //THEN
        ArgumentCaptor<Assignment> assignmentCaptor = ArgumentCaptor.forClass(Assignment.class);
        verify(assignmentRepo).save(assignmentCaptor.capture());
        assertEquals(expectedDto, actual);
    }

    @Test
    public void createAssignment_validAssignment_returnsCreated() {
        //GIVEN
        List<Authority> learnerAuth = new ArrayList<>();
        learnerAuth.add(new Authority(AuthorityEnum.ROLE_LEARNER.name()));
        learner.setAuthorities(learnerAuth);
        AssignmentCreateDto created = new AssignmentCreateDto();
        created.setNumber(3);
        created.setBranch("branch");
        created.setGithubUrl("github.com");

        Assignment expectedAssignment = new Assignment(AssignmentStatusEnum.PENDING_SUBMISSION.getStatus(), 3, "github.com", "branch",
                null, learner, null);

        AssignmentDto expected = mapper.toDto(expectedAssignment);
        //WHEN
        AssignmentDto actual = service.createAssignment(created, learner);
        //THEN
        verify(assignmentRepo).save(expectedAssignment);
        assertEquals(expected, actual);
    }


    private List<Assignment> initializeAssignmentList() {
        //create links
        String github = "github.com";
        String branch = "branch";
        String reviewVideoUrl = "review.com";

        List<Assignment> assignments = new ArrayList<>();
        assignments.add(new Assignment(AssignmentStatusEnum.RESUBMITTED.getStatus(), 1, github, branch,
                reviewVideoUrl, learner, reviewer));
        assignments.add(new Assignment(AssignmentStatusEnum.RESUBMITTED.getStatus(), 2, github, branch,
                reviewVideoUrl, learner, null));
        assignments.add(new Assignment(AssignmentStatusEnum.RESUBMITTED.getStatus(), 3, github, branch,
                null, learner, learner));
        assignments.add(new Assignment(AssignmentStatusEnum.NEEDS_UPDATE.getStatus(), 4, github, branch,
                null, learner, reviewer));
        assignments.add(new Assignment(AssignmentStatusEnum.SUBMITTED.getStatus(), 5, github, branch,
                null, learner, null));
        assignments.add(new Assignment(AssignmentStatusEnum.PENDING_SUBMISSION.getStatus(), 6, null,
                null, null, learner, null));

        return assignments;
    }
}
