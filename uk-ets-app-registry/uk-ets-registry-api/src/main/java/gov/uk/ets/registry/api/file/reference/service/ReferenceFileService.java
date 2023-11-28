package gov.uk.ets.registry.api.file.reference.service;

import gov.uk.ets.registry.api.file.reference.domain.ReferenceFile;
import gov.uk.ets.registry.api.file.reference.domain.type.ReferenceFileType;
import gov.uk.ets.registry.api.file.reference.repository.ReferenceFileRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class ReferenceFileService {

    private final ReferenceFileRepository referenceFileRepository;

    public List<ReferenceFile> getReferenceFileByType(ReferenceFileType referenceType) {
        return referenceFileRepository.findByReferenceFileType(referenceType);
    }
}
