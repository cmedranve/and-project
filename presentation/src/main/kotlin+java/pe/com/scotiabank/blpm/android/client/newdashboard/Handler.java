package pe.com.scotiabank.blpm.android.client.newdashboard;

import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.List;

import pe.com.scotiabank.blpm.android.client.R;
import pe.com.scotiabank.blpm.android.client.scotiapay.ScotiaPayFragment;
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.NewMyListFragment;
import pe.com.scotiabank.blpm.android.client.newdashboard.plin.navhost.NavHostPlinFragment;
import pe.com.scotiabank.blpm.android.client.products.dashboard.DashboardViewPager;
import pe.com.scotiabank.blpm.android.client.products.dashboard.HomeFragment;
import pe.com.scotiabank.blpm.android.client.products.notice.NoticeFragment;
import pe.com.scotiabank.blpm.android.client.profilesettings.myaccount.MyAccountFragment;

public class Handler {
    public interface HandlerListener {
        void onNavigationItemSelected(int idLabel);
    }

    private final DashboardViewPager dashboardViewPager;
    private final List<Fragment> fragmentsDashboard;
    private final HandlerListener handlerListener;

    public Handler(DashboardViewPager dashboardViewPager, List<Fragment> fragmentsDashboard, HandlerListener handlerListener) {
        this.dashboardViewPager = dashboardViewPager;
        this.fragmentsDashboard = fragmentsDashboard;
        this.handlerListener = handlerListener;
    }

    public boolean onNavigationClick(@NonNull MenuItem item) {

        if (R.id.navigation_home == item.getItemId()) {
            dashboardViewPager.setCurrentItem(getFragmentPosition(HomeFragment.class), false);
            handlerListener.onNavigationItemSelected(R.id.navigation_home);
            return true;
        }

        if (R.id.navigation_my_list == item.getItemId()) {
            dashboardViewPager.setCurrentItem(getFragmentPosition(NewMyListFragment.class), false);
            handlerListener.onNavigationItemSelected(R.id.navigation_my_list);
            return true;
        }

        if (R.id.navigation_contacts == item.getItemId()) {
            dashboardViewPager.setCurrentItem(getFragmentPosition(NavHostPlinFragment.class), false);
            handlerListener.onNavigationItemSelected(R.id.navigation_contacts);
            return true;
        }

        if (R.id.navigation_notifications == item.getItemId()) {
            setupNewFragment();
            return true;
        }

        if (R.id.navigation_contact_pay == item.getItemId()) {
            dashboardViewPager.setCurrentItem(getFragmentPosition(ScotiaPayFragment.class), false);
            handlerListener.onNavigationItemSelected(R.id.navigation_contact_pay);
            return true;
        }

        dashboardViewPager.setCurrentItem(getFragmentPosition(MyAccountFragment.class), false);
        handlerListener.onNavigationItemSelected(R.id.navigation_profile);
        return true;
    }

    private void setupNewFragment() {
        int fragmentPosition = getFragmentPosition(NoticeFragment.class);
        dashboardViewPager.setCurrentItem(fragmentPosition, false);
        handlerListener.onNavigationItemSelected(R.id.navigation_notifications);
    }

    private int getFragmentPosition(Class<?> classValue) {
        for (int i = 0; fragmentsDashboard != null && i < fragmentsDashboard.size(); i++) {
            if (fragmentsDashboard.get(i).getClass() == classValue) {
                return i;
            }
        }
        return 0;
    }
}