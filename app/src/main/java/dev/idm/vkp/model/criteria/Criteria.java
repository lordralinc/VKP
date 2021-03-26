package dev.idm.vkp.model.criteria;

import org.jetbrains.annotations.NotNull;

public class Criteria implements Cloneable {

    @NotNull
    @Override
    public Criteria clone() throws CloneNotSupportedException {
        return (Criteria) super.clone();
    }
}
