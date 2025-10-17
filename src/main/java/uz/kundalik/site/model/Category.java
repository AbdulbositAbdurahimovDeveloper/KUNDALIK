package uz.kundalik.site.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.type.SqlTypes;
import uz.kundalik.site.enums.CategoryType;
import uz.kundalik.site.model.Abs.AbsAuditingSoftDeleteEntity;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categories")
@SQLDelete(sql = "UPDATE categories SET deleted = true WHERE id = ?")
public class Category extends AbsAuditingSoftDeleteEntity {

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "category_type", nullable = false)
    @JdbcTypeCode(SqlTypes.VARCHAR) // <-- YECHIM: Eng ishonchli usul
    private CategoryType categoryType;

    private String iconName;
    private String colorCode;

    // --- Bog'lanishlar ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // Tizim kategoriyalari uchun NULL bo'lishi mumkin
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id")
    private Category parentCategory;

    @OneToMany(mappedBy = "parentCategory", fetch = FetchType.LAZY)
    private List<Category> childCategories;
}