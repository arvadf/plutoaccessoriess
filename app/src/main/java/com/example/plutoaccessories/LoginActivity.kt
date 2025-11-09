package com.example.plutoaccessories.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.plutoaccessories.databinding.ActivityLoginBinding
import com.example.plutoaccessories.models.LoginResponse
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

    // Hapus companion object karena URL dikelola oleh ApiClient

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

        Log.d("LOGIN_DATA", "Mengirim -> Email: [$email], Password: [${password.replace(Regex("."), "*")}]")

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

        val loginCall = apiService.loginUser(email, password)
        loginCall.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                binding.progLogin.visibility = View.GONE
                binding.btnLogin.isEnabled = true

                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse?.success == 1 && loginResponse.user != null) {
                        val user = loginResponse.user
                        if (user.idUser != null) {
                            session.createLoginSession(
                                user.idUser,
                                user.nama,
                                user.email,
                                user.role,
                                user.alamat,
                                user.noHp
                            )
                            Toast.makeText(this@LoginActivity, "Login berhasil. Selamat datang, ${user.nama}", Toast.LENGTH_SHORT).show()
                            gotoAppropriateActivity(user.role)
                            finish()
                        } else {
                            Log.e("LOGIN_ERROR", "Login sukses tapi idUser dari server null.")
                            Toast.makeText(this@LoginActivity, "Login Gagal: Data pengguna tidak lengkap dari server.", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        val message = loginResponse?.message ?: "Username atau password salah."
                        Toast.makeText(this@LoginActivity, "Login Gagal: $message", Toast.LENGTH_LONG).show()
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Terjadi kesalahan pada server."
                    Log.e("LOGIN_ERROR", "Error Code: ${response.code()}, Body: $errorBody")
                    Toast.makeText(this@LoginActivity, "Error: ${response.code()} - $errorBody", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
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