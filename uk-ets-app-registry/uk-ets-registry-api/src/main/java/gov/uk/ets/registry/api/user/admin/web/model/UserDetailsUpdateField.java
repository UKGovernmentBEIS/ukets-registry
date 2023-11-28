package gov.uk.ets.registry.api.user.admin.web.model;

import java.lang.annotation.*;

/**
 * 
 * UKETS-7097 - Apply this annotation to the editable fields that can be changed on the "Update User Detail" form.
 * label stands for the Field Name and is used in the User's H&C section.
 * The fields are now categorized into "major" and "minor" fields, and depending on what has been submitted for change, the system will follow a specific logic. 
 * 
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface UserDetailsUpdateField {
	String label();

	boolean isMinor() default false;

}