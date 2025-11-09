package com.example.plutoaccessories.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.plutoaccessories.R
import com.example.plutoaccessories.utils.SessionManager
import com.example.plutoaccessories.utils.ValidationHelper
import org.json.JSONException
import org.json.JSONObject

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

    companion object {
        private const val URL_REGISTER = "http://192.168.1.6/pluto_api/register_user.php"
    }

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

        val stringRequest = object : StringRequest(
            Request.Method.POST, URL_REGISTER,
            Response.Listener { response ->
                progressDialog.dismiss()
                try {
                    val jsonObject = JSONObject(response)
                    val success = jsonObject.getInt("success")
                    val message = jsonObject.getString("message")

                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

                    if (success == 1) {
                        val userId = jsonObject.optInt("id_user", -1)
                        sessionManager.createLoginSession(userId, nama, email, "user", alamat, no_hp)

                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error parsing data: " + response, Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error: VolleyError ->
                progressDialog.dismiss()
                Toast.makeText(this, "Koneksi gagal: ${error.message}", Toast.LENGTH_LONG).show()
            }) {

            override fun getParams(): MutableMap<String, String>? {
                val params = HashMap<String, String>()
                params["nama"] = nama
                params["email"] = email
                params["password"] = password
                params["alamat"] = alamat
                params["no_hp"] = no_hp
                return params
            }
        }

        Volley.newRequestQueue(this).add(stringRequest)
    }
}
