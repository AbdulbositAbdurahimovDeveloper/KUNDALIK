package uz.kundalik.telegram.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uz.kundalik.site.model.Abs.AbsLongEntity;

@Getter
@Setter
@Entity
@Table(name = "message_translations", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"message_key_id", "language_id"})
})
public class MessageTranslation extends AbsLongEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "message_key_id")
    private MessageKey messageKey;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "language_id")
    private Language language;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String text; // Tarjima matni
}