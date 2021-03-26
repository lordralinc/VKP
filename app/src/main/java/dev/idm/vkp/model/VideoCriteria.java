package dev.idm.vkp.model;

import dev.idm.vkp.db.DatabaseIdRange;
import dev.idm.vkp.model.criteria.Criteria;


public class VideoCriteria extends Criteria {

    private final int accountId;

    private final int ownerId;

    private final int albumId;

    private DatabaseIdRange range;

    public VideoCriteria(int accountId, int ownerId, int albumId) {
        this.accountId = accountId;
        this.ownerId = ownerId;
        this.albumId = albumId;
    }

    public int getAccountId() {
        return accountId;
    }

    public int getAlbumId() {
        return albumId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public DatabaseIdRange getRange() {
        return range;
    }

    public VideoCriteria setRange(DatabaseIdRange range) {
        this.range = range;
        return this;
    }
}
