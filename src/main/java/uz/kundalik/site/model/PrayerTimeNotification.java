package uz.kundalik.site.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import uz.kundalik.site.model.Abs.AbsLongEntity;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "prayer_time_notifications")
public class PrayerTimeNotification extends AbsLongEntity {

    @Column(name = "fajr_enabled", nullable = false)
    private boolean fajrEnabled = false;

    @Column(name = "fajr_offset_minutes", nullable = false)
    private int fajrOffsetMinutes = -10;

    @Column(name = "sunrise_enabled", nullable = false)
    private boolean sunriseEnabled = false;

    @Column(name = "sunrise_offset_minutes", nullable = false)
    private int sunriseOffsetMinutes = 0;

    @Column(name = "dhuhr_enabled", nullable = false)
    private boolean dhuhrEnabled = false;

    @Column(name = "dhuhr_offset_minutes", nullable = false)
    private int dhuhrOffsetMinutes = -10;

    @Column(name = "asr_enabled", nullable = false)
    private boolean asrEnabled = false;

    @Column(name = "asr_offset_minutes", nullable = false)
    private int asrOffsetMinutes = -10;

    @Column(name = "maghrib_enabled", nullable = false)
    private boolean maghribEnabled = false;

    @Column(name = "maghrib_offset_minutes", nullable = false)
    private int maghribOffsetMinutes = -10;

    @Column(name = "isha_enabled", nullable = false)
    private boolean ishaEnabled = false;

    @Column(name = "isha_offset_minutes", nullable = false)
    private int ishaOffsetMinutes = -10;

    @UpdateTimestamp
    private Timestamp updatedAt;

    // --- Bog'lanishlar ---
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
}