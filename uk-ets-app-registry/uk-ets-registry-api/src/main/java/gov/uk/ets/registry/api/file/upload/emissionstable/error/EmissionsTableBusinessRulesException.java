package gov.uk.ets.registry.api.file.upload.emissionstable.error;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("serial")
public class EmissionsTableBusinessRulesException extends RuntimeException {

    private final List<EmissionsUploadBusinessError> emissionsTableErrorList = new ArrayList<>();

    public static EmissionsTableBusinessRulesException create(EmissionsUploadBusinessError emissionsTableError) {
    	EmissionsTableBusinessRulesException emissionsTableBusinessRulesException =new EmissionsTableBusinessRulesException();
        emissionsTableBusinessRulesException.addError(emissionsTableError);
        return emissionsTableBusinessRulesException;
    }

    public void addError(EmissionsUploadBusinessError error) {
        this.emissionsTableErrorList.add(error);
    }

    public void addErrors(Collection<EmissionsUploadBusinessError> error) {
        this.emissionsTableErrorList.addAll(error);
    }
    
}
