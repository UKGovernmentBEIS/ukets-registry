package gov.uk.ets.registry.api.user.repository;

import gov.uk.ets.registry.api.file.upload.wrappers.BulkArUserDTO;
import gov.uk.ets.registry.api.user.EnrolmentKeyDTO;
import gov.uk.ets.registry.api.user.UserDTO;
import gov.uk.ets.registry.api.user.UserFileDTO;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Data repository for users.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Retrieves a user based on his unique business identifier.
     *
     * @param urid The unique business identifier.
     * @return a user
     */
    User findByUrid(String urid);

    /**
     * Retrieves a list of {@link UserDTO users} based on a list of urids.
     *
     * @param urids The unique business identifiers.
     * @return a list of users based on urids
     */
    @Query(value =
        "select distinct new gov.uk.ets.registry.api.user.UserDTO(u.urid, u.state, u.firstName, u.lastName, u.knownAs) " +
            "from User u " +
            "where u.urid in ?1")
    List<UserDTO> findByUrids(List<String> urids);

    /**
     * Retrieves all users whose first name or last name starts with the passed
     * term ignoring case.
     *
     * @param term The search term
     * @return a list of users
     */

    @Query(value =
        "select u from User u " +
            "where " +
            "(" +
            "lower(u.firstName) like lower(concat(:term, '%')) " +
            "or " +
            "lower(u.lastName) like lower(concat(:term, '%')) " +
            ") and u.state = 'ENROLLED'")
    List<User> getUsersByNameStartingWith(@Param("term") String term);

    /**
     * Retrieves a user based on his unique IAM identifier.
     *
     * @param iamIdentifier The unique IAM identifier.
     * @return a user
     */
    User findByIamIdentifier(String iamIdentifier);

    /**
     * Retrieves the users related to this account holder.
     *
     * @param accountHolderIdentifier The account holder identifier.
     * @param userId                  The id of the associated user.
     * @return a list of users
     */
    @Query(value = """
        select new gov.uk.ets.registry.api.user.UserDTO(u0.urid, u0.state, u0.firstName, u0.lastName, u0.knownAs)
        from User u0 where u0.id in
           (select u1.id
               from AccountAccess acs
                 join acs.user u1
                 join acs.account acc
                 join acc.accountHolder hol
               where (
                        (
                            :userId is null
                            and :accountHolderIdentifier is not null
                            and hol.identifier = :accountHolderIdentifier
                        )
                        or
                        (
                            :userId is not null
                        )            
               )
               and hol.type <> gov.uk.ets.registry.api.account.domain.types.AccountHolderType.GOVERNMENT
               and acs.state =  gov.uk.ets.registry.api.account.domain.types.AccountAccessState.ACTIVE
               and acs.right <> gov.uk.ets.registry.api.account.domain.types.AccountAccessRight.ROLE_BASED
               and (
                   :userId is null or acc.id in (
                        select acc2 from AccountAccess acs2
                          join acs2.user u2
                          join acs2.account acc2
                          join acc2.accountHolder hol2
                        where u2.id = :userId
                   )
                       
               )
           )
        and (:userId is null or u0.id <> :userId)
        and u0.state <> gov.uk.ets.registry.api.user.domain.UserStatus.SUSPENDED
        and u0.state <> gov.uk.ets.registry.api.user.domain.UserStatus.DEACTIVATED
        and u0.state <> gov.uk.ets.registry.api.user.domain.UserStatus.DEACTIVATION_PENDING
        """)
    List<UserDTO> getAccountHolderArs(Long accountHolderIdentifier, Long userId);

    /**
     * Retrieves the enrolment key information from users table for the specific user.
     *
     * @param urid The user identifier
     * @return The enrolment key information
     */
    @Query(value =
        "select new gov.uk.ets.registry.api.user.EnrolmentKeyDTO(u.urid, u.enrolmentKey, u.enrolmentKeyDate) " +
            "   from User u" +
            "   where u.urid = ?1")
    EnrolmentKeyDTO getEnrolmentKeyDetails(String urid);

    /**
     * Retrieves the list of user files.
     *
     * @param urid The user identifier
     * @return The list of files
     */
    @Query(value =
        "select new gov.uk.ets.registry.api.user.UserFileDTO(uf.id, uf.uploadedFile.fileName, uf.documentName, uf.uploadedFile.creationDate) " +
            "   from UserFile uf" +
            "   join uf.user u " +
            "   where u.urid = ?1 and uf.uploadedFile.fileStatus='SUBMITTED'")
    List<UserFileDTO> getUserFiles(String urid);

    /**
     * Retrieves the users.
     *
     * @return A list of BulkArUserDTO objects.
     */
    @Query(value = "select new gov.uk.ets.registry.api.file.upload.wrappers.BulkArUserDTO(u.urid, u.state) from User u ")
    List<BulkArUserDTO> retrieveUsersByUrid();

    /**
     * Retrieves all users that have specific role (by identifier).
     */
    List<User> findAllByUserRoles_Role_IamIdentifier(String roleIdentifier);

    /**
     * Retrieves all users that belong in one of the roles of the collection.
     * @param userRoles the list of the user roles.
     * @return a list of users.
     */
    @Query(value = "select u from User u join u.userRoles ur where ur.role.roleName in ?1")
    List<User> findUsersByRoleName(List<String> userRoles);

    /**
     * Retrieves all users that belong in one of the roles of the collection and in one of the statuses provided.
     * @param userRoles the list of the user roles.
     * @param statuses the list of the user statuses.
     * @return a list of users.
     */
    @Query(value = "select u from User u join u.userRoles ur where ur.role.roleName in ?1 and u.state in ?2")
    List<User> findUsersByRoleNameAndState(List<String> userRoles, List<UserStatus> statuses);

    /**
     * Retrieves all users that have the known_as attribute already set.
     */
    List<User> findByKnownAsNotNull();
}
