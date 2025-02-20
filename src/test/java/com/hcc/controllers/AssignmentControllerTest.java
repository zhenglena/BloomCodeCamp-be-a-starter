package com.hcc.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcc.dtos.AssignmentCreateDto;
import com.hcc.dtos.AssignmentDto;
import com.hcc.entities.Assignment;
import com.hcc.entities.Authority;
import com.hcc.entities.User;
import com.hcc.enums.AssignmentStatusEnum;
import com.hcc.enums.AuthorityEnum;
import com.hcc.exceptions.UnauthorizedAccessException;
import com.hcc.mappers.AssignmentMapper;
import com.hcc.repositories.AssignmentRepository;
import com.hcc.repositories.UserRepository;
import com.hcc.services.AssignmentService;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class AssignmentControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AssignmentService assignmentService;
    @MockBean
    private AssignmentRepository assignmentRepository;
    @MockBean
    private UserRepository userRepository;

    private final AssignmentMapper mapper = Mappers.getMapper(AssignmentMapper.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private User learner;
    private User reviewer;
    private List<Assignment> assignmentList;
    private UserDetails userDetails;

    @BeforeEach
    public void setup() {
        learner = new User();
        learner.setId(123L);
        learner.setAuthorities(List.of(new Authority(AuthorityEnum.ROLE_LEARNER.name())));

        reviewer = new User();
        reviewer.setId(234L);
        reviewer.setAuthorities(List.of(new Authority(AuthorityEnum.ROLE_REVIEWER.name())));

        assignmentList = initializeAssignmentList();
        userDetails = mock(UserDetails.class);
    }

    @Test
    void contextLoads() {}

    @Test
    public void getAssignmentsByUser_learner_returnsList() throws Exception {
        checkUserAuthentication(userDetails, learner);

        when(assignmentRepository.findByUserId(learner.getId())).thenReturn(assignmentList);
        when(assignmentService.getAssignmentsByLearner(learner)).thenReturn(mapper.toDtoList(assignmentList));

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/assignments").with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", CoreMatchers.is(assignmentList.size())));

        verify(assignmentService).getAssignmentsByLearner(learner);
    }

    @Test
    public void getAssignmentsByUser_noAssignments_returnsNoContent() throws Exception {
        List<Assignment> mockList = new ArrayList<>();
        checkUserAuthentication(userDetails, learner);

        when(assignmentRepository.findByUserId(learner.getId())).thenReturn(mockList);
        when(assignmentService.getAssignmentsByLearner(learner)).thenReturn(mapper.toDtoList(mockList));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/assignments").with(user(learner)))
                .andExpect(status().isNoContent());

        verify(assignmentService).getAssignmentsByLearner(learner);
    }

    @Test
    public void getAssignmentsByUser_reviewer_returnsList() throws Exception {
        String submitted = AssignmentStatusEnum.SUBMITTED.getStatus();
        String resubmitted = AssignmentStatusEnum.RESUBMITTED.getStatus();
        String completed = AssignmentStatusEnum.COMPLETED.getStatus();

        checkUserAuthentication(userDetails, reviewer);

        List<Assignment> submittedAssignments =
                assignmentList.stream().filter(a -> a.getStatus().equals(submitted)).collect(Collectors.toList());
        List<Assignment> resubmittedAssignments =
                assignmentList.stream().filter(a -> a.getStatus().equals(resubmitted)).collect(Collectors.toList());
        List<Assignment> completedAssignments =
                assignmentList.stream().filter(a -> a.getStatus().equals(completed)).collect(Collectors.toList());

        List<Assignment> returnedAssignments = new ArrayList<>();
        returnedAssignments.addAll(submittedAssignments);
        returnedAssignments.addAll(resubmittedAssignments);
        returnedAssignments.addAll(completedAssignments);

        when(assignmentRepository.findByStatus(submitted)).thenReturn(submittedAssignments);
        when(assignmentRepository.findByCodeReviewerIdAndStatus(reviewer.getId(), resubmitted)).thenReturn(resubmittedAssignments);
        when(assignmentRepository.findByCodeReviewerIdAndStatus(reviewer.getId(), completed)).thenReturn(completedAssignments);
        when(assignmentService.getAssignmentsByReviewer(reviewer)).thenReturn(mapper.toDtoList(returnedAssignments));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/assignments")
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", CoreMatchers.is(returnedAssignments.size())));

        verify(assignmentService).getAssignmentsByReviewer(reviewer);
    }

    @Test
    public void getAssignmentById_returnsOK() throws Exception {
        Assignment assignment = assignmentList.get(0);
        Long assignmentId = 123L;

        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(assignment));
        when(assignmentService.getAssignmentById(assignmentId)).thenReturn(mapper.toDto(assignment));

        String json = objectMapper.writeValueAsString(assignmentId);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/assignments/{id}", json))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(mapper.toDto(assignment))));

        verify(assignmentService).getAssignmentById(assignmentId);
    }

    @Test
    public void putAssignment_successfulLearnerUpdate_returnsOK() throws Exception {
        Long assignmentId = 123L;
        Assignment original = new Assignment(AssignmentStatusEnum.NEEDS_UPDATE.getStatus(), 1, "github", "branch",
                null, learner, reviewer);
        original.setId(assignmentId);

        AssignmentDto updated = new AssignmentDto();
        updated.setStatus(AssignmentStatusEnum.RESUBMITTED.getStatus());
        updated.setGithubUrl("github.com");
        updated.setBranch("branch.com");

        Assignment expected = new Assignment(AssignmentStatusEnum.RESUBMITTED.getStatus(), 1, "github.com", "branch" +
                ".com", null, learner, reviewer);

        checkUserAuthentication(userDetails, learner);

        doReturn(mapper.toDto(expected)).when(assignmentService).updateAssignmentById(updated, assignmentId, learner);
        doReturn(Optional.of(original)).when(assignmentRepository).findById(assignmentId);

        String assignmentJson = objectMapper.writeValueAsString(updated);

        mockMvc.perform(MockMvcRequestBuilders
                .put("/api/assignments/{id}", assignmentId).with(user(userDetails))
                .content(assignmentJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(assignmentService).updateAssignmentById(updated, assignmentId, learner);
    }

    @Test
    public void putAssignment_successfulReviewerUpdate_returnsOK() throws Exception {
        Long assignmentId = 123L;
        Assignment original = new Assignment(AssignmentStatusEnum.SUBMITTED.getStatus(), 1, "github", "branch",
                null, learner, null);
        original.setId(assignmentId);

        AssignmentDto updated = new AssignmentDto();
        updated.setStatus(AssignmentStatusEnum.IN_REVIEW.getStatus());
        updated.setCodeReviewer(reviewer);

        Assignment expected = new Assignment(AssignmentStatusEnum.IN_REVIEW.getStatus(), 1, "github", "branch",
                null, learner, reviewer);

        checkUserAuthentication(userDetails, reviewer);

        doReturn(mapper.toDto(expected)).when(assignmentService).updateAssignmentById(updated, assignmentId, learner);
        doReturn(Optional.of(original)).when(assignmentRepository).findById(assignmentId);

        String assignmentJson = objectMapper.writeValueAsString(updated);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/assignments/{id}", assignmentId).with(user(userDetails))
                        .content(assignmentJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(assignmentService).updateAssignmentById(updated, assignmentId, reviewer);
    }

    @Test
    public void putAssignment_userNotAuthenticated_returnsUnauthorized() throws Exception {
        AssignmentDto assignment = mapper.toDto(assignmentList.get(0));

        String json = objectMapper.writeValueAsString(assignment);

        mockMvc.perform(MockMvcRequestBuilders
                .put("/api/assignments/{id}", 123L)
                .content(json).contentType(MediaType.APPLICATION_JSON)
                .with(user(userDetails)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void putAssignment_userNotFound_returnsNotFound() throws Exception {
        AssignmentDto assignment = mapper.toDto(assignmentList.get(0));

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userDetails.getUsername()).thenReturn("mockUsername");
        when(userRepository.findByUsername("mockUsername")).thenReturn(Optional.empty());

        String json = objectMapper.writeValueAsString(assignment);
        mockMvc.perform(MockMvcRequestBuilders
                .put("/api/assignments/{id}", 123L)
                .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void createAssignment_learner_returnsCreated() throws Exception {
        AssignmentCreateDto assignment = new AssignmentCreateDto();
        assignment.setNumber(1);
        assignment.setBranch("branch");
        assignment.setGithubUrl("github");

        Assignment expectedAssignment = new Assignment(AssignmentStatusEnum.PENDING_SUBMISSION.getStatus(), 1,
                "github", "branch", null, learner, null);

        checkUserAuthentication(userDetails, learner);

        when(assignmentService.createAssignment(assignment, learner)).thenReturn(mapper.toDto(expectedAssignment));

        String json = objectMapper.writeValueAsString(assignment);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/assignments")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string(objectMapper.writeValueAsString(mapper.toDto(expectedAssignment))));

        verify(assignmentService).createAssignment(assignment, learner);
    }

    @Test
    public void createAssignment_reviewer_returnsUnauthorized() throws Exception {
        AssignmentCreateDto assignment = new AssignmentCreateDto();

        checkUserAuthentication(userDetails, reviewer);
        when(assignmentService.createAssignment(assignment, reviewer)).thenThrow(UnauthorizedAccessException.class);

        String json = objectMapper.writeValueAsString(assignment);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/assignments")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        verify(assignmentService).createAssignment(assignment, reviewer);
    }

    private List<Assignment> initializeAssignmentList() {
        //create links
        String github = "github.com";
        String branch = "branch";
        String reviewVideoUrl = "review.com";

        List<Assignment> assignments = new ArrayList<>();
        assignments.add(new Assignment(AssignmentStatusEnum.RESUBMITTED.getStatus(), 1, github, branch,
                null, learner, reviewer));
        assignments.add(new Assignment(AssignmentStatusEnum.RESUBMITTED.getStatus(), 2, github, branch,
                null, learner, null));
        assignments.add(new Assignment(AssignmentStatusEnum.RESUBMITTED.getStatus(), 3, github, branch,
                null, learner, learner));
        assignments.add(new Assignment(AssignmentStatusEnum.NEEDS_UPDATE.getStatus(), 1, github, branch,
                null, learner, reviewer));
        assignments.add(new Assignment(AssignmentStatusEnum.SUBMITTED.getStatus(), 5, github, branch,
                null, learner, null));
        assignments.add(new Assignment(AssignmentStatusEnum.PENDING_SUBMISSION.getStatus(), 6, github,
                branch, null, null, null));

        return assignments;
    }

    private void checkUserAuthentication(UserDetails userDetails, User user) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userDetails.getUsername()).thenReturn("mockUsername");
        when(userRepository.findByUsername("mockUsername")).thenReturn(Optional.of(user));
    }

}