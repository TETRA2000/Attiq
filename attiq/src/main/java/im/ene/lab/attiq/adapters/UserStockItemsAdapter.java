package im.ene.lab.attiq.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.RequestCreator;
import com.wefika.flowlayout.FlowLayout;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.BindDimen;
import im.ene.lab.attiq.Attiq;
import im.ene.lab.attiq.R;
import im.ene.lab.attiq.data.api.ApiClient;
import im.ene.lab.attiq.data.one.PublicTag;
import im.ene.lab.attiq.data.one.PublicUser;
import im.ene.lab.attiq.data.one.UserOwnItem;
import im.ene.lab.attiq.util.TimeUtil;
import im.ene.lab.attiq.util.UIUtil;
import im.ene.lab.attiq.widgets.RoundedTransformation;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eneim on 1/6/16.
 */
public class UserStockItemsAdapter extends ListAdapter<UserOwnItem> {

  private final Object LOCK = new Object();

  private final String mUserId;
  private final ArrayList<UserOwnItem> mItems;

  public UserStockItemsAdapter(String userId) {
    super();
    this.mUserId = userId;
    mItems = new ArrayList<>();
    setHasStableIds(true);
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(ViewHolder.LAYOUT_RES, parent, false);
    final ViewHolder viewHolder = new ViewHolder(view);
    viewHolder.setOnViewHolderClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        int position = viewHolder.getAdapterPosition();
        if (position != RecyclerView.NO_POSITION && mOnItemClickListener != null) {
          mOnItemClickListener.onItemClick(UserStockItemsAdapter.this, viewHolder, view, position,
              getItemId(position));
        }
      }
    });

    return viewHolder;
  }

  @Override public long getItemId(int position) {
    return getItem(position).getId();
  }

  @Override public int getItemCount() {
    if (mItems == null) {
      return 0;
    }
    return mItems.size();
  }

  @Override public UserOwnItem getItem(int position) {
    return mItems.get(position);
  }

  private UserOwnItem getBottomItem() {
    return getItem(getItemCount() - 1);
  }

  @Override
  public void loadItems(final boolean isLoadingMore, int page, int pageLimit,
                        @Nullable String query, final Callback<List<UserOwnItem>> callback) {
    ApiClient.userStockedItems(mUserId, page).enqueue(new Callback<List<UserOwnItem>>() {
      @Override public void onResponse(Response<List<UserOwnItem>> response) {
        cleanup(!isLoadingMore);
        if (callback != null) {
          callback.onResponse(response);
        }
      }

      @Override public void onFailure(Throwable throwable) {
        cleanup(!isLoadingMore);
        if (callback != null) {
          callback.onFailure(throwable);
        }
      }
    });
  }

  @Override public void addItem(UserOwnItem item) {
    synchronized (LOCK) {
      mItems.add(item);
      notifyItemInserted(getItemCount() - 1);
    }
  }

  @Override public void addItems(List<UserOwnItem> items) {
    synchronized (LOCK) {
      int oldLen = getItemCount();
      mItems.addAll(items);
      notifyItemRangeInserted(oldLen, items.size());
    }
  }

  private void cleanup(boolean shouldCleanup) {
    if (shouldCleanup) {
      synchronized (LOCK) {
        mItems.clear();
        notifyDataSetChanged();
      }
    }
  }

  public static abstract class OnUserItemClickListener
      implements OnItemClickListener {

    public abstract void onUserClick(PublicUser user);

    public abstract void onItemContentClick(UserOwnItem item);

    @Override
    public void onItemClick(BaseAdapter adapter,
                            BaseAdapter.ViewHolder viewHolder,
                            View view, int adapterPos, long itemId) {
      final UserOwnItem item;
      if (adapter instanceof BaseListAdapter) {
        item = (UserOwnItem) ((BaseListAdapter) adapter).getItem(adapterPos);
      } else {
        item = null;
      }

      if (item != null && viewHolder instanceof ViewHolder) {
        if (view == ((ViewHolder) viewHolder).mItemUserImage) {
          onUserClick(item.getUser());
        } else if (view == ((ViewHolder) viewHolder).itemView) {
          onItemContentClick(item);
        }
      }
    }
  }

  public static class ViewHolder extends BaseListAdapter.ViewHolder<UserOwnItem> {

    static final int LAYOUT_RES = R.layout.post_item_view;

    private final LayoutInflater mInflater;

    // Views
    @Bind(R.id.item_user_icon) ImageView mItemUserImage;
    @Bind(R.id.item_title) TextView mItemTitle;
    @Bind(R.id.item_tags) FlowLayout mItemTags;
    @Bind(R.id.item_info) TextView mItemInfo;
    @Bind(R.id.item_posted_info) TextView mItemUserInfo;

    // Others
    @BindDimen(R.dimen.item_icon_size_half) int mIconCornerRadius;
    @BindDimen(R.dimen.dimen_unit) int mIconBorderWidth;
    @BindColor(R.color.colorAccent) int mIconBorderColor;

    private final Context mContext;

    public ViewHolder(View view) {
      super(view);
      mContext = itemView.getContext();
      mInflater = LayoutInflater.from(mContext);
      mItemUserInfo.setClickable(true);
      mItemUserInfo.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override public void setOnViewHolderClickListener(View.OnClickListener listener) {
      mItemUserImage.setOnClickListener(listener);
      itemView.setOnClickListener(listener);
    }

    @Override public void bind(UserOwnItem item) {
      String itemInfo = item.getCommentCount() == 1 ?
          mContext.getString(R.string.item_info_one, item.getStockCount()) :
          mContext.getString(R.string.item_info_many, item.getStockCount(), item.getCommentCount());
      mItemInfo.setText(itemInfo);

      if (item.getUser() != null) {
        String userName = item.getUser().getUrlName();
        if (item.getCreatedAt().equals(item.getUpdatedAt())) {
          mItemUserInfo.setText(Html.fromHtml(mContext.getString(R.string.item_user_info,
              userName, userName,
              TimeUtil.beautify(item.getCreatedAtAsSeconds())
          )));
        } else {
          mItemUserInfo.setText(Html.fromHtml(mContext.getString(R.string.item_user_info_edited,
              userName, userName,
              TimeUtil.beautify(item.getCreatedAtAsSeconds()),
              userName, item.getUuid())));
        }
        mItemUserInfo.setVisibility(View.VISIBLE);
      } else {
        mItemUserInfo.setVisibility(View.GONE);
      }

      mItemTitle.setText(Html.fromHtml(item.getTitle()));
      final RequestCreator requestCreator;
      if (!UIUtil.isEmpty(item.getUser().getProfileImageUrl())) {
        requestCreator = Attiq.picasso().load(item.getUser().getProfileImageUrl());
      } else {
        requestCreator = Attiq.picasso().load(R.drawable.blank_profile_icon_medium);
      }

      requestCreator
          .placeholder(R.drawable.blank_profile_icon_medium)
          .error(R.drawable.blank_profile_icon_medium)
          .fit().centerInside()
          .transform(new RoundedTransformation(
              mIconBorderWidth, mIconBorderColor, mIconCornerRadius))
          .into(mItemUserImage);

      mItemTags.removeAllViews();
      if (!UIUtil.isEmpty(item.getTags())) {
        for (PublicTag tag : item.getTags()) {
          TextView tagView = (TextView) mInflater.inflate(
              R.layout.widget_tag_textview, mItemTags, false);
          tagView.setText(tag.getName());
          mItemTags.addView(tagView);
        }
      }
    }
  }
}
