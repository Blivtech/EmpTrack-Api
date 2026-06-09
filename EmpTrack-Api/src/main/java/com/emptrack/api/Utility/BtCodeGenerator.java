package com.emptrack.api.Utility;

import com.emptrack.api.model.TblBtSequence;
import com.emptrack.api.repository.BtSequenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BtCodeGenerator {

    private final BtSequenceRepository btSequenceRepository;

    public String generate() {
        TblBtSequence seq = btSequenceRepository.save(new TblBtSequence());
        return String.format("BT%04d", seq.getId());
    }
}