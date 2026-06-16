package com.emptrack.api.repository;

import com.emptrack.api.model.TblAttendanceDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface AttendanceDetailRepository
    extends JpaRepository<TblAttendanceDetail, Long> {

    // ✅ Get all details by attendance_id
    List<TblAttendanceDetail> findByAttendanceId(String attendanceId);

    // ✅ Get single detail by detail_id
    Optional<TblAttendanceDetail> findByDetailId(String detailId);



    @Modifying
    @Transactional
    @Query("DELETE FROM TblAttendanceDetail d WHERE d.attendanceId = :attendanceId")
    void deleteByAttendanceId(@Param("attendanceId") String attendanceId);
}