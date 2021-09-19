package bmv.tack.store.entities;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "task")
public class TaskEntity {

    @Id
    @Column(name ="id")
    private Long id;

    @Column(name = "name", unique = true )
    private String name;

    @Column(name ="description")
    private String description;

    @Builder.Default
    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

}
