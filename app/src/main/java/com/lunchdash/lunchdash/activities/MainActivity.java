package com.lunchdash.lunchdash.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.lunchdash.lunchdash.R;
import com.lunchdash.lunchdash.adapters.NavListAdapter;
import com.lunchdash.lunchdash.fragments.ProfileFragment;
import com.lunchdash.lunchdash.fragments.RestaurantSearchFragment;
import com.lunchdash.lunchdash.fragments.SettingsFragment;
import com.lunchdash.lunchdash.models.NavItem;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {
    DrawerLayout drawerLayout;
    ListView lvDrawer;
    List<NavItem> navItems;

    RestaurantSearchFragment rSearchFragment;
    ProfileFragment profileFragment;
    SettingsFragment settingsFragment;
    FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rSearchFragment = new RestaurantSearchFragment();
        profileFragment = new ProfileFragment();
        settingsFragment = new SettingsFragment();


        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        lvDrawer = (ListView) findViewById(R.id.lvDrawer);
        navItems = new ArrayList<>();
        navItems.add(new NavItem("Search", R.drawable.search_icon));
        navItems.add(new NavItem("My Profile", R.drawable.profile_icon));
        navItems.add(new NavItem("Settings", R.drawable.settings_icon));
        navItems.add(new NavItem("Log Out", R.drawable.logout_icon));

        NavListAdapter adapterNavList = new NavListAdapter(this, navItems);
        lvDrawer.setAdapter(adapterNavList);

        fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.flContent, rSearchFragment, "RESTAURANT_SEARCH");
        ft.commit();

        setupNavDrawerListener(lvDrawer);


    }

    private void setupNavDrawerListener(final ListView lvDrawer) { //This sets up swapping fragments when a navbar item is clicked.
        lvDrawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String title = navItems.get(position).getTitle();

                FragmentTransaction ft = fm.beginTransaction();

                switch (title) {
                    case "Search":
                        if (rSearchFragment.isVisible()) { //Do nothing if we're already on that fragment.
                            return;
                        }
                        showSearchFragment(ft);
                        break;
                    case "My Profile":
                        if (profileFragment.isVisible()) {
                            return;
                        }
                        showProfileFragment(ft);
                        break;
                    case "Settings":
                        if (settingsFragment.isVisible()) {
                            return;
                        }
                        showSettingsFragment(ft);
                        break;
                    case "Log Out":
                        ParseUser.logOut();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                        break;
                }
                ft.addToBackStack(null);
                ft.commit();
                drawerLayout.closeDrawer(lvDrawer); //Close the drawer after you swap fragments.
            }
        });
    }

    public void showSearchFragment(FragmentTransaction ft) {
        if (rSearchFragment.isAdded()) {
            ft.show(rSearchFragment);
        } else {
            ft.add(R.id.flContent, rSearchFragment, "RESTAURANT_SEARCH");
        }
        if (profileFragment.isAdded()) ft.hide(profileFragment);
        if (settingsFragment.isAdded()) ft.hide(settingsFragment);
    }

    public void showProfileFragment(FragmentTransaction ft) {
        if (profileFragment.isAdded()) {
            ft.show(profileFragment);
        } else {
            ft.add(R.id.flContent, profileFragment, "{PROFILE");
        }
        if (rSearchFragment.isAdded()) ft.hide(rSearchFragment);
        if (settingsFragment.isAdded()) ft.hide(settingsFragment);
    }

    public void showSettingsFragment(FragmentTransaction ft) {
        if (settingsFragment.isAdded()) {
            ft.show(settingsFragment);
        } else {
            ft.add(R.id.flContent, settingsFragment, "SETTINGS");
        }
        if (rSearchFragment.isAdded()) ft.hide(rSearchFragment);
        if (profileFragment.isAdded()) ft.hide(profileFragment);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) { //Open the nav drawer if the menu button is pressed.

        if (keyCode == KeyEvent.KEYCODE_MENU) {
            toggleDrawer();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void toggleDrawer() {
        View drawerView = findViewById(R.id.lvDrawer); // child drawer view

        if (!drawerLayout.isDrawerOpen(drawerView)) {
            drawerLayout.openDrawer(drawerView);
        } else if (drawerLayout.isDrawerOpen(drawerView)) {
            drawerLayout.closeDrawer(drawerView);
        }
    }


    @Override
    public void onBackPressed() {
        View drawerView = findViewById(R.id.lvDrawer);
        if (drawerLayout.isDrawerOpen(drawerView)) {
            drawerLayout.closeDrawer(drawerView);
            return;
        }
        if (fm.getBackStackEntryCount() == 0) {
            finish();
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
        } else {
            fm.popBackStack();
        }
    }

}
