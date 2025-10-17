package uz.kundalik.site.model.Abs; // Sizning paketingiz

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.Where;

@Getter
@Setter
@MappedSuperclass
@FieldNameConstants
@SQLRestriction(value = "deleted = false")
//@Where(clause = "deleted = false") // Barcha 'find' so'rovlariga avtomatik shu shartni qo'shadi
public abstract class AbsAuditingSoftDeleteEntity extends AbsDateEntity {

    @Column(nullable = false)
    private boolean deleted = false;
}