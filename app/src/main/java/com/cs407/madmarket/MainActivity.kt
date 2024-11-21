package com.cs407.madmarket

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.cs407.madmarket.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    //get permissions for notifications
    private lateinit var binding: ActivityMainBinding
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {isGranted: Boolean ->
        if (!isGranted){
            Toast.makeText(this, "Please allow all notifications to continue.",
                Toast.LENGTH_LONG).show()
        }
    }

    @VisibleForTesting
    public fun requestPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {


            if (ContextCompat.checkSelfPermission(
                    applicationContext, android.Manifest.permission.POST_NOTIFICATIONS
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set initial fragment
        replaceFragment(HomeFragment())

        // Handle Bottom Navigation selections
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    if (getCurrentFragment() !is HomeFragment) replaceFragment(HomeFragment())
                }
                R.id.search -> {
                    if (getCurrentFragment() !is SearchFragment) replaceFragment(SearchFragment())
                }
                R.id.cart -> {
                    if (getCurrentFragment() !is CartFragment) replaceFragment(CartFragment())
                }
                R.id.profile -> {
                    if (getCurrentFragment() !is ProfileFragment) replaceFragment(ProfileFragment())
                }
                R.id.messages -> {
                    if (getCurrentFragment() !is MessagesFragment) replaceFragment(MessagesFragment())
                }
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .setReorderingAllowed(true)
            .commit()
    }

    private fun getCurrentFragment(): Fragment? {
        return supportFragmentManager.findFragmentById(R.id.frame_layout)
    }
}
