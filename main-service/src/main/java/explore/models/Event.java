package explore.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Size(min = 20, max = 2000)
    private String annotation;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @NotNull
    @Size(min = 20, max = 7000)
    private String description;

    @NotNull
    @FutureOrPresent
    @Column(name = "event_date")
    private LocalDateTime eventDate;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    @NotNull
    private Boolean paid;

    @PositiveOrZero
    @Column(name = "participant_limit")
    private int participantLimit;

    @NotNull
    @Column(name = "request_moderation")
    private Boolean requestModeration;

    @NotNull
    @Size(min = 3, max = 120)
    private String title;

    @NotNull
    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @NotNull
    @Enumerated(EnumType.STRING)
    private State state;

    @PositiveOrZero
    @Column(name = "confirmed_requests")
    private int confirmedRequests;
}
