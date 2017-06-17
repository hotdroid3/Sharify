package softwareengineering.assignment.sharify;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SMViewPagerActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smview_pager);
        ViewPager viewPager = (ViewPager)findViewById(R.id.SMviewpager);
        viewPager.setAdapter(new SMViewPagerActivity.SMViewPagerAdapter(getSupportFragmentManager(), SMViewPagerActivity.this));

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    public class SMViewPagerAdapter extends FragmentPagerAdapter {
        final int PAGE_COUNT = 4;
        private String tabTitles[] = new String[]{"Uploaded", "Accepted", "Collected", "Profile"};
        private Context context;

        public SMViewPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position)
            {
                case 0:
                    return AvailableItemsFragment.newInstance();
                case 1:
                    return NGOAcceptedItemsFragment.newInstance();
                case 2:
                    return NGOCollectedItemsFragment.newInstance();
                case 3:
                    return ViewProfileFragment.newInstance();
                default:
                    return null;

            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            return tabTitles[position];
        }
    }
}
