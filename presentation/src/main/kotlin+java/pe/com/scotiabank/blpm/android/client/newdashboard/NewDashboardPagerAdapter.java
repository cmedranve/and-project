package pe.com.scotiabank.blpm.android.client.newdashboard;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class NewDashboardPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> fragments;

    public NewDashboardPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return getFragments().get(position);
    }

    @Override
    public int getCount() {
        return getFragments().size();
    }

    public List<Fragment> getFragments() {
        return fragments;
    }
}