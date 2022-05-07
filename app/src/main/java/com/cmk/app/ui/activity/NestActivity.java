package com.cmk.app.ui.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.cmk.app.R;
import com.cmk.app.ui.fragment.NestFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class NestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nest);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager viewPager = findViewById(R.id.viewPager);

        List<Fragment> fragments = new ArrayList<>();
        List<String> tabs = new ArrayList<>();
        NestFragment nestFragment1 = new NestFragment();
        NestFragment nestFragment2 = new NestFragment();
        NestFragment nestFragment3 = new NestFragment();
        NestFragment nestFragment4 = new NestFragment();
        fragments.add(nestFragment1);
        fragments.add(nestFragment2);
        fragments.add(nestFragment3);
        fragments.add(nestFragment4);
        tabs.add("11111");
        tabs.add("22222");
        tabs.add("33333");
        tabs.add("44444");
        tabLayout.addTab(tabLayout.newTab().setText(tabs.get(0)));
        tabLayout.addTab(tabLayout.newTab().setText(tabs.get(1)));
        tabLayout.addTab(tabLayout.newTab().setText(tabs.get(2)));
        tabLayout.addTab(tabLayout.newTab().setText(tabs.get(3)));

        MyPager pagerAdapter = new MyPager(getSupportFragmentManager(), fragments, tabs);
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    static class MyPager extends FragmentPagerAdapter{
        private List<Fragment> fragmentList = new ArrayList<>();
        private List<String> titleList = new ArrayList<>();

        public MyPager(FragmentManager fm, List<Fragment> fragmentList, List<String> titleList) {
            super(fm);
            this.fragmentList = fragmentList;
            this.titleList = titleList;
        }

        public MyPager(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titleList.get(position);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }
    }
}
