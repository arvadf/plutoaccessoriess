package com.example.plutoaccessories.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.plutoaccessories.R
import com.example.plutoaccessories.models.UserModel
import com.example.plutoaccessories.network.ApiClient
import com.example.plutoaccessories.network.ApiService
import com.example.plutoaccessories.utils.SessionManager
import com.example.plutoaccessories.utils.ValidationHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var etNama: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etAlamat: EditText
    private lateinit var etNoHp: EditText
    private lateinit var btnRegister: Button
    private lateinit var btnLogin: Button
    private lateinit var progressDialog: ProgressDialog
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etNama = findViewById(R.id.etNama)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etAlamat = findViewById(R.id.etAlamat)
        etNoHp = findViewById(R.id.etNoHp)
        btnRegister = findViewById(R.id.btnRegister)
        btnLogin = findViewById(R.id.btnLogin)

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Mendaftarkan akun...")
        progressDialog.setCancelable(false)

        sessionManager = SessionManager(this)

        btnRegister.setOnClickListener { registerUser() }

        btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun registerUser() {
        val nama = etNama.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val alamat = etAlamat.text.toString().trim()
        val no_hp = etNoHp.text.toString().trim()

        if (!ValidationHelper.isValidName(nama)) {
            etNama.error = "Nama minimal 3 huruf"
            return
        }
        if (!ValidationHelper.isValidEmail(email)) {
            etEmail.error = "Email tidak valid"
            return
        }
        if (!ValidationHelper.isValidPassword(password)) {
            etPassword.error = "Password minimal 6 karakter"
            return
        }
        if (!ValidationHelper.isValidPhone(no_hp)) {
            etNoHp.error = "Nomor HP tidak valid"
            return
        }
        if (!ValidationHelper.isValidAddress(alamat)) {
            etAlamat.error = "Alamat minimal 5 karakter"
            return
        }

        progressDialog.show()
        val apiService: ApiService = ApiClient.getClient().create(ApiService::class.java)
        val userRequest = UserModel(
            nama = nama,
            email = email,
            password = password,
            alamat = alamat,
            noHp = no_hp,
            role = "user"
        )

        apiService.registerUser(userRequest).enqueue(object : Callback<List<UserModel>> {
            override fun onResponse(call: Call<List<UserModel>>, response: Response<List<UserModel>>) {
                progressDialog.dismiss()
                if (response.isSuccessful) {
                    val createdUser = response.body()?.firstOrNull()
                    val userId = createdUser?.idUser ?: -1
                    sessionManager.createLoginSession(
                        userId,
                        createdUser?.nama ?: nama,
                        createdUser?.email ?: email,
                        createdUser?.password ?: password,
                        createdUser?.role ?: "user",
                        createdUser?.alamat ?: alamat,
                        createdUser?.noHp ?: no_hp,
                        null // Pass null for profileImage
                    )
                    Toast.makeText(this@RegisterActivity, "Registrasi berhasil. Silakan login.", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                    finish()
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Gagal mendaftarkan pengguna."
                    Toast.makeText(this@RegisterActivity, errorMsg, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<UserModel>>, t: Throwable) {
                progressDialog.dismiss()
                Toast.makeText(this@RegisterActivity, "Koneksi gagal: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
