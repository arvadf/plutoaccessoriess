package com.example.plutoaccessories.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.plutoaccessories.R
import com.example.plutoaccessories.utils.SessionManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProfileActivity : AppCompatActivity() {
    var tvNama: TextView? = null
    var tvEmail: TextView? = null
    var tvAlamat: TextView? = null
    var tvNoHp: TextView? = null
    var btnLogout: Button? = null
    var btnOrderHistory: Button? = null
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        tvNama = findViewById<TextView?>(R.id.tvNama)
        tvEmail = findViewById<TextView?>(R.id.tvEmail)
        tvAlamat = findViewById<TextView?>(R.id.tvAlamat)
        tvNoHp = findViewById<TextView?>(R.id.tvNoHp)
        btnLogout = findViewById<Button?>(R.id.btnLogout)
        btnOrderHistory = findViewById<Button?>(R.id.btnOrderHistory)

        sessionManager = SessionManager(this)
        if (!sessionManager.isLoggedIn) {
            val i = Intent(this@ProfileActivity, LoginActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
            finish()
            return
        }

        val prefs = getSharedPreferences("pluto_session", MODE_PRIVATE)
        val nama: String = prefs.getString(SessionManager.KEY_NAMA, "Tidak diketahui")!!
        val email: String = prefs.getString(SessionManager.KEY_EMAIL, "-")!!
        val alamat: String = prefs.getString(SessionManager.KEY_ALAMAT, "-")!!
        val noHp: String = prefs.getString(SessionManager.KEY_NO_HP, "-")!!

        // Tampilkan ke TextView
        tvNama!!.setText(nama)
        tvEmail!!.setText(email)
        tvAlamat!!.setText(alamat)
        tvNoHp!!.setText(noHp)

        // Tombol Logout
        btnLogout!!.setOnClickListener(View.OnClickListener { v: View? ->
            sessionManager.logout()
            val intent = Intent(this@ProfileActivity, LoginActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        })

        btnOrderHistory!!.setOnClickListener {
            startActivity(Intent(this@ProfileActivity, OrderHistoryActivity::class.java))
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_profile
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.nav_wishlist -> {
                    startActivity(Intent(this, WishlistActivity::class.java))
                    true
                }
                R.id.nav_cart -> {
                    startActivity(Intent(this, CartActivity::class.java))
                    true
                }
                R.id.nav_transactions -> {
                    startActivity(Intent(this, TransactionsActivity::class.java))
                    true
                }
                R.id.nav_profile -> {
                    // Already on profile screen
                    true
                }
                else -> false
            }
        }
    }
}
