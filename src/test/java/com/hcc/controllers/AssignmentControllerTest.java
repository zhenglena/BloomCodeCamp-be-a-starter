package com.hcc.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcc.entities.Assignment;
import com.hcc.entities.Authority;
import com.hcc.entities.User;
import com.hcc.enums.AuthorityEnum;
import com.hcc.mappers.AssignmentMapper;
import com.hcc.repositories.AssignmentRepository;
import com.hcc.repositories.UserRepository;
import com.hcc.services.AssignmentService;
import com.hcc.services.AssignmentServiceTest;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
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
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
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

    private final List<Assignment> assignmentList = AssignmentServiceTest.initializeAssignmentList();
    private final AssignmentMapper mapper = new AssignmentMapper();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void getAssignmentsByUserId_userExists_returnsList() throws Exception {
        Long userId = 123L;
        User user = mock(User.class);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(assignmentRepository.findByUserId(userId)).thenReturn(assignmentList);
        when(assignmentService.getAssignmentsByUserId(userId)).thenReturn(mapper.toDtoList(assignmentList));

        String json = objectMapper.writeValueAsString(userId);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/assignments")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", CoreMatchers.is(assignmentList.size())));

        verify(assignmentService).getAssignmentsByUserId(userId);
    }

    @Test
    public void getAssignmentsByUserId_noAssignments_returnsNoContent() throws Exception {
        Long userId = 123L;
        User user = mock(User.class);
        List<Assignment> assignmentList = new ArrayList<>();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(assignmentRepository.findByUserId(userId)).thenReturn(assignmentList);
        when(assignmentService.getAssignmentsByUserId(userId)).thenReturn(mapper.toDtoList(assignmentList));


        String json = objectMapper.writeValueAsString(userId);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/assignments")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(assignmentService).getAssignmentsByUserId(userId);
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
    public void putAssignment_successfulUpdate_returnsOK() throws Exception {
        Long assignmentId = 123L;
        UserDetails userDetails = mock(UserDetails.class);
        User user = new User();
        user.setAuthorities(List.of(new Authority(AuthorityEnum.ROLE_LEARNER.name())));

        Assignment original = assignmentList.get(2);
        original.setId(assignmentId);
        Assignment updated = assignmentList.get(4);
        updated.setId(assignmentId);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userDetails.getUsername()).thenReturn("mockUsername");
        when(userRepository.findByUsername("mockUsername")).thenReturn(Optional.of(user));
        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(original));
        when(assignmentService.putAssignmentById(updated, assignmentId, user))
                .thenReturn(mapper.toDto(updated));

        String assignmentJson = objectMapper.writeValueAsString(updated);

        mockMvc.perform(MockMvcRequestBuilders
                .put("/api/assignments/{id}", assignmentId)
                .content(assignmentJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(assignmentService).putAssignmentById(updated, assignmentId, user);
    }

    @Test
    public void postAssignment_savedSuccessfully_returnsCreated() throws Exception {
        Assignment assignment = assignmentList.get(0);
        assignment.setId(123L);

        when(assignmentService.postAssignment(assignment)).thenReturn(mapper.toDto(assignment));

        String json = objectMapper.writeValueAsString(assignment);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/assignments")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string(objectMapper.writeValueAsString(mapper.toDto(assignment))));

        verify(assignmentService).postAssignment(assignment);
    }

    @Test
    public void postAssignment_emptyBody_returnsNoContent() throws Exception {
        Assignment assignment = new Assignment();

        when(assignmentService.postAssignment(assignment)).thenReturn(null);

        String json = objectMapper.writeValueAsString(assignment);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/assignments")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(assignmentService).postAssignment(assignment);
    }

}