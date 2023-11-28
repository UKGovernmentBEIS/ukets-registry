package gov.uk.ets.registry.api.file.upload.services;

import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * A scheduler responsible for managing the files table.
 */
@Service
@RequiredArgsConstructor
public class FilesScheduler {

    private final FileUploadService fileUploadService;

    @Transactional
    @SchedulerLock(name = "fileSchedulerLock", lockAtLeastFor = "500ms")
    @Scheduled(cron = "${scheduler.files.start}")
    public void scheduleNonSubmittedFiles() {
        LockAssert.assertLocked();
        fileUploadService.deleteNotSubmittedFiles();
    }

    @Transactional
    @SchedulerLock(name = "submittedFileSchedulerLock", lockAtLeastFor = "500ms")
    @Scheduled(cron = "${scheduler.files.start}")
    public void scheduleSubmittedFiles() {
        LockAssert.assertLocked();
        fileUploadService.deleteSubmittedFiles();
    }
}
