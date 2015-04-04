package com.lunchdash.lunchdash.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
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
    NavListAdapter adapterNavList;

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

        NavItem searchNavItem = new NavItem("Search", R.drawable.search_icon);
        searchNavItem.setSelected(true);

        navItems.add(searchNavItem);
        navItems.add(new NavItem("Profile", R.drawable.profile_icon));
        navItems.add(new NavItem("Settings", R.drawable.settings_icon));
        navItems.add(new NavItem("Log Out", R.drawable.logout_icon));

        adapterNavList = new NavListAdapter(this, navItems);
        lvDrawer.setAdapter(adapterNavList);

        fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.flContent, rSearchFragment, "RESTAURANT_SEARCH");
        ft.addToBackStack("Search");
        ft.commit();

        setupDrawerListeners(lvDrawer);
    }

    private void setupDrawerListeners(final ListView lvDrawer) { //This sets up swapping fragments when a navbar item is clicked.
        lvDrawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String title = navItems.get(position).getTitle();

                switch (title) {
                    case "Search":
                        if (rSearchFragment.isVisible()) { //Do nothing if we're already on that fragment.
                            return;
                        }
                        showSearchFragment(true);
                        break;
                    case "Profile":
                        if (profileFragment.isVisible()) {
                            return;
                        }
                        showProfileFragment(true);

                        break;
                    case "Settings":
                        if (settingsFragment.isVisible()) {
                            return;
                        }
                        showSettingsFragment(true);
                        break;
                    case "Log Out":
                        ParseUser.logOut();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                        break;
                }

                //Reset the colors for all the items, then highlight the selected item.
                for (int i = 0; i < navItems.size(); i++) {
                    navItems.get(i).setSelected(false);
                }
                navItems.get(position).setSelected(true);
                adapterNavList.notifyDataSetChanged();

                drawerLayout.closeDrawer(lvDrawer); //Close the drawer after you swap fragments.
                for (int i = 0; i < fm.getBackStackEntryCount(); i++) {
                    Log.d("backstackdebug", fm.getBackStackEntryAt(i).getName());
                }
                Log.d("backstackdebug", "-------------------");
            }
        });
    }

    //We'll call this when we hit the back button.
    public void selectNavItem(String title) {

        //Unselect all the nav items.
        for (int i = 0; i < navItems.size(); i++) {
            navItems.get(i).setSelected(false);
        }
        getNavItemWithTitle(title).setSelected(true);
        adapterNavList.notifyDataSetChanged();
    }

    public NavItem getNavItemWithTitle(String title) {
        for (NavItem item : navItems) {
            if (item.getTitle().equals(title)) {
                return item;
            }
        }
        return null;
    }

    public void showSearchFragment(boolean addToBackstack) {
        FragmentTransaction ft = fm.beginTransaction();
        if (rSearchFragment.isAdded()) {
            ft.show(rSearchFragment);
        } else {
            ft.add(R.id.flContent, rSearchFragment, "RESTAURANT_SEARCH");
        }
        if (profileFragment.isAdded()) ft.hide(profileFragment);
        if (settingsFragment.isAdded()) ft.hide(settingsFragment);
        if (addToBackstack) ft.addToBackStack("Search");
        ft.commit();
    }

    public void showProfileFragment(boolean addToBackstack) {
        FragmentTransaction ft = fm.beginTransaction();
        if (profileFragment.isAdded()) {
            ft.show(profileFragment);
        } else {
            ft.add(R.id.flContent, profileFragment, "PROFILE");
        }
        if (rSearchFragment.isAdded()) ft.hide(rSearchFragment);
        if (settingsFragment.isAdded()) ft.hide(settingsFragment);
        if (addToBackstack) ft.addToBackStack("Profile");
        ft.commit();
    }

    public void showSettingsFragment(boolean addToBackstack) {
        FragmentTransaction ft = fm.beginTransaction();
        if (settingsFragment.isAdded()) {
            ft.show(settingsFragment);
        } else {
            ft.add(R.id.flContent, settingsFragment, "SETTINGS");
        }
        if (rSearchFragment.isAdded()) ft.hide(rSearchFragment);
        if (profileFragment.isAdded()) ft.hide(profileFragment);
        if (addToBackstack) ft.addToBackStack("Settings");
        ft.commit();
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
        if (fm.getBackStackEntryCount() < 2) { //There's only 1 entry left so we're going to exit out of the app.
            finish();
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
        } else {
            Log.d("backstackdebug", "BACK PRESSED");
            for (int i = 0; i < fm.getBackStackEntryCount(); i++) {
                Log.d("backstackdebug", fm.getBackStackEntryAt(i).getName());
            }

            fm.popBackStackImmediate();
            String lastFragment = fm.getBackStackEntryAt(fm.getBackStackEntryCount() - 1).getName(); //Get the name of the fragment on top of the stack.
            switch (lastFragment) {
                case "Search":
                    showSearchFragment(false);
                    break;
                case "Settings":
                    showSettingsFragment(false);
                    break;
                case "Profile":
                    showProfileFragment(false);
                    break;
            }
            selectNavItem(lastFragment);
        }
    }

}
