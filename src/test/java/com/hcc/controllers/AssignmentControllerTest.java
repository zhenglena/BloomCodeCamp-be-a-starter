package com.hcc.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcc.entities.Assignment;
import com.hcc.entities.User;
import com.hcc.mappers.AssignmentMapper;
import com.hcc.repositories.AssignmentRepository;
import com.hcc.repositories.UserRepository;
import com.hcc.services.AssignmentService;
import com.hcc.services.AssignmentServiceTest;
import com.hcc.util.TestSecurityConfig;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.expression.spel.ast.Assign;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
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
                .andExpect(status().isOk());

        verify(assignmentService).getAssignmentById(assignmentId);
    }

    @Test
    public void putAssignment_successfulUpdate_returnsCreated() throws Exception {
        Long assignmentId = 123L;
        Long userId = 456L;

        Assignment assignmentToUpdate = assignmentList.get(2);
        assignmentToUpdate.setId(assignmentId);
        Assignment assignmentWithUpdates = assignmentList.get(4);
        assignmentWithUpdates.setId(assignmentId);


        User user = mock(User.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(assignmentToUpdate));
        when(assignmentService.putAssignmentById(assignmentWithUpdates, assignmentId, userId))
                .thenReturn(mapper.toDto(assignmentWithUpdates));

        String assignmentJson = objectMapper.writeValueAsString(assignmentToUpdate);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/assignments/{id}", assignmentId)
                .content(assignmentJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(assignmentService).putAssignmentById(assignmentWithUpdates, assignmentId, userId);

    }

}