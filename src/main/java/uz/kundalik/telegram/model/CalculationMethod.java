package uz.kundalik.telegram.model;

import jakarta.persistence.*;
import lombok.*;
import uz.kundalik.site.model.Abs.AbsDateEntity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "calculation_methods")
public class CalculationMethod extends AbsDateEntity {

    @Column(name = "api_method_id", unique = true, nullable = false)
    private Integer apiMethodId; // Aladhan API dagi ID (masalan, 3)

    @Column(name = "name", nullable = false)
    private String name; // Qisqa nom (masalan, "Muslim World League")

    @Column(name = "description", length = 512)
    private String description; // To'liqroq tavsif

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true; // Admin tomonidan faollashtirilganmi?
}