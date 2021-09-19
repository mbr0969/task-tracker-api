package bmv.tack.store.repositories;

import bmv.tack.store.entities.TaskStateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskStateRepository extends JpaRepository<TaskStateEntity,Long> {
}
