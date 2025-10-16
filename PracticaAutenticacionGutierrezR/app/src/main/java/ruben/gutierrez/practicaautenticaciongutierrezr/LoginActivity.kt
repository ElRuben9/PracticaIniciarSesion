package ruben.gutierrez.practicaautenticaciongutierrezr

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import org.w3c.dom.Text

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = Firebase.auth

        val email: EditText = findViewById(R.id.etEmail)
        val password: EditText = findViewById(R.id.etPassword)
        val errorTv: TextView = findViewById(R.id.tvError)

        val button: Button = findViewById(R.id.btnLogin)

        errorTv.visibility = View.INVISIBLE

    }

    fun goToMain(user: FirebaseUser){
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("user",user.email)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    fun showError(text: String = "", visible: Boolean)
    {
        val errorTv: TextView = findViewById(R.id.tvError)
        errorTv.text = text
        errorTv.visibility = if (visible) View.VISIBLE else View.INVISIBLE

    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null){
            goToMain(currentUser)
        }
    }
    fun login(email: String, password: String){
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this){ task ->
            if (task.isSuccessful){
                val user = auth.currentUser
                showError(visible = false)
                goToMain(user!!)

            }else{
                showError("Usuario y/o contraseña equivacados",true)
            }
        }
    }
}