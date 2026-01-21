package gov.uk.ets.registry.api.file.upload.emissionstable.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EmissionsTableError {

	/**
	 * Invalid filename
	 */
	ERROR_5002(5002,"The filename must start ‘UK_Emissions_<DDMMYYYY>_<Regulator>_'"),
	
	/**
	 * Invalid excel template - columns missing
	 */
	ERROR_5003(5003,"%s column is missing"),
	
	/**
	 * Invalid MD5 checksum for the given contents
	 */
	ERROR_5004(5004,"The MD5 checksum is not valid for the given contents"),
	
	/**
	 * Installation / aircraft operator ID does not exist in Registry
	 */
	ERROR_5005(5005,"The Operator does not exist. Enter a valid Operator ID"),
	
	/**
	 * 	Installation / aircraft operator ID not linked to an account
	 */
	ERROR_5006(5006,"The Operator is not linked to an account. Remove the ID from the file or enter a different Operator ID"),
	
	/**
	 * The status of the account to which the Installation / aircraft operator ID is linked 
	 * is is in an improper status (either CLOSED or TRANSFER PENDING)
	 */
	ERROR_5007(5007,"The Operator ID cannot be added as it is linked to an account with a Closed or Transfer Pending status"),
	
	/**
	 * Invalid quantity for emissions- only zero or positive values are permitted
	 */
	ERROR_5008(5008,"The quantity of emissions included for %s must be a zero or positive value"),
	
	/**
	 * Invalid year i.e. instead of a year value someone submits the text 'Two thousand twenty'
	 */
	ERROR_5009(5009,"The year must be in the correct format, for example 2021"),
	
	/** Year outside current phase */
	ERROR_5010(5010,"The year must be within the current phase (2021 to 2030)"),
	
	/**
	 * Invalid year - cannot upload emissions for future years
	 */
	ERROR_5011(5011,"Emissions cannot be reported for future years"),
	
	/**
	 * Invalid year - cannot upload emissions for current year <b>unless the Last year of verified emissions = current year</b>
	 */
    @Deprecated(forRemoval = true)
	ERROR_5012(5012,"Emissions cannot be reported for the current year"),
	
	/**
	 * Installation/Aircraft operator not operational for the year reported in the emissions table 
	 * (First year of verified emissions submission <= Emission year <= Last year of verified emissions submission
	 */
	ERROR_5013(5013,"The Operator was not operational for the selected year"),
	
	/** Empty mandatory columns */
	ERROR_5014(5014,"Column %s must not be empty"),
	
	/**
	 * 	Duplicate entries exist in the excel (the same installation /aircraft operator ID for the same year exists more than once) - error to be displayed 
	 * only for the duplicate row (not for the first row on which emissions for the same year and operator are displayed)
	 */
	ERROR_5015(5015,"There are duplicate entries for the Operator ID for this year"),
	
	/** Emissions cannot be reported for a specific year when an installation/aircraft operator is excluded */
	ERROR_5016(5016,"The Operator cannot report emissions");
	
	private final int code;
	private final String messageTemplate;
}
