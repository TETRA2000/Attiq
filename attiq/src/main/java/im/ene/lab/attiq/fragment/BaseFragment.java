package im.ene.lab.attiq.fragment;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.v4.app.Fragment;
import android.view.View;

import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import im.ene.lab.attiq.util.event.Event;

/**
 * Created by eneim on 12/13/15.
 */
public class BaseFragment extends Fragment {

  @CallSuper
  @Override public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    ButterKnife.bind(this, view);
    EventBus.getDefault().register(this);
  }

  @CallSuper
  @Override public void onDestroyView() {
    EventBus.getDefault().unregister(this);
    ButterKnife.unbind(this);
    super.onDestroyView();
  }

  @SuppressWarnings("unused")
  public void onEvent(Event event) {
  }
}
