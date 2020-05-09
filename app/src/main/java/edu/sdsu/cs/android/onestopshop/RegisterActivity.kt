package edu.sdsu.cs.android.onestopshop

import android.app.Activity
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
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private val db = Firebase.firestore
    private var usernameValid = false
    private var passwordValid = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        username_register_field.onFocusChangeListener = View.OnFocusChangeListener { view, focus ->
            if(!focus){
                val userNameField = view as EditText
                val userNameProvided = userNameField.text.toString()
                usernameValid = validateUsername(userNameProvided)
                if(usernameValid){
                    checkIfUsernameExists(userNameProvided)
                }
            }
        }

        submit_button.setOnClickListener {
            //check if both fields are valid
            if(usernameValid && passwordValid) {
                Toast.makeText(this, "Registration complete!", Toast.LENGTH_SHORT).show()
            }
        }

        cancel_button.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    private fun validateUsername(userInput: String):Boolean{
        return (userInput !== null && userInput !== "")
    }

    private fun checkIfUsernameExists(userNameProvided:String){
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                var userExists = false
                for (document in result) {
                    val userInfo = document.data
                    val currentUsername = userInfo["username"]
                    if(currentUsername == userNameProvided){
                        userExists = true
                    }
                }
                if(userExists) {
                    username_register_field.background = getDrawable(R.drawable.error_edit_text)
                    user_status_text.text = getString(R.string.user_exists_error_message) + " " + userNameProvided
                    user_status_text.setTextColor(getColor(R.color.error_text_color))
                }else {
                    username_register_field.background = null
                    user_status_text.text = getString(R.string.user_available_message) + " " + userNameProvided
                    user_status_text.setTextColor(getColor(R.color.valid_text_color))
                }
            }
            .addOnFailureListener { exception ->
                Log.w("RCA", "Error getting documents.", exception)
            }
    }

    companion object {
        fun newIntent(packageContext: Context, enrolledClasses:ArrayList<String>) : Intent {
            return Intent(packageContext, RegisterActivity::class.java).apply {
                //TODO: Put intent values as needed putExtra(STUDENT_CLASSES_INTENT_KEY, enrolledClasses)
            }
        }
    }
}
