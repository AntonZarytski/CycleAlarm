package zaritsky.com.cyclealarm.interfaces;

import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;

public interface AbleToChangeFragment {
    void removeFragment(Fragment removingFragment);
    void addFragment(@IdRes int containerViewId, Fragment addingFragment);
    void replaceFragments(@IdRes int containerViewId, Fragment newFragment);
    void onSelectedFragment(int position);
}
