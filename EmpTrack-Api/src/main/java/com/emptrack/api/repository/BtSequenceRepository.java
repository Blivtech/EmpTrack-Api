package com.emptrack.api.repository;

import com.emptrack.api.model.TblBtSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BtSequenceRepository extends JpaRepository<TblBtSequence, Long> {
}