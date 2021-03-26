package dev.idm.vkp.model;

import dev.idm.vkp.db.DatabaseIdRange;
import dev.idm.vkp.model.criteria.Criteria;


public class VideoAlbumCriteria extends Criteria {

    private final int accountId;

    private final int ownerId;

    private DatabaseIdRange range;

    public VideoAlbumCriteria(int accountId, int ownerId) {
        this.accountId = accountId;
        this.ownerId = ownerId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public int getAccountId() {
        return accountId;
    }

    public DatabaseIdRange getRange() {
        return range;
    }

    public VideoAlbumCriteria setRange(DatabaseIdRange range) {
        this.range = range;
        return this;
    }
}
