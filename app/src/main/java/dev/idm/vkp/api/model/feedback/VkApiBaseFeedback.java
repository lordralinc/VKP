package dev.idm.vkp.api.model.feedback;

import dev.idm.vkp.api.model.VKApiComment;

public abstract class VkApiBaseFeedback {

    public String type;
    public long date;

    public VKApiComment reply;


}
