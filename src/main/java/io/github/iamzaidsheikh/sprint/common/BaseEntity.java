package io.github.iamzaidsheikh.sprint.common;

import java.time.Instant;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
public class BaseEntity {
    @Field(name = "created_at")
    private Instant createdAt;

    @Field(name = "last_modified")
    private Instant lastModified;
}
