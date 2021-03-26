package dev.idm.vkp.api.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import dev.idm.vkp.api.model.VKApiAttachment;
import dev.idm.vkp.api.model.VkApiAttachments;
import dev.idm.vkp.util.Objects;

public class AttachmentsEntryDtoAdapter extends AbsAdapter implements JsonDeserializer<VkApiAttachments.Entry> {

    @Override
    public VkApiAttachments.Entry deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject o = json.getAsJsonObject();

        String type = optString(o, "type");
        VKApiAttachment attachment = AttachmentsDtoAdapter.parse(type, o, context);

        VkApiAttachments.Entry entry = null;
        if (Objects.nonNull(attachment)) {
            entry = new VkApiAttachments.Entry(type, attachment);
        }

        return entry;
    }
}
