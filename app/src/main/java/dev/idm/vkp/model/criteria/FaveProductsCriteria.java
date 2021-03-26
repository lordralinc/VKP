package dev.idm.vkp.model.criteria;

import dev.idm.vkp.db.DatabaseIdRange;

public class FaveProductsCriteria extends Criteria {

    private final int accountId;

    private DatabaseIdRange range;

    public FaveProductsCriteria(int accountId) {
        this.accountId = accountId;
    }

    public DatabaseIdRange getRange() {
        return range;
    }

    public FaveProductsCriteria setRange(DatabaseIdRange range) {
        this.range = range;
        return this;
    }

    public int getAccountId() {
        return accountId;
    }
}
