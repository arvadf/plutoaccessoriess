package com.example.plutoaccessories.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.plutoaccessories.activities.AdminDashboardActivity
import com.example.plutoaccessories.databinding.ActivityLoginBinding
import com.example.plutoaccessories.models.UserModel
import com.example.plutoaccessories.network.ApiClient
import com.example.plutoaccessories.network.ApiService
import com.example.plutoaccessories.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var session: SessionManager
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)
        if (session.isLoggedIn) {
            gotoAppropriateActivity(session.role)
            finish()
            return
        }

        apiService = ApiClient.getClient().create(ApiService::class.java)

        binding.btnLogin.setOnClickListener { attemptLogin() }
        binding.tvRegister.setOnClickListener { 
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun attemptLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (TextUtils.isEmpty(email)) {
            binding.etEmail.error = "Email wajib diisi"
            binding.etEmail.requestFocus()
            return
        }
        if (TextUtils.isEmpty(password)) {
            binding.etPassword.error = "Password wajib diisi"
            binding.etPassword.requestFocus()
            return
        }

        binding.progLogin.visibility = View.VISIBLE
        binding.btnLogin.isEnabled = false

        apiService.loginUser(emailFilter = "eq.$email", passwordFilter = "eq.$password").enqueue(object : Callback<List<UserModel>> {
            override fun onResponse(call: Call<List<UserModel>>, response: Response<List<UserModel>>) {
                binding.progLogin.visibility = View.GONE
                binding.btnLogin.isEnabled = true

                if (response.isSuccessful) {
                    val users = response.body()
                    if (users != null && users.isNotEmpty()) {
                        val user = users[0]
                        if (user.idUser != null) {
                            session.createLoginSession(
                                user.idUser,
                                user.nama,
                                user.email,
                                user.password,
                                user.role,
                                user.alamat,
                                user.noHp,
                                null // Pass null for profileImage
                            )
                            Toast.makeText(this@LoginActivity, "Login berhasil. Selamat datang, ${user.nama}", Toast.LENGTH_SHORT).show()
                            gotoAppropriateActivity(user.role)
                            finish()
                        } else {
                            Toast.makeText(this@LoginActivity, "ID Pengguna tidak valid", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(this@LoginActivity, "Email atau password salah", Toast.LENGTH_LONG).show()
                    }
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Terjadi kesalahan"
                    Log.e("LOGIN_ERROR", "Error Code: ${response.code()}, Message: $errorMessage")
                    Toast.makeText(this@LoginActivity, "Login Gagal: $errorMessage", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<UserModel>>, t: Throwable) {
                binding.progLogin.visibility = View.GONE
                binding.btnLogin.isEnabled = true
                Log.e("LOGIN_FAILURE", "Request Gagal: ", t)
                Toast.makeText(this@LoginActivity, "Koneksi Gagal: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun gotoAppropriateActivity(role: String?) {
        val intent = if ("admin".equals(role, ignoreCase = true)) {
            Intent(this, AdminDashboardActivity::class.java)
        } else {
            Intent(this, MainActivity::class.java)
        }
        startActivity(intent)
    }
}
