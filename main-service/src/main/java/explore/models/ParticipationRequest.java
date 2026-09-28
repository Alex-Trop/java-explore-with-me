package explore.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ParticipationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "requester_id")
    private User requester;

    @NotNull
    private LocalDateTime created;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status;
}
