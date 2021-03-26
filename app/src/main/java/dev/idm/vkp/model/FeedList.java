package dev.idm.vkp.model;

import dev.idm.vkp.api.model.Identificable;

public class FeedList implements Identificable {

    private final int id;

    private final String title;

    public FeedList(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}