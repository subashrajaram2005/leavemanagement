package com.example.leavemanagement.controller;

import com.example.leavemanagement.model.LeaveRequest;
import com.example.leavemanagement.repository.LeaveRequestRepository;
import com.example.leavemanagement.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/leave")
@CrossOrigin(origins = "http://localhost:3000")
public class LeaveRequestController {

    @Autowired
    private LeaveRequestRepository repository;

    @Autowired
    private EmailService emailService;

    // Student submits leave request
    @PostMapping("/request")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> submitLeave(@Valid @RequestBody LeaveRequest req, BindingResult br) {
        if (br.hasErrors()) {
            String errorMessage = br.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body("Validation error: " + errorMessage);
        }

        LocalDate from = req.getFromDate();
        LocalDate to = req.getToDate();

        if (from == null || to == null) {
            return ResponseEntity.badRequest().body("Validation error: From date and To date must be provided");
        }

        if (from.isAfter(to)) {
            return ResponseEntity.badRequest().body("Validation error: fromDate must be before or equal to toDate");
        }

        List<LeaveRequest> overlaps = repository.findOverlappingLeaves(req.getRegNo(), from, to);
        if (!overlaps.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Validation error: Leave date range overlaps with an existing request for this student.");
        }

        req.setStatus("Pending");
        LeaveRequest saved = repository.save(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // Admin fetches all leave requests
    @GetMapping("/requests")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllRequests() {
        try {
            List<LeaveRequest> list = repository.findAll();
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server error: " + e.getMessage());
        }
    }

    // Approve leave request
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> approve(@PathVariable("id") Long id) {
        try {
            Optional<LeaveRequest> opt = repository.findById(id);
            if (opt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
            LeaveRequest r = opt.get();
            if ("Approved".equalsIgnoreCase(r.getStatus())) {
                return ResponseEntity.badRequest().body("Already approved");
            }
            r.setStatus("Approved");
            repository.save(r);

            String subj = "Leave Approved";
            String body = "Hello " + r.getName() + ",\n\nYour leave from " + r.getFromDate() + " to " + r.getToDate() + " has been Approved.\n\nRegards.";

            try {
                emailService.sendLeaveStatusEmail(r.getEmail(), subj, body);
            } catch (Exception e) {
                e.printStackTrace();
                // Log or handle email sending failure if needed
            }

            return ResponseEntity.ok(r);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error approving leave or sending email: " + e.getMessage());
        }
    }

    // Reject leave request
    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> reject(@PathVariable("id") Long id) {
        try {
            Optional<LeaveRequest> opt = repository.findById(id);
            if (opt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
            LeaveRequest r = opt.get();
            if ("Rejected".equalsIgnoreCase(r.getStatus())) {
                return ResponseEntity.badRequest().body("Already rejected");
            }
            r.setStatus("Rejected");
            repository.save(r);

            String subj = "Leave Rejected";
            String body = "Hello " + r.getName() + ",\n\nYour leave from " + r.getFromDate() + " to " + r.getToDate() + " has been Rejected.\n\nRegards.";

            try {
                emailService.sendLeaveStatusEmail(r.getEmail(), subj, body);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return ResponseEntity.ok(r);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error rejecting leave or sending email: " + e.getMessage());
        }
    }

    // Delete leave request
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        try {
            Optional<LeaveRequest> opt = repository.findById(id);
            if (opt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
            repository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting leave request: " + e.getMessage());
        }
    }
}
