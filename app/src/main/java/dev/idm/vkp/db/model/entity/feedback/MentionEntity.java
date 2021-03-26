package dev.idm.vkp.db.model.entity.feedback;

import dev.idm.vkp.db.model.entity.Entity;
import dev.idm.vkp.db.model.entity.EntityWrapper;

/**
 * Base class for types [mention]
 */
public class MentionEntity extends FeedbackEntity {

    private EntityWrapper where = EntityWrapper.empty();

    public MentionEntity(int type) {
        super(type);
    }

    public Entity getWhere() {
        return where.get();
    }

    public MentionEntity setWhere(Entity where) {
        this.where = new EntityWrapper(where);
        return this;
    }
}