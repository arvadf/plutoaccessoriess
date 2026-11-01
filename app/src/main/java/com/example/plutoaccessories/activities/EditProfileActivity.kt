package com.example.plutoaccessories.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.plutoaccessories.R
import com.example.plutoaccessories.databinding.ActivityEditProfileBinding
import com.example.plutoaccessories.utils.SessionManager

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var sessionManager: SessionManager
    private var profileImageUri: Uri? = null

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            openGallery()
        } else {
            Toast.makeText(this, "Izin ditolak", Toast.LENGTH_SHORT).show()
        }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
                contentResolver.takePersistableUriPermission(uri, takeFlags)

                profileImageUri = uri
                sessionManager.updateProfileImage(uri.toString())

                Glide.with(this)
                    .load(uri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_person)
                    .into(binding.ivProfile)

                Toast.makeText(this, "Foto profil diperbarui", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        sessionManager = SessionManager(this)

        setupUserData()
        setupClickListeners()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        setupUserData()
    }

    private fun setupUserData() {
        val userDetails = sessionManager.userDetails
        binding.tvNama.text = userDetails[SessionManager.KEY_NAMA] ?: "-"
        binding.tvEmail.text = userDetails[SessionManager.KEY_EMAIL] ?: "-"
        binding.tvAlamat.text = userDetails[SessionManager.KEY_ALAMAT] ?: "-"
        binding.tvNoHp.text = userDetails[SessionManager.KEY_NO_HP] ?: "-"

        val profileImageUriString = userDetails[SessionManager.KEY_PROFIL_IMAGE]
        if (profileImageUriString != null) {
            try {
                profileImageUri = Uri.parse(profileImageUriString)
                Glide.with(this)
                    .load(profileImageUri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_person)
                    .into(binding.ivProfile)
            } catch (e: SecurityException) {
                binding.ivProfile.setImageResource(R.drawable.ic_person)
                sessionManager.updateProfileImage(null)
            }
        } else {
            binding.ivProfile.setImageResource(R.drawable.ic_person)
        }
    }

    private fun setupClickListeners() {
        binding.ivEditProfileImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        binding.ivEditNama.setOnClickListener {
            showEditDialog("Nama", binding.tvNama.text.toString()) { newValue ->
                val details = sessionManager.userDetails
                sessionManager.updateUserDetails(newValue, details[SessionManager.KEY_EMAIL]!!, details[SessionManager.KEY_ALAMAT]!!, details[SessionManager.KEY_NO_HP]!!)
                setupUserData()
            }
        }

        binding.ivEditEmail.setOnClickListener {
            showEditDialog("Email", binding.tvEmail.text.toString()) { newValue ->
                val details = sessionManager.userDetails
                sessionManager.updateUserDetails(details[SessionManager.KEY_NAMA]!!, newValue, details[SessionManager.KEY_ALAMAT]!!, details[SessionManager.KEY_NO_HP]!!)
                setupUserData()
            }
        }

        binding.ivEditAlamat.setOnClickListener {
            showEditDialog("Alamat", binding.tvAlamat.text.toString()) { newValue ->
                val details = sessionManager.userDetails
                sessionManager.updateUserDetails(details[SessionManager.KEY_NAMA]!!, details[SessionManager.KEY_EMAIL]!!, newValue, details[SessionManager.KEY_NO_HP]!!)
                setupUserData()
            }
        }

        binding.ivEditNoHp.setOnClickListener {
            showEditDialog("No. HP", binding.tvNoHp.text.toString()) { newValue ->
                val details = sessionManager.userDetails
                sessionManager.updateUserDetails(details[SessionManager.KEY_NAMA]!!, details[SessionManager.KEY_EMAIL]!!, details[SessionManager.KEY_ALAMAT]!!, newValue)
                setupUserData()
            }
        }

        binding.btnChangePassword.setOnClickListener {
            startActivity(Intent(this, ChangePasswordActivity::class.java))
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        galleryLauncher.launch(intent)
    }

    private fun showEditDialog(title: String, currentValue: String, onSave: (newValue: String) -> Unit) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Ubah $title")

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        input.setText(currentValue)
        builder.setView(input)

        builder.setPositiveButton("Simpan") { dialog, _ ->
            val newValue = input.text.toString().trim()
            if (newValue.isNotEmpty()) {
                onSave(newValue)
                Toast.makeText(this, "$title berhasil diperbarui", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "$title tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Batal") { dialog, _ -> dialog.cancel() }

        builder.show()
    }
}
