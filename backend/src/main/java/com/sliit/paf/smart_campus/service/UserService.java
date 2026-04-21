package com.sliit.paf.smart_campus.service;

import com.sliit.paf.smart_campus.dto.response.UserResponse;
import com.sliit.paf.smart_campus.exception.ForbiddenException;
import com.sliit.paf.smart_campus.exception.ResourceNotFoundException;
import com.sliit.paf.smart_campus.model.User;
import com.sliit.paf.smart_campus.model.User.Role;
import com.sliit.paf.smart_campus.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final TicketRepository ticketRepository;
    private final CommentRepository commentRepository;
    private final NotificationRepository notificationRepository;

    @Value("${app.admin.emails:admin@sliit.lk}")
    private String adminEmails;

    /** Find user by email */
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }

    /** Find or create user from OAuth2 login */
    public User findOrCreateUser(String email, String name, String picture) {
        return userRepository.findByEmail(email).orElseGet(() -> {
            User user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setPicture(picture);
            user.setCreatedAt(Instant.now());
            
            // Auto-assign roles based on email prefix or configured admin emails
            if (Arrays.asList(adminEmails.split(",")).contains(email.trim()) || email.startsWith("admin@")) {
                user.setRole(Role.ADMIN);
            } else if (email.startsWith("tech@")) {
                user.setRole(Role.TECHNICIAN);
            } else {
                user.setRole(Role.USER);
            }
            return userRepository.save(user);
        });
    }

    public User getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public UserResponse updateUserRole(String id, String role) {
        User user = getUserById(id);
        user.setRole(Role.valueOf(role));
        return toResponse(userRepository.save(user));
    }

    /** Delete a user and all their related data (cascade delete) */
    @Transactional
    public void deleteUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        // Prevent deletion of admin users
        if (user.getRole() == Role.ADMIN) {
            throw new ForbiddenException("Cannot delete admin users");
        }

        // Cascade delete all related entities
        bookingRepository.deleteByUserId(userId);
        ticketRepository.deleteByUserId(userId);
        commentRepository.deleteByUserId(userId);
        notificationRepository.deleteByUserId(userId);

        // Delete the user
        userRepository.deleteById(userId);
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getName(),
                user.getPicture(), user.getRole().name(), user.getCreatedAt());
    }
}
