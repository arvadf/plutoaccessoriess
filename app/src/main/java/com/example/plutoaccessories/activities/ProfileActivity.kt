package com.example.plutoaccessories.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.plutoaccessories.R
import com.example.plutoaccessories.databinding.ActivityProfileBinding
import com.example.plutoaccessories.utils.SessionManager

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        if (!sessionManager.isLoggedIn) {
            redirectToLogin()
            return
        }

        setupUserData()
        setupClickListeners()
        setupBottomNavigation()
    }

    override fun onResume() {
        super.onResume()
        setupUserData()
        // Ensure correct item is selected when returning to this activity
        binding.bottomNavigation.selectedItemId = R.id.nav_profile
    }

    private fun setupUserData() {
        val userDetails = sessionManager.userDetails
        binding.tvNama.text = userDetails[SessionManager.KEY_NAMA] ?: "Tidak Diketahui"
        binding.tvEmail.text = userDetails[SessionManager.KEY_EMAIL] ?: "-"

        val profileImageUriString = userDetails[SessionManager.KEY_PROFIL_IMAGE]
        if (profileImageUriString != null) {
            try {
                binding.ivProfile.setImageURI(Uri.parse(profileImageUriString))
            } catch (e: Exception) {
                binding.ivProfile.setImageResource(R.drawable.ic_person)
            }
        } else {
            binding.ivProfile.setImageResource(R.drawable.ic_person)
        }
    }

    private fun setupClickListeners() {
        binding.tvLogout.setOnClickListener {
            sessionManager.logout()
            redirectToLogin()
        }

        binding.rlOrderHistory.setOnClickListener {
            startActivity(Intent(this, OrderHistoryActivity::class.java))
        }

        binding.rlWallet.setOnClickListener {
            startActivity(Intent(this, WalletActivity::class.java))
        }

        binding.rlEditProfile.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }
    }

    private fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            if (item.itemId == binding.bottomNavigation.selectedItemId) {
                return@setOnNavigationItemSelectedListener false // Do nothing if already on this screen
            }
            val intent = when (item.itemId) {
                R.id.nav_home -> Intent(this, MainActivity::class.java)
                R.id.nav_wishlist -> Intent(this, WishlistActivity::class.java)
                R.id.nav_cart -> Intent(this, CartActivity::class.java)
                R.id.nav_transactions -> Intent(this, TransactionsActivity::class.java)
                else -> null
            }
            intent?.let {
                it.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                startActivity(it)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out) // Optional: for smooth transition
            }
            return@setOnNavigationItemSelectedListener true
        }
    }
}
