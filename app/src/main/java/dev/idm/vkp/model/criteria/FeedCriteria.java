package dev.idm.vkp.model.criteria;

import dev.idm.vkp.db.DatabaseIdRange;

public class FeedCriteria extends Criteria {

    private final int accountId;

    private DatabaseIdRange range;

    public FeedCriteria(int accountId) {
        this.accountId = accountId;
    }

    public DatabaseIdRange getRange() {
        return range;
    }

    public FeedCriteria setRange(DatabaseIdRange range) {
        this.range = range;
        return this;
    }

    public int getAccountId() {
        return accountId;
    }
}
