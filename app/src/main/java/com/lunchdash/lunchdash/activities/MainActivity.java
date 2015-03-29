package com.lunchdash.lunchdash.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.lunchdash.lunchdash.R;
import com.lunchdash.lunchdash.adapters.NavListAdapter;
import com.lunchdash.lunchdash.fragments.RestaurantSearchFragment;
import com.lunchdash.lunchdash.models.NavItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {
    DrawerLayout drawerLayout;
    ListView lvDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        lvDrawer = (ListView) findViewById(R.id.lvDrawer);
        List<NavItem> navItems = new ArrayList<>();
        navItems.add(new NavItem("Search", android.R.drawable.ic_menu_search));
        navItems.add(new NavItem("My Profile", R.drawable.abc_textfield_activated_mtrl_alpha));
        navItems.add(new NavItem("Settings", android.R.drawable.ic_menu_report_image));
        navItems.add(new NavItem("Log Out", R.drawable.abc_textfield_activated_mtrl_alpha));

        NavListAdapter adapterNavList = new NavListAdapter(this, navItems);
        lvDrawer.setAdapter(adapterNavList);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.flContent, new RestaurantSearchFragment(), "RESTAURANT_SEARCH");
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
