package io.github.iamzaidsheikh.sprint.listener;

import java.time.Instant;

import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent;
import org.springframework.stereotype.Component;

import io.github.iamzaidsheikh.sprint.common.BaseEntity;

@Component
public class BeforeSaveEventListener extends AbstractMongoEventListener<BaseEntity> {

    @Override
    public void onBeforeSave(BeforeSaveEvent<BaseEntity> event) {
        Instant timestamp = Instant.now();
        if (event.getSource().getCreatedAt() == null)
            event.getSource().setCreatedAt(timestamp);
        event.getSource().setLastModified(timestamp);
        super.onBeforeSave(event);
    }

}
