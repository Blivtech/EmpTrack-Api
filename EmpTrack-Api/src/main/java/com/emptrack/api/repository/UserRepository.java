package com.emptrack.api.repository;

import com.emptrack.api.model.TblUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<TblUsers, Long> {
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByEmail(String email);
    boolean existsByBtCode(String btCode);
    Optional<TblUsers> findByPhoneNumber(String phoneNumber);

    @Query("SELECT MAX(u.id) FROM TblUsers u")
    Optional<Long> findMaxId();

    List<TblUsers> findAllByActiveStatusOrderByDisplayNameAsc(Integer activeStatus);

}