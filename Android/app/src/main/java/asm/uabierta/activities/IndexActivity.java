package asm.uabierta.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.Objects;

import asm.uabierta.R;
import asm.uabierta.fragments.FoundsFragment;
import asm.uabierta.fragments.LostsFragment;
import asm.uabierta.utils.UserPreferences;

public class IndexActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private UserPreferences uP;
    private DrawerLayout drawer;
    private TextView tvName, tvPhone;
    private FloatingActionButton btnFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        uP = new UserPreferences(getApplicationContext());

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView nv = (NavigationView) findViewById(R.id.logged_nav);
        nv.setNavigationItemSelectedListener(this);
        View headerLayout = nv.getHeaderView(0);
        tvName  = (TextView) headerLayout.findViewById(R.id.tvName);
        tvPhone = (TextView) headerLayout.findViewById(R.id.tvPhone);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnFab = (FloatingActionButton) findViewById(R.id.btnFab);

        if(!uP.isLogged()){
            btnFab.setVisibility(View.GONE);
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        PagerAdapter pagerAdapter =
                new PagerAdapter(getSupportFragmentManager(), IndexActivity.this);
        viewPager.setAdapter(pagerAdapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        // Iterate over all tabs and set the custom view
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(pagerAdapter.getTabView(i));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        tvName.setText(uP.getName());
        if(!Objects.equals(uP.getPrefix(), "prefix"))
            tvPhone.setText(uP.getPrefix()+" "+uP.getPhone());
        else
            tvPhone.setText(uP.getPhone());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(!uP.isLogged())
            getMenuInflater().inflate(R.menu.menu_index, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.login:
                Intent iLogin = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(iLogin);
                break;
            case R.id.signup:
                Intent iSign = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(iSign);
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.new_ad:
                Intent nAdd = new Intent(getApplicationContext(), NewItemActivity.class);
                startActivity(nAdd);
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.my_ads:
                Intent iAdds = new Intent(getApplicationContext(), MyItemsActivity.class);
                startActivity(iAdds);
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.settings:
                Intent iSettings = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(iSettings);
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.logout:
                UserPreferences.closeSession(getApplicationContext(), IndexActivity.this);
                break;
            default:
                drawer.closeDrawer(GravityCompat.START);
                break;
        }
        return true;
    }

    class PagerAdapter extends FragmentPagerAdapter {

        String tabTitles[] = new String[] { getString(R.string.founds), getString(R.string.losts) };
        Context context;

        public PagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return new FoundsFragment();
                case 1:
                    return new LostsFragment();
            }

            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            return tabTitles[position];
        }

        public View getTabView(int position) {
            View tab = LayoutInflater.from(IndexActivity.this).inflate(R.layout.tab_page, null);
            TextView tv = (TextView) tab.findViewById(R.id.custom_text);
            tv.setText(tabTitles[position]);
            return tab;
        }
    }

    public void newItem(View view) {
        Intent intent = new Intent(getApplicationContext(), NewItemActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            finish();
        }
    }
}