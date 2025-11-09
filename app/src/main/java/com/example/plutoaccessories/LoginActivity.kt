package com.example.plutoaccessories.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.plutoaccessories.databinding.ActivityLoginBinding
import com.example.plutoaccessories.utils.SessionManager
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var session: SessionManager

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

        binding.btnLogin.setOnClickListener { attemptLogin() }
        binding.tvRegister.setOnClickListener { 
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun attemptLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        Log.e("LOGIN_DATA", "Mengirim -> Email: [$email], Password: [$password]")

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

        val stringRequest = object : StringRequest(
            Request.Method.POST, LOGIN_URL,
            Response.Listener { response ->
                binding.progLogin.visibility = View.GONE
                binding.btnLogin.isEnabled = true
                Log.d("LOGIN_RESPONSE", "Response: $response")

                try {
                    val obj = JSONObject(response)
                    val success = obj.optInt("success", 0)
                    val message = obj.optString("message", "Terjadi kesalahan.")

                    if (success == 1) {
                        val user = obj.getJSONObject("user")
                        val idUser = user.getInt("id_user")
                        val nama = user.getString("nama")
                        val userEmail = user.getString("email")
                        val role = user.optString("role", "user")
                        val alamat = user.optString("alamat")
                        val noHp = user.optString("no_hp")

                        session.createLoginSession(idUser, nama, userEmail, role, alamat, noHp)
                        
                        Toast.makeText(this, "Login berhasil. Selamat datang, $nama", Toast.LENGTH_SHORT).show()
                        gotoAppropriateActivity(role)
                        finish()
                    } else {
                        Toast.makeText(this, "Login gagal: $message", Toast.LENGTH_LONG).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error parsing response: $response", Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener { error: VolleyError ->
                binding.progLogin.visibility = View.GONE
                binding.btnLogin.isEnabled = true
                val networkResponse = error.networkResponse
                val errorMsg = if (networkResponse != null) {
                    "Error code ${networkResponse.statusCode}: ${String(networkResponse.data)}"
                } else {
                    error.message ?: "Unknown Volley error"
                }
                Log.e("LOGIN_ERROR", errorMsg)
                Toast.makeText(this, "Koneksi Gagal: $errorMsg", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String>? {
                val params = HashMap<String, String>()
                params["email"] = email
                params["password"] = password
                return params
            }
        }

        Volley.newRequestQueue(this).add(stringRequest)
    }

    private fun gotoAppropriateActivity(role: String?) {
        val intent = if ("admin".equals(role, ignoreCase = true)) {
            Intent(this, AdminDashboardActivity::class.java)
        } else {
            Intent(this, MainActivity::class.java)
        }
        startActivity(intent)
    }

    companion object {
        private const val LOGIN_URL = "http://192.168.1.6/pluto_api/login_user.php"
    }
}