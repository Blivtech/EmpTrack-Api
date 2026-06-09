package com.emptrack.api.repository;

import com.emptrack.api.model.TblAttendanceDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AttendanceDetailRepository
    extends JpaRepository<TblAttendanceDetail, Long> {

    // ✅ Get all details by attendance_id
    List<TblAttendanceDetail> findByAttendanceId(String attendanceId);

    // ✅ Get single detail by detail_id
    Optional<TblAttendanceDetail> findByDetailId(String detailId);

    // ✅ Delete all details for an attendance
    void deleteByAttendanceId(String attendanceId);
}