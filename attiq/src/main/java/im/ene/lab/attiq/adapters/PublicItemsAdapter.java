package im.ene.lab.attiq.adapters;

import android.support.annotation.Nullable;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wefika.flowlayout.FlowLayout;

import butterknife.Bind;
import im.ene.lab.attiq.Attiq;
import im.ene.lab.attiq.R;
import im.ene.lab.attiq.data.ApiClient;
import im.ene.lab.attiq.data.vault.PublicItem;
import im.ene.lab.attiq.data.vault.PublicTag;
import im.ene.lab.attiq.util.UIUtil;
import im.ene.lab.attiq.widgets.RoundedTransformation;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import java.util.List;

/**
 * Created by eneim on 12/14/15.
 */
public class PublicItemsAdapter extends BaseListAdapter<PublicItem> {

  private final RealmResults<PublicItem> mItems;

  public PublicItemsAdapter(RealmResults<PublicItem> items) {
    super();
    mItems = items;
    setHasStableIds(true);
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(ViewHolder.LAYOUT_RES, parent, false);
    return new ViewHolder(view);
  }

  @Override public long getItemId(int position) {
    return getItem(position).getId();
  }

  @Override public int getItemCount() {
    return mItems.size();
  }

  @Override public PublicItem getItem(int position) {
    return mItems.get(position);
  }

  private PublicItem getBottomItem() {
    return getItem(getItemCount() - 1);
  }

  @Override
  public void loadItems(final boolean isLoadingMore, int page, int pageLimit, @Nullable String
      query,
                        final Callback<List<PublicItem>> callback) {
    Long id = null;
    if (isLoadingMore) {
      id = getBottomItem().getId();
    }

    ApiClient.stream(id).enqueue(new Callback<List<PublicItem>>() {
      @Override public void onResponse(Response<List<PublicItem>> response, Retrofit retrofit) {
        cleanup(!isLoadingMore);
        if (callback != null) {
          callback.onResponse(response, retrofit);
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

  private void cleanup(boolean shouldCleanup) {
    if (shouldCleanup) {
      Realm realm = Attiq.realm();
      realm.beginTransaction();
      realm.clear(PublicItem.class);
      realm.commitTransaction();
      realm.close();
    }
  }

  public static class ViewHolder extends BaseRecyclerAdapter.ViewHolder<PublicItem> {

    static final int LAYOUT_RES = R.layout.post_item_view;

    private final LayoutInflater mInflater;

    private final int mIconCornerRadius;
    private final int mIconBorderWidth;
    private final int mIconBorderColor;

    @Bind(R.id.item_user_icon) ImageView mItemUserImage;
    @Bind(R.id.item_title) TextView mItemTitle;
    @Bind(R.id.item_tags) FlowLayout mItemTags;
    @Bind(R.id.item_info) TextView mItemInfo;
    @Bind(R.id.item_posted_info) TextView mItemUserInfo;

    public ViewHolder(View view) {
      super(view);
      mInflater = LayoutInflater.from(mContext);
      mIconCornerRadius = mContext.getResources()
          .getDimensionPixelSize(R.dimen.item_icon_size_half);
      mIconBorderWidth = mContext.getResources().getDimensionPixelSize(R.dimen.dimen_unit);
      mIconBorderColor = UIUtil.getColor(mContext, R.color.colorPrimary);
      mItemUserInfo.setClickable(true);
      mItemUserInfo.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override public void bind(PublicItem item) {
      mItemInfo.setText(mContext.getString(R.string.item_info,
          item.getStockCount(), item.getCommentCount()));

      if (item.getUser() != null) {
        String userName = item.getUser().getUrlName();
        if (item.getCreatedAt().equals(item.getUpdatedAt())) {
          mItemUserInfo.setText(Html.fromHtml(mContext.getString(R.string.item_user_info,
              userName, userName, item.getCreatedAtInWords())));
        } else {
          mItemUserInfo.setText(Html.fromHtml(mContext.getString(R.string.item_user_info_edited,
              userName, userName, item.getCreatedAtInWords(), userName, item.getUuid())));
        }

        mItemUserInfo.setVisibility(View.VISIBLE);
      } else {
        mItemUserInfo.setVisibility(View.GONE);
      }

      mItemTitle.setText(Html.fromHtml(item.getTitle()));
      if (!UIUtil.isEmpty(item.getUser().getProfileImageUrl())) {
        mItemUserImage.setVisibility(View.VISIBLE);
        Attiq.picasso().load(item.getUser().getProfileImageUrl())
            .fit().centerInside()
            .transform(new RoundedTransformation(
                mIconBorderWidth, mIconBorderColor, mIconCornerRadius))
            .into(mItemUserImage);
      } else {
        mItemUserImage.setVisibility(View.GONE);
      }

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
