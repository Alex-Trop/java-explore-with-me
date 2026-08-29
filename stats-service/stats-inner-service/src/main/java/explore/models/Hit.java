package explore.models;

import dto.App;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(exclude = "id")
@Entity
@Table(name = "hits")
public class Hit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NonNull
    @Enumerated(EnumType.STRING)
    private App app;

    @NonNull
    private String uri;

    @NonNull
    private String ip;

    @NonNull
    @Column(name = "created")
    private LocalDateTime timestamp;
}
