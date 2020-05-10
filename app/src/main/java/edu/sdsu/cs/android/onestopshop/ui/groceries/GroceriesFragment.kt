package edu.sdsu.cs.android.onestopshop.ui.groceries

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.sdsu.cs.android.onestopshop.R
import edu.sdsu.cs.android.onestopshop.UserListActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_groceries.*

class GroceriesFragment : Fragment() {

    private lateinit var groceriesViewModel: GroceriesViewModel
    private val db = Firebase.firestore

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

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

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        grocery_list_progress.visibility = View.VISIBLE
        initializeGroceryList()
    }

    private fun initRecyclerView(recyclerData:ArrayList<HashMap<String,String>>){
        viewManager = LinearLayoutManager(context)
        viewAdapter = MyAdapter(recyclerData)
        recyclerView = grocery_list.apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter
        }
    }

    private fun initializeGroceryList(){
        db.collection("groceries")
            .get()
            .addOnSuccessListener { result ->
                var recyclerData:ArrayList<HashMap<String,String>> = ArrayList()
                for (grocery in result) {
                    val currentGroceryItem = grocery.data
                    val recyclerItem:HashMap<String,String> = HashMap()
                    recyclerItem["name"] = currentGroceryItem["name"] as String
                    recyclerItem["id"] = currentGroceryItem["id"] as String
                    recyclerData.add(recyclerItem)
                }
                grocery_list_progress.visibility = View.GONE
                initRecyclerView(recyclerData)
            }
            .addOnFailureListener { exception ->
                Log.w("RCA", "Error getting documents.", exception)
                login_progress.visibility = View.INVISIBLE
            }
    }

    class MyAdapter(private val myDataset: ArrayList<HashMap<String,String>>) :
        RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

        private val whiteRow:String = "#FFFFFF"
        private val grayRow:String = "#BBBBBB"

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder.
        // Each data item is just a string in this case that is shown in a TextView.
        class MyViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)


        // Create new views (invoked by the layout manager)
        override fun onCreateViewHolder(parent: ViewGroup,
                                        viewType: Int): MyAdapter.MyViewHolder {
            // create a new view
            val textView = LayoutInflater.from(parent.context)
                .inflate(R.layout.grocery_item_text_view, parent, false) as TextView
            // set the view's size, margins, paddings and layout parameters
            //textView.width =
            return MyViewHolder(textView)
        }

        // Replace the contents of a view (invoked by the layout manager)
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            //holder.textView.text = myDataset[position]
            val groceryData = myDataset[position]
            holder.textView.text = groceryData["name"]

            if(position % 2 == 0){
                holder.textView.setBackgroundColor(Color.parseColor(whiteRow))
            }else{
                holder.textView.setBackgroundColor(Color.parseColor(grayRow))
            }
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = myDataset.size
    }
}