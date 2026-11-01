package com.example.plutoaccessories.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.plutoaccessories.databinding.ActivityAdminDashboardBinding
import com.example.plutoaccessories.utils.SessionManager
import com.example.plutoaccessories.activities.ManageProductsActivity

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminDashboardBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        if (!sessionManager.isLoggedIn) {
            redirectToLogin()
            return
        }

        setupAdminInfo()
        setupClickListeners()
    }

    private fun setupAdminInfo() {
        val userDetails = sessionManager.userDetails
        binding.tvAdminNama.text = userDetails[SessionManager.KEY_NAMA] ?: "Admin"
        binding.tvAdminEmail.text = userDetails[SessionManager.KEY_EMAIL] ?: "-"
        binding.tvAdminRole.text = sessionManager.role.replaceFirstChar { it.uppercase() }
    }

    private fun setupClickListeners() {
        binding.btnManageProducts.setOnClickListener {
            startActivity(Intent(this, ManageProductsActivity::class.java))
        }

        binding.btnManageUsers.setOnClickListener {
            startActivity(Intent(this, ManageUsersActivity::class.java))
        }

        binding.btnManageOrders.setOnClickListener {
            startActivity(Intent(this, ManageOrdersActivity::class.java))
        }

        binding.btnManageTransactions.setOnClickListener {
            startActivity(Intent(this, ManageTransactionsActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            sessionManager.logout()
            redirectToLogin()
        }
    }

    private fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
