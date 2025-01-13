package com.hcc.config;

import com.hcc.entities.Assignment;
import com.hcc.entities.Authority;
import com.hcc.entities.User;
import com.hcc.enums.AssignmentStatusEnum;
import com.hcc.enums.AuthorityEnum;
import com.hcc.repositories.AssignmentRepository;
import com.hcc.repositories.UserRepository;
import com.hcc.utils.CustomPasswordEncoder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class DataLoader {
    @Bean
    public CommandLineRunner loadData(UserRepository userRepository,
                                      AssignmentRepository assignmentRepository,
                                      CustomPasswordEncoder customPasswordEncoder) {

        PasswordEncoder encoder = customPasswordEncoder.getPasswordEncoder();

        return args -> {
//            User learner = userRepository.findByUsername("testLearner")
//                    .orElseGet(() -> {
//                        User newUser = new User();
//                        newUser.setUsername("testLearner");
//                        newUser.setPassword(encoder.encode("testPassword"));
//
//                        Authority learnerAuthority = new Authority(AuthorityEnum.ROLE_LEARNER.name());
//                        learnerAuthority.setUser(newUser);
//
//                        newUser.setAuthorities(List.of(learnerAuthority));
//                        return userRepository.save(newUser);
//                    });
//
//            User reviewer = userRepository.findByUsername("testReviewer")
//                    .orElseGet(() -> {
//                        User newReviewer = new User();
//                        newReviewer.setUsername("testReviewer");
//                        newReviewer.setPassword(encoder.encode("testPassword"));
//
//                        Authority reviewerAuthority = new Authority(AuthorityEnum.ROLE_REVIEWER.name());
//                        reviewerAuthority.setUser(newReviewer);
//
//                        newReviewer.setAuthorities(List.of(reviewerAuthority));
//                        return userRepository.save(newReviewer);
//                    });
//
//            Assignment assignment1 = new Assignment(AssignmentStatusEnum.RESUBMITTED.getStatus(), 1, "github1", "branch1",
//                    null, learner, reviewer);
//            Assignment assignment2 = new Assignment(AssignmentStatusEnum.SUBMITTED.getStatus(), 2, "github2", "branch2",
//                    null, learner, null);
//            Assignment assignment3 = new Assignment(AssignmentStatusEnum.COMPLETED.getStatus(), 3, "github3", "branch3",
//                    "review3", learner, reviewer);
//            Assignment assignment4 = new Assignment(AssignmentStatusEnum.NEEDS_UPDATE.getStatus(), 4, "github4", "branch4",
//                    null, learner, reviewer);
//            Assignment assignment5 = new Assignment(AssignmentStatusEnum.SUBMITTED.getStatus(), 5, "github5", "branch5",
//                    null, learner, null);
//            Assignment assignment6 = new Assignment(AssignmentStatusEnum.PENDING_SUBMISSION.getStatus(), 6, "github6",
//                    "branch6", null, learner, null);
//
//            assignmentRepository.saveAll(List.of(
//                    assignment1, assignment2, assignment3, assignment4, assignment5, assignment6
//            ));
//            assignmentRepository.save(assignment4);
        };
    }
}
