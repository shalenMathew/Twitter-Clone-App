package com.example.thoughts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.thoughts.Fragments.ConnectFragment;
import com.example.thoughts.Fragments.HomeFragment;
import com.example.thoughts.Fragments.NotificationFragment;
import com.example.thoughts.Fragments.ProfileFragment;
import com.example.thoughts.Fragments.ReelFragment;
import com.example.thoughts.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.mainToolbar);
        MainActivity.this.setTitle("Profile");


        binding.mainNavBottom.setItemSelected(R.id.Home,true);
        binding.mainToolbar.setVisibility(View.GONE);
        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame,new HomeFragment()).commit();

        binding.mainNavBottom.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {

                Fragment fragment= null;

                if(i==R.id.Home){

                    binding.mainToolbar.setVisibility(View.GONE);
                    binding.fragmentHomeFab.setVisibility(View.VISIBLE);

                    fragment= new HomeFragment();

                } else if (i==R.id.Notification) {

                    binding.fragmentHomeFab.setVisibility(View.GONE);
                    binding.mainToolbar.setVisibility(View.GONE);
                    fragment=new NotificationFragment();

                }else if (i==R.id.Reels) {
                    binding.mainToolbar.setVisibility(View.GONE);
                    binding.fragmentHomeFab.setVisibility(View.GONE);

                    fragment=new ReelFragment();

                }else if (i==R.id.Connect) {

                    binding.mainToolbar.setVisibility(View.GONE);
                    binding.fragmentHomeFab.setVisibility(View.GONE);
                    fragment=new ConnectFragment();

                }else if (i==R.id.Profile) {

                    binding.mainToolbar.setVisibility(View.VISIBLE);
                    binding.fragmentHomeFab.setVisibility(View.GONE);
                    fragment=new ProfileFragment();

                }

                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame,fragment).commit();
            }
        });

        binding.fragmentHomeFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent i = new Intent(MainActivity.this, AddTweetActivity.class);
                startActivity(i);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.logout_menu,menu);
       return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int itemId = item.getItemId();

        if(itemId==R.id.log_out){

            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(this,LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);

        }


        return super.onOptionsItemSelected(item);
    }
}