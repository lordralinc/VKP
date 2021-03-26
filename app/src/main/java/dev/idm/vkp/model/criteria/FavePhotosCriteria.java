package dev.idm.vkp.model.criteria;

import dev.idm.vkp.db.DatabaseIdRange;

public class FavePhotosCriteria extends Criteria {

    private final int accountId;

    private DatabaseIdRange range;

    public FavePhotosCriteria(int accountId) {
        this.accountId = accountId;
    }

    public int getAccountId() {
        return accountId;
    }

    public DatabaseIdRange getRange() {
        return range;
    }

    public FavePhotosCriteria setRange(DatabaseIdRange range) {
        this.range = range;
        return this;
    }
}
