package dev.idm.vkp.adapter.base;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import dev.idm.vkp.adapter.listener.OwnerClickListener;

import static dev.idm.vkp.util.Objects.nonNull;

public abstract class AbsRecyclerViewAdapter<H extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<H> {

    private OwnerClickListener ownerClickListener;

    public void setOwnerClickListener(OwnerClickListener ownerClickListener) {
        this.ownerClickListener = ownerClickListener;
    }

    protected void addOwnerAvatarClickHandling(View view, int ownerId) {
        view.setOnClickListener(v -> {
            if (nonNull(ownerClickListener)) {
                ownerClickListener.onOwnerClick(ownerId);
            }
        });
    }
}