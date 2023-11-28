package gov.uk.ets.registry.api.user.admin.web;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSearchByNameResultDTO implements Serializable {

	private static final long serialVersionUID = -824974102569975943L;
	private String fullName;
    private String urid;
	private String knownAs;
	private String displayName;
}
