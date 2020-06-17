package com.example.lts;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(Login.role.equals("driver"))
        {
            MenuInflater inflater = getMenuInflater(); inflater.inflate(R.menu.user_menu, menu); return true;}
        else{
            MenuInflater inflater = getMenuInflater(); inflater.inflate(R.menu.admin_menu, menu); return true;}


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.logout:
                Login.userId=null;
                Login.role=null;
                SQLiteDatabase mydatabase = openOrCreateDatabase("driver_db",MODE_PRIVATE,null);
                mydatabase.execSQL("DROP TABLE IF EXISTS login");
                Intent i=new Intent(getApplicationContext(),Login.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                return true;
            case R.id.newuser:
                Intent reg = new Intent(getApplicationContext(), Register.class);
                startActivity(reg);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
