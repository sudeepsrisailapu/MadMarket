package com.cs407.madmarket

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.cs407.madmarket.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

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
