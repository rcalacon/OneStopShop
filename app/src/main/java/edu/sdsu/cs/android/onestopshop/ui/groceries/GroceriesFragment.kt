package edu.sdsu.cs.android.onestopshop.ui.groceries

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.android.criminalintent.Crime
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.sdsu.cs.android.onestopshop.R
import edu.sdsu.cs.android.onestopshop.UserListActivity
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*

class GroceriesFragment : Fragment() {

    interface Callbacks {
        fun onCrimeSelected(crimeId: String)
    }

    private var callbacks: Callbacks? = null

    private lateinit var groceriesViewModel: GroceriesViewModel
    private val db = Firebase.firestore

    private lateinit var crimeRecyclerView: RecyclerView
    private var adapter: CrimeAdapter? = CrimeAdapter(emptyList())

    override fun onAttach(context: Context){
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        groceriesViewModel =
            ViewModelProviders.of(this).get(GroceriesViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_groceries, container, false)
        val textView: TextView = root.findViewById(R.id.text_groceries)
        groceriesViewModel.text.observe(this, Observer {
            textView.text = it
        })

        crimeRecyclerView =
            root.findViewById(R.id.crime_recycler_view) as RecyclerView
        crimeRecyclerView.layoutManager = LinearLayoutManager(context)
        crimeRecyclerView.adapter = adapter

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initializeGroceryList()
    }

    private fun initializeGroceryList(){
        db.collection("groceries")
            .get()
            .addOnSuccessListener { result ->
                for (grocery in result) {
                    val currentGroceryData = grocery.data
                    val groceryId = currentGroceryData["id"]
                    val groceryName = currentGroceryData["name"]
                    //place data into recyclerview
                }
            }
            .addOnFailureListener { exception ->
                Log.w("RCA", "Error getting documents.", exception)
                login_progress.visibility = View.INVISIBLE
            }
    }

    private inner class CrimeHolder(view: View)
        : RecyclerView.ViewHolder(view), View.OnClickListener {

        private lateinit var crime: Crime

        private val titleTextView: TextView = itemView.findViewById(R.id.crime_title)
        private val dateTextView: TextView = itemView.findViewById(R.id.crime_date)
        private val solvedImageView: ImageView = itemView.findViewById(R.id.crime_solved)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(crime: Crime) {
            this.crime = crime
            titleTextView.text = this.crime.title
            dateTextView.text = this.crime.date.toString()
            solvedImageView.visibility = if (crime.isSolved) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        override fun onClick(v: View) {
            callbacks?.onCrimeSelected(crime.id)
        }

    }

    private inner class CrimeAdapter(var crimes: List<Crime>)
        :RecyclerView.Adapter<CrimeHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
            val view = layoutInflater.inflate(R.layout.list_item_crime, parent, false)
            return CrimeHolder(view)
        }

        override fun getItemCount() = crimes.size

        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
            val crime = crimes[position]
            holder.bind(crime)
        }
    }

    companion object {
        fun newInstance(crimeId: String) : GroceriesFragment {
            val args = Bundle().apply {
                putSerializable("grocery_id", crimeId)
            }
            return GroceriesFragment().apply{
                arguments = args
            }
        }
    }
}