package edu.sdsu.cs.android.onestopshop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_welcome.*
import java.io.BufferedReader
import java.io.InputStream

class WelcomeActivity : AppCompatActivity() {

    private val db = Firebase.firestore
    private var groceryFileNumLines = 0
    private var groceryDbNumInserts = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        db.collection("init")
            .get()
            .addOnSuccessListener { result ->
                if(result.isEmpty){
                    welcome_info.visibility = View.VISIBLE

                    // initialize groceries collection
                    val groceriesInputStream: InputStream = resources.openRawResource(R.raw.groceries)
                    val groceriesBufferedReader: BufferedReader = groceriesInputStream.bufferedReader()

                    var grocery:String? = groceriesBufferedReader.readLine()

                    val groceryIdIndex = 1
                    val groceryNameIndex = 0

                    while(grocery !== null){
                        groceryFileNumLines ++

                        val groceryInfo = grocery.split(getString(R.string.grocery_data_delimiter))
                        val groceryMap = hashMapOf(
                            "name" to groceryInfo[groceryIdIndex],
                            "id" to groceryInfo[groceryNameIndex]
                        )
                        db.collection("groceries")
                            .add(groceryMap)
                            .addOnSuccessListener { documentReference ->
                                groceryDbNumInserts ++
                                welcome_info_details.text = "Installed ${groceryDbNumInserts} out of ${groceryFileNumLines} items"

                                if (groceryFileNumLines === groceryDbNumInserts){
                                    welcome_info.text = getString(R.string.welcome_info_complete)
                                    welcome_info_details.visibility = View.INVISIBLE
                                    welcome_progress.visibility = View.INVISIBLE
                                    next_button.setEnabled(true)
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.w("RCA", "Error adding grocery from initialization list", e)
                            }
                        grocery = groceriesBufferedReader.readLine()
                    }

                    val initMap = hashMapOf(
                        "initialized" to true
                    )

                    // set grocery list initialized state to true
                    db.collection("init")
                        .add(initMap)
                        .addOnFailureListener { e ->
                            Log.w("RCA", "Error adding initialization information", e)
                        }
                } else {
                    val loginIntent = LoginActivity.newIntent(this@WelcomeActivity)
                    startActivity(loginIntent)
                    finish()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("RCA", "Error getting initialization information.", exception)
            }

        next_button.setOnClickListener {
            val loginIntent = LoginActivity.newIntent(this@WelcomeActivity)
            startActivity(loginIntent)
            finish()
        }
    }
}
