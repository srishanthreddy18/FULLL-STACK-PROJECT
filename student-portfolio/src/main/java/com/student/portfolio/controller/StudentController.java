package com.student.portfolio.controller;

import com.student.portfolio.entity.Milestone;
import com.student.portfolio.entity.Portfolio;
import com.student.portfolio.entity.Project;
import com.student.portfolio.entity.User;
import com.student.portfolio.service.MilestoneService;
import com.student.portfolio.service.PortfolioService;
import com.student.portfolio.service.ProjectService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private MilestoneService milestoneService;

    @Autowired
    private PortfolioService portfolioService;

    // Helper method to check if the session belongs to a student
    private User getSessionUser(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user != null && "ROLE_STUDENT".equals(user.getRole())) {
            return user;
        }
        return null;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> dashboard(HttpSession session) {
        User user = getSessionUser(session);
        if (user == null) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "Unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
        }
        
        List<Project> projects = projectService.getProjectsByUser(user);
        Map<String, Object> response = new HashMap<>();
        response.put("user", user);
        response.put("projectCount", projects.size());
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/projects")
    public ResponseEntity<?> createProject(@RequestBody Project project, HttpSession session) {
        User user = getSessionUser(session);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
        }
        
        project.setUser(user);
        project.setStatus("Pending"); // Automatically pending for admin approval
        Project savedProject = projectService.saveProject(project);
        
        return ResponseEntity.ok(savedProject);
    }

    @GetMapping("/projects")
    public ResponseEntity<?> listProjects(HttpSession session) {
        User user = getSessionUser(session);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
        }
        
        List<Project> projects = projectService.getProjectsByUser(user);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/projects/{id}")
    public ResponseEntity<?> getProjectWithMilestones(@PathVariable Long id, HttpSession session) {
        User user = getSessionUser(session);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
        }
        
        Project project = projectService.getProjectById(id);
        // Ensure that a student can only view their own projects
        if (project == null || !project.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Access denied"));
        }
        
        List<Milestone> milestones = milestoneService.getMilestonesByProject(project);
        
        Map<String, Object> response = new HashMap<>();
        response.put("project", project);
        response.put("milestones", milestones);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/projects/{id}/milestones")
    public ResponseEntity<?> addMilestone(@PathVariable Long id, @RequestBody Milestone milestone, HttpSession session) {
        User user = getSessionUser(session);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
        }
        
        Project project = projectService.getProjectById(id);
        if (project != null && project.getUser().getId().equals(user.getId())) {
            milestone.setProject(project);
            Milestone savedMilestone = milestoneService.saveMilestone(milestone);
            return ResponseEntity.ok(savedMilestone);
        }
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Access denied"));
    }

    @GetMapping("/portfolio")
    public ResponseEntity<?> getPortfolio(HttpSession session) {
        User user = getSessionUser(session);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
        }
        
        Portfolio portfolio = portfolioService.getPortfolioByUser(user);
        if (portfolio == null) {
            portfolio = new Portfolio();
            portfolio.setUser(user);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("portfolio", portfolio);
        response.put("user", user);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/portfolio")
    public ResponseEntity<?> savePortfolio(@RequestBody Portfolio portfolioData, HttpSession session) {
        User user = getSessionUser(session);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
        }
        
        Portfolio existing = portfolioService.getPortfolioByUser(user);
        if (existing != null) {
            existing.setSkills(portfolioData.getSkills());
            existing.setAchievements(portfolioData.getAchievements());
            Portfolio updated = portfolioService.savePortfolio(existing);
            return ResponseEntity.ok(updated);
        } else {
            portfolioData.setUser(user);
            Portfolio saved = portfolioService.savePortfolio(portfolioData);
            return ResponseEntity.ok(saved);
        }
    }
}
