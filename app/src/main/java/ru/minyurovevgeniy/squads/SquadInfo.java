package ru.minyurovevgeniy.squads;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

//import ru.minyurovevgeniy.squads.ui.main.SectionsPagerAdapter;
import ru.minyurovevgeniy.squads.databinding.ActivitySquadInfoBinding;

public class SquadInfo extends AppCompatActivity {

    //private ActivitySquadInfoBinding binding;

    //int id=0;
    String name="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_squad_info);
        //getSupportActionBar().hide();

        Intent intent = getIntent();

        //id=intent.getIntExtra("id",1);
        name=intent.getStringExtra("name");

      //  binding = ActivitySquadInfoBinding.inflate(getLayoutInflater());
        //setContentView(binding.getRoot());

        //SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        //ViewPager viewPager = binding.viewPager;
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        //TabLayout tabs = binding.tabs;
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        TextView squad_name=findViewById(R.id.squad);
        squad_name.setText(name);

        TextView home = findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SquadInfo.this,MainActivity.class);
                startActivity(intent);
            }
        });

        String[] tabsName = { "Основная информация","Объявления","Расписание встреч","Случайная фотография" };

        tabs.removeAllTabs();

        for (String tab_name : tabsName) {
            tabs.addTab(tabs.newTab().setText(tab_name));
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getItemPosition(Object item)
        {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            // return PlaceholderFragment.newInstance(position + 1);

            switch (position)
            {
                case 0:
                    // Новости
                    return new GeneralFragment();

                case 1:
                // Новости
                return new AnnouncementsFragment();

                case 2:
                    return new TimetableFragment();
                case 3:
                    return new RandomPhotoFragment();
            }
            return new GeneralFragment();
        }

        @Override
        public int getCount() {

            return 4;
        }
    }
}