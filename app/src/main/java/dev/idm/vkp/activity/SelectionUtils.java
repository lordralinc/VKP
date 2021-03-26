package dev.idm.vkp.activity;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import dev.idm.vkp.R;
import dev.idm.vkp.model.FavePage;
import dev.idm.vkp.model.Owner;
import dev.idm.vkp.model.SelectProfileCriteria;
import dev.idm.vkp.model.User;
import dev.idm.vkp.util.Logger;
import dev.idm.vkp.util.Utils;

public class SelectionUtils {

    private static final String TAG = SelectionUtils.class.getSimpleName();
    private static final String VIEW_TAG = "SelectionUtils.SelectionView";

    public static void addSelectionProfileSupport(Context context, ViewGroup root, Object mayBeUser) {
        if (!(context instanceof ProfileSelectable) || root == null) return;

        SelectProfileCriteria criteria = ((ProfileSelectable) context).getAcceptableCriteria();

        boolean canSelect = false;
        if (criteria.getIsPeopleOnly() ? mayBeUser instanceof User : mayBeUser instanceof Owner || mayBeUser instanceof FavePage) {
            canSelect = true;
        }

        if (canSelect && criteria.getOwnerType() == SelectProfileCriteria.OwnerType.ONLY_FRIENDS) {
            assert mayBeUser instanceof User;
            canSelect = ((User) mayBeUser).isFriend();
        }

        ProfileSelectable callack = (ProfileSelectable) context;
        ImageView selectionView = root.findViewWithTag(VIEW_TAG);

        if (!canSelect && selectionView == null) return;

        if (canSelect && selectionView == null) {
            selectionView = new ImageView(context);
            selectionView.setImageResource(R.drawable.plus);
            selectionView.setTag(VIEW_TAG);
            selectionView.setBackgroundResource(R.drawable.circle_back);
            selectionView.getBackground().setAlpha(150);
            selectionView.setLayoutParams(createLayoutParams(root));

            int dp4px = (int) Utils.dpToPx(4, context);
            selectionView.setPadding(dp4px, dp4px, dp4px, dp4px);

            Logger.d(TAG, "Added new selectionView");
            root.addView(selectionView);
        } else {
            Logger.d(TAG, "Re-use selectionView");
        }

        selectionView.setVisibility(canSelect ? View.VISIBLE : View.GONE);

        if (!canSelect) {
            selectionView.setOnClickListener(null);
        } else {
            if (mayBeUser instanceof FavePage && ((FavePage) mayBeUser).getOwner() != null) {
                selectionView.setOnClickListener(v -> callack.select(((FavePage) mayBeUser).getOwner()));
            } else if (mayBeUser instanceof Owner) {
                selectionView.setOnClickListener(v -> callack.select((Owner) mayBeUser));
            }
        }
    }

    private static ViewGroup.LayoutParams createLayoutParams(ViewGroup parent) {
        if (parent instanceof FrameLayout) {
            int margin = (int) Utils.dpToPx(6, parent.getContext());

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            params.bottomMargin = margin;
            params.leftMargin = margin;
            params.rightMargin = margin;
            params.topMargin = margin;
            return params;
        } else {
            throw new IllegalArgumentException("Not yet impl for parent: " + parent.getClass().getSimpleName());
        }
    }
}
