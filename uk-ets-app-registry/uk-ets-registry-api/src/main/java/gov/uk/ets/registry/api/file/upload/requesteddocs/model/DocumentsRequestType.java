package gov.uk.ets.registry.api.file.upload.requesteddocs.model;

import gov.uk.ets.registry.api.task.domain.types.RequestType;

public enum DocumentsRequestType {
	ACCOUNT_HOLDER, 
	USER;

	public static DocumentsRequestType fromTaskRequestType(RequestType requestType) {
		if (requestType == null) {
			throw new RuntimeException("Failed to match a proper Documents Request Type.");
		}
		return switch (requestType) {
		case AH_REQUESTED_DOCUMENT_UPLOAD -> DocumentsRequestType.ACCOUNT_HOLDER;
		case AR_REQUESTED_DOCUMENT_UPLOAD -> DocumentsRequestType.USER;
		default -> throw new RuntimeException("Failed to match a proper Documents Request Type.");
		};
	}
}
