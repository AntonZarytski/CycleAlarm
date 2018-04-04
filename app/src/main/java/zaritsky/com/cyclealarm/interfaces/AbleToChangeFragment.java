package zaritsky.com.cyclealarm.interfaces;

import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
/**this interface contains methods for fragmentManager for navigating between fragments*/
public interface AbleToChangeFragment {
    void removeFragment(Fragment removingFragment);
    void addFragment(@IdRes int containerViewId, Fragment addingFragment);
    void changeFragments(@IdRes int containerViewId, Fragment newFragment);
    void onSelectedFragment(Fragment fragment, int position);
}
