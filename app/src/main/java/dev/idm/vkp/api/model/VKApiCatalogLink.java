package dev.idm.vkp.api.model;

public class VKApiCatalogLink implements VKApiAttachment {

    public String url;

    public String title;

    public String subtitle;

    public String preview_photo;

    public VKApiCatalogLink() {

    }

    @Override
    public String getType() {
        return VKApiAttachment.TYPE_LINK;
    }
}
