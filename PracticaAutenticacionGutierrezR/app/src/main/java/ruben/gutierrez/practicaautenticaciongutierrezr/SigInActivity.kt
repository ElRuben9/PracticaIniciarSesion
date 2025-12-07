package ruben.gutierrez.practicaautenticaciongutierrezr

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class SignInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var isLoading = false 

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        
        auth = Firebase.auth

        val email: EditText = findViewById(R.id.etrEmail)
        val password: EditText = findViewById(R.id.etrPassword)
        val confirmPassword: EditText = findViewById(R.id.etrConfirmPassword)
        val errorTv: TextView = findViewById(R.id.tvrError)
        val button: Button = findViewById(R.id.btnRegister)

        errorTv.visibility = View.INVISIBLE

        button.setOnClickListener {

            if (isLoading) return@setOnClickListener

            val emailText = email.text.toString().trim()
            val passwordText = password.text.toString().trim()
            val confirmText = confirmPassword.text.toString().trim()

            // ------- VALIDACIONES ---------

            if (emailText.isEmpty() || passwordText.isEmpty() || confirmText.isEmpty()) {
                showError("Todos los campos deben de ser llenados")
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                showError("Formato de correo inválido")
                return@setOnClickListener
            }

            if (passwordText.length < 6) {
                showError("La contraseña debe tener al menos 6 caracteres")
                return@setOnClickListener
            }

            if (passwordText != confirmText) {
                showError("Las contraseñas no coinciden")
                return@setOnClickListener
            }

            // Contraseña segura (opcional)
            if (!passwordText.matches(".*[A-Z].*".toRegex())) {
                showError("La contraseña debe incluir al menos una mayúscula")
                return@setOnClickListener
            }
            if (!passwordText.matches(".*[0-9].*".toRegex())) {
                showError("La contraseña debe incluir al menos un número")
                return@setOnClickListener
            }

            hideError()
            isLoading = true
            button.isEnabled = false

            signIn(emailText, passwordText, button)
        }
    }


    private fun signIn(email: String, password: String, button: Button) {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->

                isLoading = false
                button.isEnabled = true

                if (task.isSuccessful) {
                    Log.d("INFO", "signInWithEmail:success")

                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)

                } else {
                    Log.w("ERROR", "signInWithEmail:failure", task.exception)

                    // Mensajes más claros segun el error
                    val errorMsg = when (task.exception?.message) {
                        "The email address is already in use by another account." -> "El correo ya está registrado."
                        "The email address is badly formatted." -> "Correo con formato inválido."
                        else -> "El registro falló."
                    }

                    Toast.makeText(baseContext, errorMsg, Toast.LENGTH_SHORT).show()
                }
            }
    }


    private fun showError(msg: String) {
        val errorTv: TextView = findViewById(R.id.tvrError)
        errorTv.text = msg
        errorTv.visibility = View.VISIBLE
    }

    private fun hideError() {
        val errorTv: TextView = findViewById(R.id.tvrError)
        errorTv.visibility = View.INVISIBLE
    }
}
