package gov.uk.ets.keycloak.users.service.adapter.persistence;

import java.util.UUID;
import org.keycloak.models.jpa.entities.UserAttributeEntity;
import org.keycloak.models.jpa.entities.UserEntity;

public class RepositoryHelper {

    public static String UK_ETS_REALM_ENTITY_ID = "4366fbef-9ff5-4755-b74e-3d5f1438c498";

    public UserAttributeEntity createAttribute(String name, String value, UserEntity user) {
        UserAttributeEntity attributeEntity = new UserAttributeEntity();
        attributeEntity.setId(UUID.randomUUID().toString());
        attributeEntity.setName(name);
        attributeEntity.setValue(value);
        attributeEntity.setUser(user);
        return attributeEntity;
    }



}
