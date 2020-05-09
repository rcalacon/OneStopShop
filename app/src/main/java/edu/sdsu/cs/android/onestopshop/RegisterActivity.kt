package edu.sdsu.cs.android.onestopshop

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    var fieldsValid = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        submit_button.setOnClickListener {
            //check if
            if(fieldsValid) {

            }
        }

        cancel_button.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
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
