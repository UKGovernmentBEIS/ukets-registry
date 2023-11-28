package gov.uk.ets.registry.api.common;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.StringJoiner;

import org.apache.commons.lang3.StringUtils;

import gov.uk.ets.registry.api.user.admin.web.model.UserDetailsDTO;
import gov.uk.ets.registry.api.user.admin.web.model.UserDetailsUpdateField;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class UserDetailsUtil {

	private UserDetailsUtil() {
	}

	public static boolean majorUserDetailsUpdateRequested(UserDetailsDTO changedDto) {
		for (Field f : changedDto.getClass().getDeclaredFields()) {
			UserDetailsUpdateField annotation = f.getAnnotation(UserDetailsUpdateField.class);
			try {
				f.setAccessible(true);
				if (annotation != null && !annotation.isMinor()) {
					Object newValue = f.get(changedDto);
					if (newValue != null) {
						return true;
					}
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				log.error("Failed to evaluate the field expression: {}", e.getMessage());
			}
		}
		return false;
	}

	public static String generateUserDetailsUpdateComment(UserDetailsDTO currentDto, UserDetailsDTO changedDto) {
		if (currentDto == null || changedDto == null) {
			return null;
		}

		StringJoiner joiner = new StringJoiner(", ");

		for (Field changedField : changedDto.getClass().getDeclaredFields()) {
			UserDetailsUpdateField annotation = changedField.getAnnotation(UserDetailsUpdateField.class);
			if (annotation != null) {
				try {
					changedField.setAccessible(true);
					Object newValue = changedField.get(changedDto);
					if (newValue != null) {
						Field currentField = currentDto.getClass().getDeclaredField(changedField.getName());
						currentField.setAccessible(true);
						Object oldValue = currentField.get(currentDto);
						joiner.add(constructUserDetailsUpdateComment(annotation.label(), oldValue, newValue));
					}
				} catch (Exception e) {
					log.error("Failed to evaluate the field expression: {}", e.getMessage());
				}
			}
		}
		return StringUtils.truncate(joiner.toString(), 1000);
	}

	private static String constructUserDetailsUpdateComment(String label, Object oldValue, Object newValue) {
		return String.format("%s change from %s to %s", label, 
				Optional.ofNullable(oldValue).filter(obj -> !obj.equals("")).orElse("-"),
				Optional.ofNullable(newValue).filter(obj -> !obj.equals("")).orElse("-"));
	}
}
