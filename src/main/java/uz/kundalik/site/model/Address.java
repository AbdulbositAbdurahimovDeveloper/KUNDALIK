package uz.kundalik.site.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import uz.kundalik.site.model.Abs.AbsAuditingSoftDeleteEntity;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "addresses")
@SQLDelete(sql = "UPDATE addresses SET deleted = true WHERE id = ?")
public class Address extends AbsAuditingSoftDeleteEntity {

    @Column(name = "address_name")
    private String addressName;

    private String country;

    @Column(nullable = false)
    private String region;

    @Column(nullable = false)
    private String district;

    @Column(name = "street_and_house")
    private String streetAndHouse;

    private String landmark;
    private Double latitude;
    private Double longitude;

    // --- Bog'lanishlar ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}