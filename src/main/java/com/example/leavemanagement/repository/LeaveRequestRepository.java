package com.example.leavemanagement.repository;

import com.example.leavemanagement.model.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    // Find leave requests for the same regNo that overlap with given date range
    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.regNo = :regNo AND " +
            "((:fromDate BETWEEN lr.fromDate AND lr.toDate) OR " +
            "(:toDate BETWEEN lr.fromDate AND lr.toDate) OR " +
            "(lr.fromDate BETWEEN :fromDate AND :toDate) OR " +
            "(lr.toDate BETWEEN :fromDate AND :toDate))")
    List<LeaveRequest> findOverlappingLeaves(@Param("regNo") String regNo,
                                             @Param("fromDate") LocalDate fromDate,
                                             @Param("toDate") LocalDate toDate);
}
