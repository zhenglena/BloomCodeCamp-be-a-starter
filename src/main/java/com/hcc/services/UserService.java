package com.hcc.services;

import com.hcc.entities.Authority;
import com.hcc.entities.User;
import com.hcc.exceptions.ResourceNotFoundException;
import com.hcc.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
//    @Autowired
//    private UserRepository userRepository;
//
//    /**
//     * Returns the list of Roles by user. Finds the user in the database by calling UserRepository and then
//     * converts the Collection of authorities within that user to a List.
//     * @param username the user to find
//     * @return the List of authorities
//     */
//    public List<Authority> getRolesByUser(String username) {
//        Optional<User> userOptional = userRepository.findByUsername(username);
//        User user = null;
//        if (userOptional.isPresent()) {
//            user = userOptional.get();
//        } else {
//            throw new ResourceNotFoundException("User with username " + username + "does not exist.");
//        }
//
//        return convertToAuthorityList(user.getAuthorities());
//    }
//
//    private List<Authority> convertToAuthorityList(Collection<? extends GrantedAuthority> authorities) {
//        return authorities.stream()
//                .filter(Authority.class::isInstance)
//                .map(Authority.class::cast)
//                .collect(Collectors.toList());
//    }
//
//

}
