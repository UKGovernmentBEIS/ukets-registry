package gov.uk.ets.reports.generator.kyotoprotocol.sef.processors.cp2;

import java.util.Map;

import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.SEFSubmission;

/**
 * @author gkountak
 *
 */
public class InitializeSubmissionsProcessor extends AbstractTransactionProcessor {
	
	public Map<String, SEFSubmission> process(Map<String, SEFSubmission> sefSubmissions) {
		// TODO Auto-generated method stub
		for (String registryCode : sefSubmissions.keySet()) {
			sefSubmissions.put(registryCode, super.createSubmission(registryCode));
		}
		return sefSubmissions;
	}

	
}
