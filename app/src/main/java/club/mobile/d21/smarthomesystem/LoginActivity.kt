package club.mobile.d21.smarthomesystem

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import club.mobile.d21.smarthomesystem.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity: AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        val sharedPref = getSharedPreferences("AccountPrefs", Context.MODE_PRIVATE)
        val savedUserId = sharedPref.getString("USER_ID", null)
        if (savedUserId != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.loginButton.setOnClickListener {
            val email = binding.emailInput.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter full email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        user?.getIdToken(true)?.addOnCompleteListener { tokenTask ->
                            if (tokenTask.isSuccessful) {
                                val idToken = tokenTask.result?.token
                                Log.e("AuthToken", idToken ?: "No Token Found")

                                // Lưu idToken vào SharedPreferences
                                with(sharedPref.edit()) {
                                    putString("ID_TOKEN", idToken)
                                    apply()
                                }

                                // Chuyển đến MainActivity
                                startActivity(Intent(this, MainActivity::class.java))
                                finish()
                            } else {
                                Log.e("TokenError", "Failed to get token: ${tokenTask.exception?.message}")
                            }
                        }
                    } else {
                        Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }

        }
    }
}
