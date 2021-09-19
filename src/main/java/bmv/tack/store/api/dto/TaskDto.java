package bmv.tack.store.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskDto {

    @NonNull
    private Long id;

    @NonNull
    private String name;

    private String description;

    @NonNull
    @JsonProperty("created_at")
    private Instant createdAt;

}
