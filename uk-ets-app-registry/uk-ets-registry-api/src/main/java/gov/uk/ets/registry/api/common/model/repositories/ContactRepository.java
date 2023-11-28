package gov.uk.ets.registry.api.common.model.repositories;

import gov.uk.ets.registry.api.common.model.entities.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Data repository for contacts.
 */
public interface ContactRepository extends JpaRepository<Contact, Long> {

    // nothing to implement here at the moment.

}
