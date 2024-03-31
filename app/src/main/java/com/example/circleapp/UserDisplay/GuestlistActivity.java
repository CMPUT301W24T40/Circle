package com.example.circleapp.UserDisplay;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.example.circleapp.BaseObjects.Event;
import com.example.circleapp.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * This class is used to display the registered users for a given event.
 */
public class GuestlistActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guestlist);

        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);

        Event event = getIntent().getParcelableExtra("event");
        setupViewPager(event);
    }

    /**
     * Sets up the ViewPager to display tabbed setup for fragments for registered and checked-in users.
     *
     * @param event The Event object for which the guest list is displayed.
     */
    private void setupViewPager(Event event) {
        FragmentStateAdapter adapter = new FragmentStateAdapter(getSupportFragmentManager(), getLifecycle()) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("event", event);

                switch (position) {
                    case 0:
                        RegisteredUsersFragment registeredUsersFragment = new RegisteredUsersFragment();
                        registeredUsersFragment.setArguments(bundle);
                        return registeredUsersFragment;
                    case 1:
                        CheckedInUsersFragment checkedInUsersFragment = new CheckedInUsersFragment();
                        checkedInUsersFragment.setArguments(bundle);
                        return checkedInUsersFragment;

                    default:
                        return null;
                }
            }

            @Override
            public int getItemCount() {
                return 2;
            }
        };
        viewPager.setAdapter(adapter);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Registered");
                            break;
                        case 1:
                            tab.setText("Checked In");
                            break;
                    }
                }).attach();
    }
}