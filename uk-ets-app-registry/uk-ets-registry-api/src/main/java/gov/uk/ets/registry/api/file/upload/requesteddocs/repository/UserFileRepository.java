package gov.uk.ets.registry.api.file.upload.requesteddocs.repository;

import gov.uk.ets.registry.api.file.upload.requesteddocs.domain.UserFile;
import gov.uk.ets.registry.api.user.domain.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserFileRepository extends JpaRepository<UserFile, Long> {
	
	/**
     * Retrieves a user file by id that belongs to the specified user.
     *
     * @param id the file id
     * @param user the specified user
     * @return UserFile
     */
    UserFile findByIdAndUser(Long id, User user);
}
