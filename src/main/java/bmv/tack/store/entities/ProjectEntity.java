package bmv.tack.store.entities;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "project")
public class ProjectEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name ="id")
    private Long id;
    @Column(name = "name", unique = true )
    private String name;
    @Builder.Default
    @Column(name = "created_at")
    private Instant createdAt = Instant.now();
    @Builder.Default
    @OneToMany
    @JoinColumn(name = "project_id", referencedColumnName = "id")
     private List<TaskStateEntity> taskStateEntities = new ArrayList<>();
}
