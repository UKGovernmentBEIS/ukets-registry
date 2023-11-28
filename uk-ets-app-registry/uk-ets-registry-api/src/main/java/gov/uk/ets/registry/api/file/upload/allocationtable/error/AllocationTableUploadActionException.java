package gov.uk.ets.registry.api.file.upload.allocationtable.error;

import gov.uk.ets.registry.api.task.service.TaskActionError;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = false, of = "allocationTableActionErrors")
public class AllocationTableUploadActionException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 1946534382523440359L;

	public static AllocationTableUploadActionException create(
	        AllocationTableActionError error) {
		AllocationTableUploadActionException exception = new AllocationTableUploadActionException();
		exception.addError(error);
		return exception;
	}

	private final List<AllocationTableActionError> allocationTableActionErrors = new ArrayList<>();


	public void addError(AllocationTableActionError error) {
		this.allocationTableActionErrors.add(error);
	}

	/**
	 * @return the error messages of its {@link TaskActionError} errors.
	 */
	@Override
	public String getMessage() {
		return allocationTableActionErrors.stream()
				.map(AllocationTableActionError::getMessage)
				.collect(Collectors.joining("\n"));
	}

}
