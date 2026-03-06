package com.student.portfolio.controller;

import com.student.portfolio.entity.Project;
import com.student.portfolio.entity.User;
import com.student.portfolio.service.ProjectService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private ProjectService projectService;

    // Helper method to ensure session belongs to an admin
    private User getAdminSessionUser(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user != null && "ROLE_ADMIN".equals(user.getRole())) {
            return user;
        }
        return null;
    }

    @GetMapping("/projects")
    public ResponseEntity<?> getAllProjects(HttpSession session) {
        if (getAdminSessionUser(session) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized admins only"));
        }

        List<Project> allProjects = projectService.getAllProjects();
        return ResponseEntity.ok(allProjects);
    }

    @PostMapping("/projects/{id}/review")
    public ResponseEntity<?> reviewProject(@PathVariable Long id, 
                                           @RequestBody Map<String, String> reviewData, 
                                           HttpSession session) {
        if (getAdminSessionUser(session) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized admins only"));
        }

        String status = reviewData.get("status");
        String feedback = reviewData.get("feedback");

        projectService.updateProjectStatusAndFeedback(id, status, feedback);
        
        Project updatedProject = projectService.getProjectById(id);
        return ResponseEntity.ok(updatedProject);
    }
}
