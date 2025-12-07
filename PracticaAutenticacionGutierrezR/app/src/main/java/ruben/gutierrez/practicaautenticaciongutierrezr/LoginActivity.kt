package ruben.gutierrez.practicaautenticaciongutierrezr

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var isLoading = false 

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()

        val email: EditText = findViewById(R.id.etEmail)
        val password: EditText = findViewById(R.id.etPassword)
        val errorTv: TextView = findViewById(R.id.tvError)
        val button: Button = findViewById(R.id.btnLogin)

        errorTv.visibility = View.INVISIBLE

        button.setOnClickListener {
            if (isLoading) return@setOnClickListener

            val emailText = email.text.toString().trim()
            val passwordText = password.text.toString().trim()

            // ---------- VALIDACIONES ----------
            if (emailText.isEmpty()) {
                showError("El correo está vacío", true)
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                showError("Formato de correo inválido", true)
                return@setOnClickListener
            }

            if (passwordText.isEmpty()) {
                showError("La contraseña está vacía", true)
                return@setOnClickListener
            }

            if (passwordText.length < 6) {
                showError("La contraseña debe tener al menos 6 caracteres", true)
                return@setOnClickListener
            }

            isLoading = true
            button.isEnabled = false
            showError(visible = false)

            login(emailText, passwordText)
        }
    }

    fun goToMain(user: FirebaseUser) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("user", user.email)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    fun showError(text: String = "", visible: Boolean) {
        val errorTv: TextView = findViewById(R.id.tvError)
        errorTv.text = text
        errorTv.visibility = if (visible) View.VISIBLE else View.INVISIBLE
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            goToMain(currentUser)
        }
    }

    fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                val button: Button = findViewById(R.id.btnLogin)

                isLoading = false
                button.isEnabled = true

                if (task.isSuccessful) {
                    val user = auth.currentUser
                    showError(visible = false)
                    goToMain(user!!)
                } else {
                    showError("Usuario y/o contraseña incorrectos", true)
                }
            }
    }
}
