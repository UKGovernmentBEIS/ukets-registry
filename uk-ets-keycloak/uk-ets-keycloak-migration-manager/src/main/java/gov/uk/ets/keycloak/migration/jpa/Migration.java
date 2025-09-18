package gov.uk.ets.keycloak.migration.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "UK_ETS_MIGRATION")
public class Migration {

    @Id
    @Column(name = "MIGRATION_KEY")
    private String key;

    @Column(name = "MIGRATION_DATE")
    private String migrationDate;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMigrationDate() {
        return migrationDate;
    }

    public void setMigrationDate(String migrationDate) {
        this.migrationDate = migrationDate;
    }

    @Override
    public String toString() {
        return "Migration{" +
            "key='" + key + '\'' +
            ", migrationDate='" + migrationDate + '\'' +
            '}';
    }
}
