package com.sangcx;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.google.android.material.navigation.NavigationView;
import com.sangcx.codeExecution.CodeExecutionFragment;

public class MainActivity extends AppCompatActivity {
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;
    DrawerLayout drawerLayout;
    Toolbar toolbar;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigationView = findViewById(R.id.navview);
        drawerLayout = findViewById(R.id.drawer);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this , drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, new HomeFragment())
                .commit();

        navigationView.setNavigationItemSelectedListener(item -> {

            switch (item.getItemId())
            {
                case R.id.home:
                    drawerLayout.closeDrawer(GravityCompat.START);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_layout, new HomeFragment())
                            .commit();
                    break;

                case R.id.codeExecution:
                    drawerLayout.closeDrawer(GravityCompat.START);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_layout, new CodeExecutionFragment())
                            .commit();
                    break;

            }

            return true;
        });
    }
}