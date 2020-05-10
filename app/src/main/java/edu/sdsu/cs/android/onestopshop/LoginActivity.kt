package edu.sdsu.cs.android.onestopshop

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        username_login_field.onFocusChangeListener = View.OnFocusChangeListener { view, focus ->
            if(!focus){
                val userNameField = view as EditText
                val userNameProvided = userNameField.text.toString()
                val usernameValid = inputValid(userNameProvided)
                if(usernameValid){
                    username_login_field.background = null
                }
            }
        }

        password_login_field.onFocusChangeListener = View.OnFocusChangeListener { view, focus ->
            if(!focus){
                val passwordField = view as EditText
                val passwordProvided = passwordField.text.toString()
                val passwordValid = inputValid(passwordProvided)
                if(passwordValid){
                    password_login_field.background = null
                }
            }
        }

        login_button.setOnClickListener {
            val providedUsernameInput = username_login_field.text.toString()
            val providedPasswordInput = password_login_field.text.toString()
            val validUser = inputValid(providedUsernameInput)
            val validPassword = inputValid(providedPasswordInput)

            if(validUser) username_login_field.background = null
            if(validPassword) password_login_field.background = null

            if(validUser && validPassword){
                login_progress.visibility = View.VISIBLE
                authenticate(providedUsernameInput, providedPasswordInput)
            }else{
                if(!validUser) username_login_field.background = getDrawable(R.drawable.error_edit_text)
                if(!validPassword) password_login_field.background = getDrawable(R.drawable.error_edit_text)

                Toast.makeText(this, getString(R.string.invalid_login_fields), Toast.LENGTH_LONG)
                .show()
            }
        }

        register_button.setOnClickListener {
            val registerIntent = RegisterActivity.newIntent(this@LoginActivity,
                ArrayList())
            startActivity(registerIntent)
        }
    }

    private fun authenticate(userNameProvided:String, passwordProvided:String){
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                var credentialsValid = false
                for (document in result) {
                    val currentUserInfo = document.data
                    val currentUsername = currentUserInfo["username"]
                    if(currentUsername == userNameProvided){
                        val password = currentUserInfo["password"]
                        if(password == passwordProvided) {
                            credentialsValid = true
                        }
                    }
                }
                if(credentialsValid){
                    //TODO: Provide username to bundle
                    val userListIntent = UserListActivity.newIntent(this@LoginActivity,
                        ArrayList())
                    startActivity(userListIntent)
                }else{
                    Toast.makeText(this, getString(R.string.auth_fail_text), Toast.LENGTH_LONG)
                        .show()
                }
                login_progress.visibility = View.INVISIBLE
            }
            .addOnFailureListener { exception ->
                Log.w("RCA", "Error getting documents.", exception)
                login_progress.visibility = View.INVISIBLE
            }
    }

    private fun inputValid(userInput: String):Boolean{
        return (userInput != null && userInput != "")
    }

    companion object {
        fun newIntent(packageContext: Context, enrolledClasses:ArrayList<String>) : Intent {
            return Intent(packageContext, LoginActivity::class.java).apply {
                //TODO: Put intent values as needed putExtra(STUDENT_CLASSES_INTENT_KEY, enrolledClasses)
            }
        }
    }
}
