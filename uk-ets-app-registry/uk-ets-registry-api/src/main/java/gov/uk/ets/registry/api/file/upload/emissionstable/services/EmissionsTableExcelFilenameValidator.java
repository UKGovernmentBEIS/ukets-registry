package gov.uk.ets.registry.api.file.upload.emissionstable.services;

import java.util.EnumMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

import com.google.re2j.Matcher;
import com.google.re2j.Pattern;

import gov.uk.ets.registry.api.file.upload.emissionstable.error.EmissionsTableError;
import gov.uk.ets.registry.api.file.upload.error.FileNameNotValidException;

/**
 * This class is responsible for validating the filename uploaded by the user for
 * the emissions.
 * It can also validate any information contained in the filename (e.g. MD5 etc).
 * 
 * @author P35036
 *
 */
public class EmissionsTableExcelFilenameValidator {
	
	public enum EmissionsFilenameRegExpGroup {FILENAME,FILENAME_PREFIX,CONTENT_DATE,REGULATOR,MD5_CHECKSUM};
	private static final Pattern EMISSIONS_TABLE_FILE_NAME_PATTERN = Pattern
	        .compile(
	                "^(UK_EMISSIONS)_(\\d{8})_(EA|NRW|SEPA|DAERA|OPRED)_(\\w{32})[_[\\w\\.]*]?$");
	private final Map<EmissionsFilenameRegExpGroup, String> fileNameGroups = new EnumMap<>(
	        EmissionsFilenameRegExpGroup.class);

	/**
	 *
	 * @param fileName
	 *            the file name
	 */
	public EmissionsTableExcelFilenameValidator(String fileName) {

		Matcher matcher = EMISSIONS_TABLE_FILE_NAME_PATTERN
		        .matcher(FilenameUtils.getBaseName(fileName.toUpperCase()));

		if (!matcher.matches()) {
			throw new FileNameNotValidException(EmissionsTableError.ERROR_5002.getMessageTemplate());
		} else {

			fileNameGroups.put(EmissionsFilenameRegExpGroup.FILENAME,
			        matcher.group(0));
			fileNameGroups.put(EmissionsFilenameRegExpGroup.FILENAME_PREFIX,
			        matcher.group(1));
			fileNameGroups.put(EmissionsFilenameRegExpGroup.CONTENT_DATE,
			        matcher.group(2));
			fileNameGroups.put(EmissionsFilenameRegExpGroup.REGULATOR,
			        matcher.group(3).replace( '-','_'));//Due to JIRA UKETS-5775
			fileNameGroups.put(EmissionsFilenameRegExpGroup.MD5_CHECKSUM,
			        matcher.group(4));
		}

	}

	/**
	 * Checks if the MD5 is valid.
	 * 
	 */
	public void validateMD5Checksum(String md5Checksum) {
		if (!fileNameGroups.get(EmissionsFilenameRegExpGroup.MD5_CHECKSUM).equalsIgnoreCase(md5Checksum)) {
			throw new FileNameNotValidException(EmissionsTableError.ERROR_5004.getMessageTemplate());
		}
	}
	
	public String getRegExpGroup(EmissionsFilenameRegExpGroup group) {
		return fileNameGroups.get(group);
	}
	
	public Map<EmissionsFilenameRegExpGroup,String> getRegExpGroupsMap() {
	  	Map<EmissionsFilenameRegExpGroup,String> groups = new EnumMap<>(EmissionsFilenameRegExpGroup.class);
	  	
	  	for(EmissionsFilenameRegExpGroup key :EmissionsFilenameRegExpGroup.values()) {
	  		groups.put(key, getRegExpGroup(key));
	  	}
	  	
		return groups;
	}
}
