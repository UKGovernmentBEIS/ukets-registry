package gov.uk.ets.registry.api.file.upload.requesteddocs.repository;

import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.file.upload.requesteddocs.domain.AccountHolderFile;
import gov.uk.ets.registry.api.file.upload.requesteddocs.domain.RequestDocumentsTaskDifference;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface AccountHolderFileRepository extends JpaRepository<AccountHolderFile, Long> {

    /**
     * This one inserts an entry to the account holder file repository upon successful account opening request
     * Using a native query to avoid refetching the already uploaded binaries
     *
     * @param fileId            the id should match the {@link gov.uk.ets.registry.api.file.upload.domain.UploadedFile#id}
     * @param document_name     The document name exists in the {@link RequestDocumentsTaskDifference#getDocumentNames()}
     * @param account_holder_id The related account holder Identifier
     */
    @Modifying
    @Query(value = "insert into account_holder_files(id,document_name,account_holder_id) values (?1, ?2, ?3)", nativeQuery = true)
    void insertAccountHolderFile(Long fileId, String document_name, Long account_holder_id);
    
	/**
     * Retrieves an account holder file by id that belongs to the specified holder.
     *
     * @param id the file id
     * @param accountHolder the specified account holder
     * @return AccountHolderFile
     */
    AccountHolderFile findByIdAndAccountHolder(Long id, AccountHolder accountHolder);
}
