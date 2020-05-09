package edu.sdsu.cs.android.onestopshop

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login_button.setOnClickListener {
            val userListIntent = UserListActivity.newIntent(this@LoginActivity,
                ArrayList())
            startActivity(userListIntent)
        }

        register_button.setOnClickListener {
            val registerIntent = RegisterActivity.newIntent(this@LoginActivity,
                ArrayList())
            startActivity(registerIntent)
        }
    }

    companion object {
        fun newIntent(packageContext: Context, enrolledClasses:ArrayList<String>) : Intent {
            return Intent(packageContext, LoginActivity::class.java).apply {
                //TODO: Put intent values as needed putExtra(STUDENT_CLASSES_INTENT_KEY, enrolledClasses)
            }
        }
    }
}
