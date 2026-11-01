package com.example.plutoaccessories.activities

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.plutoaccessories.databinding.ActivityChangePasswordBinding
import com.example.plutoaccessories.utils.SessionManager

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        sessionManager = SessionManager(this)

        binding.btnSaveChanges.setOnClickListener {
            handleChangePassword()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun handleChangePassword() {
        val oldPassword = binding.etOldPassword.text.toString()
        val newPassword = binding.etNewPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()

        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Semua kolom harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPassword != confirmPassword) {
            Toast.makeText(this, "Kata sandi baru tidak cocok", Toast.LENGTH_SHORT).show()
            return
        }

        if (!sessionManager.checkPassword(oldPassword)) {
            Toast.makeText(this, "Kata sandi lama salah", Toast.LENGTH_SHORT).show()
            return
        }

        sessionManager.updatePassword(newPassword)
        Toast.makeText(this, "Kata sandi berhasil diubah", Toast.LENGTH_SHORT).show()
        finish()
    }
}
