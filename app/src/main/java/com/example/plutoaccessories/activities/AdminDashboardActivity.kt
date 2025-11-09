package com.example.plutoaccessories.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.plutoaccessories.ManageProductsActivity
import com.example.plutoaccessories.R
import com.example.plutoaccessories.utils.SessionManager

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var tvAdminNama: TextView
    private lateinit var tvAdminEmail: TextView
    private lateinit var tvAdminRole: TextView
    private lateinit var btnManageUsers: Button
    private lateinit var btnManageProducts: Button
    private lateinit var btnLogout: Button
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        sessionManager = SessionManager(this)

        if (!sessionManager.isLoggedIn) {
            val i = Intent(this, LoginActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
            finish()
            return
        }

        tvAdminNama = findViewById(R.id.tvAdminNama)
        tvAdminEmail = findViewById(R.id.tvAdminEmail)
        tvAdminRole = findViewById(R.id.tvAdminRole)
        btnManageUsers = findViewById(R.id.btnManageUsers)
        btnManageProducts = findViewById(R.id.btnManageProducts)
        btnLogout = findViewById(R.id.btnLogout)

        val user = sessionManager.userDetails
        val nama = user[SessionManager.KEY_NAMA] ?: "Admin"
        val email = user[SessionManager.KEY_EMAIL] ?: "admin@email.com"
        val role = sessionManager.role

        tvAdminNama.text = nama
        tvAdminEmail.text = email
        tvAdminRole.text = role

        btnManageUsers.setOnClickListener {
            startActivity(Intent(this@AdminDashboardActivity, ManageUsersActivity::class.java))
        }

        btnManageProducts.setOnClickListener {
            startActivity(Intent(this@AdminDashboardActivity, ManageProductsActivity::class.java))
        }

        btnLogout.setOnClickListener {
            sessionManager.logout()
            val intent = Intent(this@AdminDashboardActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}