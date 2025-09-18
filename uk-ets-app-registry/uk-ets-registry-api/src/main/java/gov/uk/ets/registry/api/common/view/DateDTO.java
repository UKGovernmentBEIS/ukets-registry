package gov.uk.ets.registry.api.common.view;

import java.io.Serializable;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * The date transfer object.
 */
@Getter
@Setter
@EqualsAndHashCode
public class DateDTO implements Serializable {

    /**
     * Serialisation version.
     */
    private static final long serialVersionUID = -4244989617509908222L;

    /**
     * The day.
     */
    @NotNull
    @Min(value = 1, message = "Day must be between 1 and 31")
    @Max(value = 31, message = "Day must be between 1 and 31")
    private Integer day;

    /**
     * The month.
     */
    @NotNull
    @Min(value = 1, message = "Month must be between 1 and 12")
    @Max(value = 12, message = "Month must be between 1 and 12")
    private Integer month;

    /**
     * The year.
     */
    @NotNull
    @Digits(integer = 4, fraction = 0, message = "Not a valid year.")
    private Integer year;

	@Override
	public String toString() {
		return "Date{" + "day='" + day + '\'' + ", month='" + month + '\'' + ", year='" + year + '\'' + '}';
	}

}
