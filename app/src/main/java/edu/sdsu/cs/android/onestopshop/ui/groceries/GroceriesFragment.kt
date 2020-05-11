package edu.sdsu.cs.android.onestopshop.ui.groceries

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.sdsu.cs.android.onestopshop.R
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

        groceriesViewModel.text.value = getString(R.string.groceries_header).toLowerCase()
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        grocery_list_progress.visibility = View.VISIBLE
        initializeGroceryList()
    }

    private fun initRecyclerView(recyclerData:ArrayList<HashMap<String,String>>){
        viewManager = LinearLayoutManager(context)
        viewAdapter = MyAdapter(recyclerData, add_button)
        recyclerView = grocery_list.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
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

    class MyAdapter(private val myDataset: ArrayList<HashMap<String,String>>, val addButton: Button) :
        RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

        private val whiteRow:String = "#FFFFFF"
        private val grayRow:String = "#BBBBBB"
        private val selectedRow:String = "#5FD47E"

        var selectedGroceryId:String = ""
        var selectedGroceryName:String = ""

        private var selectedGroceryTextView:TextView? = null
        private var selectedGroceryOriginalColor:Int = 0

        class MyViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

        override fun onCreateViewHolder(parent: ViewGroup,
                                        viewType: Int): MyAdapter.MyViewHolder {
            val textView = LayoutInflater.from(parent.context)
                .inflate(R.layout.grocery_item_text_view, parent, false) as TextView
            return MyViewHolder(textView)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val groceryData = myDataset[position]
            holder.textView.text = groceryData["name"]
            holder.textView.hint = groceryData["id"]

            if(position % 2 == 0){
                holder.textView.setBackgroundColor(Color.parseColor(whiteRow))
            }else{
                holder.textView.setBackgroundColor(Color.parseColor(grayRow))
            }

            holder.textView.setOnClickListener {selectedGroceryItem ->
                addButton.setEnabled(true)

                //Clear highlight of previous
                if(selectedGroceryTextView !== null){
                    selectedGroceryTextView?.setBackgroundColor(selectedGroceryOriginalColor)
                }

                //update tracking variables
                selectedGroceryTextView = selectedGroceryItem as TextView
                val selectedGroceryOriginalColorDrawable:ColorDrawable = selectedGroceryItem.background as ColorDrawable
                selectedGroceryOriginalColor = selectedGroceryOriginalColorDrawable.color

                selectedGroceryId = selectedGroceryTextView?.hint.toString()
                selectedGroceryName = selectedGroceryTextView?.text.toString()

                //highlight newly selected
                selectedGroceryTextView?.setBackgroundColor(Color.parseColor(selectedRow))

            }
        }

        override fun getItemCount() = myDataset.size
    }
}