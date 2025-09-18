package gov.uk.ets.reports.generator.keycloak;

import gov.uk.ets.reports.generator.domain.KeycloakUser;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KeycloakDbService {

    private final JdbcTemplate keycloakJdbcTemplate;

    /**
     * Returns a map which contains
     * as key: the keycloak user id and
     * as value a String set set with all the keycloak user roles.
     * The set contains all the roles found in keycloak db, and not only those known
     * to the registry-api.
     *
     * @return a string,Set<String>  hashmap
     */
    public Map<String, Set<String>> getKeycloakUsersRolesMap() {
        Map<String, Set<String>> map = new HashMap<>();

        keycloakJdbcTemplate.query(
            "select ue.id, kr.name" +
                " from keycloak_role kr," +
                "     user_role_mapping urm," +
                "     user_entity ue" +
                " where ue.id = urm.user_id" +
                "  and urm.role_id = kr.id",
            resultSet -> {
                while (resultSet.next()) {
                    String id = resultSet.getString(1);
                    String role = resultSet.getString(2);
                    if (map.containsKey(id)) {
                        Set<String> userRoles = map.get(id);
                        userRoles.add(role);
                    } else {
                        Set<String> roles = new HashSet<>();
                        roles.add(role);
                        map.put(id, roles);
                    }
                }
                return null;
            });
        return map;
    }

    public List<KeycloakUser> retrieveAllUsers() {
        String allUsersQuery = "select ua_urid.value         as urid,\n" +
            "       ue.id                 as userKeycloakId,\n" +
            "       ue.created_timestamp  as createdOn,\n" +
            "       ua_rod.value          as registeredOn,\n" +
            "       ua_ll.value           as lastLoginOn,\n" +
            "       ue.username           as username,\n" +
            "       ue.email              as email,\n" +
            "       ua_status.value       as status,\n" +
            "       ue.first_name         as firstName,\n" +
            "       ue.last_name          as LastName,\n" +
            "       ua_aka.value          as alsoKnownAs,\n" +
            "       ua_bdate.value        as birthDate,\n" +
            "       ua_country.value      as country,\n" +
            "       ua_country_of_b.value as countryOfBirth,\n" +
            "       ua_bas.value          as buildingAndStreet,\n" +
            "       ua_baso.value         as buildingAndStreetOptional,\n" +
            "       ua_baso2.value        as buildingAndStreetOptional2,\n" +
            "       ua_pcode.value        as postCode,\n" +
            "       ua_toc.value          as townOrCity,\n" +
            "       ua_sorp.value         as stateOrProvince,\n" +
            "       ua_wc.value           as workCountry,\n" +
            "       ua_wmcc.value         as workMobileCountryCode,\n" +
            "       ua_wmpn.value         as workMobilePhoneNumber,\n" +
            "       ua_wacc.value         as workAlternativeCountryCode,\n" +
            "       ua_wapn.value         as workAlternativePhoneNumber,\n" +
            "       ua_nmpnr.value        as noMobilePhoneNumberReason,\n" +
            "       ua_wbas.value         as workBuildingAndStreet,\n" +
            "       ua_wbaso.value        as workBuildingAndStreetOptional,\n" +
            "       ua_wbaso2.value       as workBuildingAndStreetOptional2,\n" +
            "       ua_wpc.value          as workPostCode,\n" +
            "       ua_wtoc.value         as workTownOrCity, \n" +
            "       ua_wsop.value         as workStateOrProvince\n" +
            "from user_entity ue\n" +
            "         left join user_attribute ua_urid on ue.id = ua_urid.user_id\n" +
            "    and ua_urid.name = 'urid'\n" +
            "         left join user_attribute ua_rod on ue.id = ua_rod.user_id\n" +
            "    and ua_rod.name = 'registeredOnDate'\n" +
            "         left join user_attribute ua_ll on ue.id = ua_ll.user_id\n" +
            "    and ua_ll.name = 'lastLoginDate'\n" +
            "         left join user_attribute ua_aka on ue.id = ua_aka.user_id\n" +
            "    and ua_aka.name = 'alsoKnownAs'\n" +
            "         left join user_attribute ua_status on ue.id = ua_status.user_id\n" +
            "    and ua_status.name = 'state'\n" +
            "         left join user_attribute ua_bdate on ue.id = ua_bdate.user_id\n" +
            "    and ua_bdate.name = 'birthDate'\n" +
            "         left join user_attribute ua_country on ue.id = ua_country.user_id\n" +
            "    and ua_country.name = 'country'\n" +
            "         left join user_attribute ua_country_of_b on ue.id = ua_country_of_b.user_id\n" +
            "    and ua_country_of_b.name = 'countryOfBirth'\n" +
            "         left join user_attribute ua_bas on ue.id = ua_bas.user_id\n" +
            "    and ua_bas.name = 'buildingAndStreet'\n" +
            "         left join user_attribute ua_baso on ue.id = ua_baso.user_id\n" +
            "    and ua_baso.name = 'buildingAndStreetOptional'\n" +
            "         left join user_attribute ua_baso2 on ue.id = ua_baso2.user_id\n" +
            "    and ua_baso2.name = 'buildingAndStreetOptional2'\n" +
            "         left join user_attribute ua_pcode on ue.id = ua_pcode.user_id\n" +
            "    and ua_pcode.name = 'postCode'\n" +
            "         left join user_attribute ua_toc on ue.id = ua_toc.user_id\n" +
            "    and ua_toc.name = 'townOrCity'\n" +
            "         left join user_attribute ua_sorp on ue.id = ua_sorp.user_id\n" +
            "    and ua_sorp.name = 'stateOrProvince'\n" +
            "         left join user_attribute ua_wc on ue.id = ua_wc.user_id\n" +
            "    and ua_wc.name = 'workCountry'\n" +
            "         left join user_attribute ua_wmcc on ue.id = ua_wmcc.user_id\n" +
            "    and ua_wmcc.name = 'workMobileCountryCode'\n" +
            "         left join user_attribute ua_wmpn on ue.id = ua_wmpn.user_id\n" +
            "    and ua_wmpn.name = 'workMobilePhoneNumber'\n" +
            "         left join user_attribute ua_wacc on ue.id = ua_wacc.user_id\n" +
            "    and ua_wacc.name = 'workAlternativeCountryCode'\n" +
            "         left join user_attribute ua_wapn on ue.id = ua_wapn.user_id\n" +
            "    and ua_wapn.name = 'workAlternativePhoneNumber'\n" +
            "         left join user_attribute ua_nmpnr on ue.id = ua_nmpnr.user_id\n" +
            "    and ua_nmpnr.name = 'noMobilePhoneNumberReason'\n" +
            "         left join user_attribute ua_wbas on ue.id = ua_wbas.user_id\n" +
            "    and ua_wbas.name = 'workBuildingAndStreet'\n" +
            "         left join user_attribute ua_wbaso on ue.id = ua_wbaso.user_id\n" +
            "    and ua_wbaso.name = 'workBuildingAndStreetOptional'\n" +
            "         left join user_attribute ua_wbaso2 on ue.id = ua_wbaso2.user_id\n" +
            "    and ua_wbaso2.name = 'workBuildingAndStreetOptional2'\n" +
            "         left join user_attribute ua_wpc on ue.id = ua_wpc.user_id\n" +
            "    and ua_wpc.name = 'workPostCode'\n" +
            "         left join user_attribute ua_wtoc on ue.id = ua_wtoc.user_id\n" +
            "    and ua_wtoc.name = 'workTownOrCity'\n" +
            "         left join user_attribute ua_wsop on ue.id = ua_wsop.user_id\n" +
            "    and ua_wsop.name = 'workStateOrProvince'\n" +
            "where ua_urid.value is not null\n" +
            "order by urid;\n";

        return keycloakJdbcTemplate.query(allUsersQuery, new KeycloakUserJdbcMapper());
    }

    static class KeycloakUserJdbcMapper implements RowMapper<KeycloakUser> {
        @Override
        public KeycloakUser mapRow(ResultSet rs, int rowNum) throws SQLException {
            long createdOn = rs.getLong("createdOn");
            String registeredOn = rs.getString("registeredOn");
            String lastLoginOn = rs.getString("lastLoginOn");
            return KeycloakUser.builder()
                .urid(rs.getString("urid"))
                .keycloakUserId(rs.getString("userKeycloakId"))
                .createdOn(
                    createdOn != 0 ? Instant.ofEpochMilli(createdOn).atZone(ZoneId.of("UTC")).toLocalDateTime() : null
                )
                .registeredOn(registeredOn != null ? LocalDateTime.parse(registeredOn) : null)
                .lastLoginOn(lastLoginOn != null ? LocalDateTime.parse(lastLoginOn) : null)
                .username(rs.getString("username"))
                .email(rs.getString("email"))
                .status(rs.getString("status"))
                .firstName(rs.getString("firstName"))
                .lastName(rs.getString("lastName"))
                .alsoKnownAs(rs.getString("alsoKnownAs"))
                .birthDate(rs.getString("birthDate"))
                .country(rs.getString("country"))
                .countryOfBirth(rs.getString("countryOfBirth"))
                .buildingAndStreet(rs.getString("buildingAndStreet"))
                .buildingAndStreetOptional(rs.getString("buildingAndStreetOptional"))
                .buildingAndStreetOptional2(rs.getString("buildingAndStreetOptional2"))
                .postCode(rs.getString("postCode"))
                .townOrCity(rs.getString("townOrCity"))
                .stateOrProvince(rs.getString("stateOrProvince"))
                .workCountry(rs.getString("workCountry"))
                .workMobileCountryCode(rs.getString("workMobileCountryCode"))
                .workMobilePhoneNumber(rs.getString("workMobilePhoneNumber"))
                .workAlternativeCountryCode(rs.getString("workAlternativeCountryCode"))
                .workAlternativePhoneNumber(rs.getString("workAlternativePhoneNumber"))
                .noMobilePhoneNumberReason(rs.getString("noMobilePhoneNumberReason"))
                .workBuildingAndStreet(rs.getString("workBuildingAndStreet"))
                .workBuildingAndStreetOptional(rs.getString("workBuildingAndStreetOptional"))
                .workBuildingAndStreetOptional2(rs.getString("workBuildingAndStreetOptional2"))
                .workPostCode(rs.getString("workPostCode"))
                .workTownOrCity(rs.getString("workTownOrCity"))
                .workStateOrProvince(rs.getString("workStateOrProvince"))
                .build();
        }
    }
}
