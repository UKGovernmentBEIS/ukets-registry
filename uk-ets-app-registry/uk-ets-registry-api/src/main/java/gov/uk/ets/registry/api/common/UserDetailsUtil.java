package gov.uk.ets.registry.api.common;

import gov.uk.ets.registry.api.user.admin.web.model.UserDetailsDTO;
import gov.uk.ets.registry.api.user.admin.web.model.UserDetailsUpdateField;
import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.StringJoiner;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;


@Log4j2
public class UserDetailsUtil {

    public static final DateTimeFormatter CRC_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
            .withZone(ZoneOffset.UTC);

    private UserDetailsUtil() {
    }
    
    public static final LocalDateTime userCRCIssuanceDateToDateTime(java.util.Date crcIssuanceDate) {
        LocalDateTime crcDate = crcIssuanceDate
                .toInstant()
                .atZone(ZoneId.of("UTC"))
                .toLocalDateTime()
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
        return crcDate;
    }
    
    
    public static final LocalDateTime userCRCIssuanceDateToDateTime(String crcIssuanceDate) {
        LocalDateTime crcDate = Instant
                .parse(crcIssuanceDate)
                .atZone(ZoneId.of("UTC"))
                .toLocalDateTime()
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
        return crcDate;
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
