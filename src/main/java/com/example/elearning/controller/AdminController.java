package com.example.elearning.controller;

import com.example.elearning.model.Course;
import com.example.elearning.service.CourseService;
import com.example.elearning.service.UserService;
import com.example.elearning.session.SessionUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final CourseService courseService;
    private final UserService userService;
    private final SessionUser sessionUser;

    public AdminController(CourseService courseService, UserService userService, SessionUser sessionUser) {
        this.courseService = courseService;
        this.userService = userService;
        this.sessionUser = sessionUser;
    }

    // ── DASHBOARD ──────────────────────────────────────────
    // shows all courses + all users to the admin
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        model.addAttribute("courses", courseService.getAllCourses());
        model.addAttribute("users", userService.getAllUsers());
        return "admin/dashboard";
    }

    // ── ADD COURSE ─────────────────────────────────────────
    // shows the empty add-course form
    @GetMapping("/add-course")
    public String showAddCourseForm(Model model) {
        model.addAttribute("course", new Course());
        return "admin/add-course";
    }

    // handles the form submission → saves new course to DB
    @PostMapping("/add-course")
    public String handleAddCourse(@ModelAttribute Course course) {

        // the admin who is logged in becomes the instructor (createdBy)
        course.setCreatedBy(userService.findById(sessionUser.getUserId()));
        courseService.save(course);
        return "redirect:/admin/dashboard";
    }

    // ── EDIT COURSE ────────────────────────────────────────
    // shows the edit form pre-filled with existing course data
    @GetMapping("/edit-course/{id}")
    public String showEditCourseForm(@PathVariable Long id, Model model) {
        model.addAttribute("course", courseService.findById(id));
        return "admin/edit-course";
    }

    // handles the edit form submission → updates course in DB
    @PostMapping("/edit-course/{id}")
    public String handleEditCourse(@PathVariable Long id,
                                   @ModelAttribute Course course) {
        course.setId(id);
        course.setCreatedBy(userService.findById(sessionUser.getUserId()));
        courseService.save(course);
        return "redirect:/admin/dashboard";
    }

    // ── DELETE COURSE ──────────────────────────────────────
    @GetMapping("/delete-course/{id}")
    public String deleteCourse(@PathVariable Long id) {
        courseService.deleteById(id);
        return "redirect:/admin/dashboard";
    }
}