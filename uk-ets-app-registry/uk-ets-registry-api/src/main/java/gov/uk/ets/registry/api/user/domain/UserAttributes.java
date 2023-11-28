package gov.uk.ets.registry.api.user.domain;

/**
 * 
 * @author P35036
 * @since v0.3.0
 */
public enum UserAttributes {

	STATE("state"),
	URID("urid"),

	ALSO_KNOWN_AS("alsoKnownAs"),
	BUILDING_AND_STREET("buildingAndStreet"),
	BUILDING_AND_STREET_OPTIONAL("buildingAndStreetOptional"),
	BUILDING_AND_STREET_OPTIONAL2("buildingAndStreetOptional2"),
	COUNTRY("country"),
	COUNTRY_OF_BIRTH("countryOfBirth"),
	POST_CODE("postCode"),
	TOWN_OR_CITY("townOrCity"),
	STATE_OR_PROVINCE("stateOrProvince"),
	WORK_BUILDING_AND_STREET("workBuildingAndStreet"),
	WORK_BUILDING_AND_STREET_OPTIONAL("workBuildingAndStreetOptional"),
	WORK_BUILDING_AND_STREET_OPTIONAL2("workBuildingAndStreetOptional2"),
	WORK_COUNTRY("workCountry"),
	WORK_COUNTRY_CODE("workCountryCode"),
	WORK_PHONE_NUMBER("workPhoneNumber"),
	WORK_EMAIL_ADDRESS("workEmailAddress"),
	WORK_POST_CODE("workPostCode"),
	WORK_TOWN_OR_CITY("workTownOrCity"),
	WORK_STATE_OR_PROVINCE("workStateOrProvince"),
	YEAR_OF_BIRTH("yearOfBirth");

	private final String attributeName;

	UserAttributes(String attributeName) {
		this.attributeName = attributeName;
	}

	public String getAttributeName() {
		return attributeName;
	}
}
