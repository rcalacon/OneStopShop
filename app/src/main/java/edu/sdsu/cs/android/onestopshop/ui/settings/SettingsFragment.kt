package edu.sdsu.cs.android.onestopshop.ui.settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.sdsu.cs.android.onestopshop.R
import kotlinx.android.synthetic.main.fragment_settings.*
import java.io.BufferedReader
import java.io.File
import java.io.InputStream

class SettingsFragment : Fragment() {

    private lateinit var settingsViewModel: SettingsViewModel
    // Access a Cloud Firestore instance from your Activity
    val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        settingsViewModel =
            ViewModelProviders.of(this).get(SettingsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_settings, container, false)
        val textView: TextView = root.findViewById(R.id.text_settings)
        settingsViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        reset_button.setOnClickListener {
            val groceryMap = hashMapOf(
                "name" to "milk"
            )

            val groceriesInputStream:InputStream = resources.openRawResource(R.raw.groceries)
            val groceriesBufferedReader:BufferedReader = groceriesInputStream.bufferedReader()
            var grocery:String? = groceriesBufferedReader.readLine()
            var numLines:Int = 0
            while(grocery !== null){
                //Log.e("RCA", grocery)
                grocery = groceriesBufferedReader.readLine()
                numLines++
            }
            print(numLines) //TODO - delete duplicate lines in raw resource file.
            /*
                db.collection("groceries")
                .add(grocery)
                .addOnSuccessListener { documentReference ->
                    Log.d("RCA", "DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w("RCA", "Error adding document", e)
                }
            */
        }
    }
}