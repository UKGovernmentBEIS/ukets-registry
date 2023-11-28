package gov.uk.ets.registry.api.file.upload.emissionstable.error;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import gov.uk.ets.registry.api.task.service.TaskActionError;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false, of = "emissionsUploadActionErrors")
public class EmissionsUploadActionException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public static EmissionsUploadActionException create(
	        EmissionsUploadActionError error) {
		EmissionsUploadActionException exception = new EmissionsUploadActionException();
		exception.addError(error);
		return exception;
	}

	private List<EmissionsUploadActionError> emissionsUploadActionErrors = new ArrayList<>();

	public List<EmissionsUploadActionError> getEmissionsUploadActionErrors() {
		return emissionsUploadActionErrors;
	}

	public void addError(EmissionsUploadActionError error) {
		this.emissionsUploadActionErrors.add(error);
	}

	/**
	 * @return the error messages of its {@link TaskActionError} errors.
	 */
	@Override
	public String getMessage() {
		return emissionsUploadActionErrors.stream()
				.map(EmissionsUploadActionError::getMessage)
				.collect(Collectors.joining("\n"));
	}

}
