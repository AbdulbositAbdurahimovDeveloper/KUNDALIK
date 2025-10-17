package uz.kundalik.site.validation.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;
import uz.kundalik.site.validation.annotations.DateRange;

import java.time.LocalDate;

public class DateRangeValidator implements ConstraintValidator<DateRange, Object> {
    private String startDateField;
    private String endDateField;

    @Override
    public void initialize(DateRange constraintAnnotation) {
        this.startDateField = constraintAnnotation.startDateField();
        this.endDateField = constraintAnnotation.endDateField();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        Object startDateObj = new BeanWrapperImpl(value).getPropertyValue(startDateField);
        Object endDateObj = new BeanWrapperImpl(value).getPropertyValue(endDateField);

        if (startDateObj == null || endDateObj == null) {
            return true; // Let @NotNull handle this
        }

        LocalDate startDate = (LocalDate) startDateObj;
        LocalDate endDate = (LocalDate) endDateObj;

        return !endDate.isBefore(startDate);
    }
}