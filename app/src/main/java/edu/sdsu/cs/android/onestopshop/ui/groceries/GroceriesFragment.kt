package edu.sdsu.cs.android.onestopshop.ui.groceries

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.sdsu.cs.android.onestopshop.R
import kotlinx.android.synthetic.main.fragment_groceries.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class GroceriesFragment : Fragment() {

    private lateinit var groceriesViewModel: GroceriesViewModel
    private val db = Firebase.firestore

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: GroceryListAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    val args: GroceriesFragmentArgs by navArgs()

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
        grocery_progress.visibility = View.VISIBLE
        initializeGroceryList()

        add_button.setOnClickListener{
            grocery_progress.visibility = View.VISIBLE
            val groceryEntry = hashMapOf(
                "owner" to args.username,
                "name" to viewAdapter.selectedGroceryName,
                "id" to viewAdapter.selectedGroceryId
            )
            db.collection("items")
                .add(groceryEntry)
                .addOnSuccessListener { documentReference ->
                    Toast.makeText(
                        activity,
                        (getString(R.string.add_grocery_success) + "\n" + groceryEntry["name"]).toLowerCase(),
                        Toast.LENGTH_LONG
                    ).show()
                    grocery_progress.visibility = View.GONE
                    viewAdapter.clearSelectedGrocery()
                    add_button.setEnabled(false)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        activity,
                        (getString(R.string.add_grocery_fail) + "\n" + groceryEntry["name"]).toLowerCase(),
                        Toast.LENGTH_LONG
                    ).show()
                    grocery_progress.visibility = View.GONE
                }
        }
    }

    private fun initRecyclerView(recyclerData:ArrayList<HashMap<String,String>>){
        val selectedItemBackground = resources.getDrawable(R.drawable.selected_grocery_item)

        viewManager = LinearLayoutManager(context)
        viewAdapter = GroceryListAdapter(recyclerData, add_button, selectedItemBackground)
        recyclerView = grocery_list.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        search_bar.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                viewAdapter.filter.filter(newText)
                return false
            }
        })
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
                grocery_progress.visibility = View.GONE
                initRecyclerView(recyclerData)
            }
            .addOnFailureListener { exception ->
                Log.w("RCA", "Error getting documents.", exception)
                grocery_progress.visibility = View.INVISIBLE
            }
    }

    class GroceryListAdapter(private val groceryList: ArrayList<HashMap<String,String>>,
                             private val addButton: Button,
                             private val selectedItemBackground: Drawable) :
        RecyclerView.Adapter<GroceryListAdapter.MyViewHolder>(), Filterable {

        class MyViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

        private val whiteRow:String = "#FFFFFF"

        private var currentGroceryId:String = ""
        private var currentGroceryName:String = ""

        private var selectedGroceryTextView:TextView? = null
        private var selectedGroceryOriginalColor:Int = 0

        var groceryFilterList = ArrayList<HashMap<String,String>>()


        var selectedGroceryId:String
            get() = currentGroceryId
            set(updatedGroceryId) { currentGroceryId = updatedGroceryId }

        var selectedGroceryName:String
            get() = currentGroceryName
            set(updatedGroceryName) { currentGroceryName = updatedGroceryName }

        init {
            groceryFilterList = groceryList
        }

        override fun onCreateViewHolder(parent: ViewGroup,
                                        viewType: Int): GroceryListAdapter.MyViewHolder {
            val textView = LayoutInflater.from(parent.context)
                .inflate(R.layout.grocery_item_text_view, parent, false) as TextView
            return MyViewHolder(textView)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val groceryData = groceryFilterList[position]
            holder.textView.text = groceryData["name"]
            holder.textView.hint = groceryData["id"]
            holder.textView.setBackgroundColor(Color.parseColor(whiteRow))

            holder.textView.setOnClickListener {selectedGroceryItem ->
                if(selectedGroceryTextView !== null && currentGroceryId == (selectedGroceryItem as TextView).hint.toString()) {
                    addButton.setEnabled(false)

                    selectedGroceryTextView?.background = null
                    selectedGroceryTextView?.setBackgroundColor(selectedGroceryOriginalColor)
                    currentGroceryId = ""
                    currentGroceryName = ""
                    selectedGroceryTextView = null
                    selectedGroceryOriginalColor = 0
                }else{
                    addButton.setEnabled(true)

                    //Clear highlight of previous
                    if(selectedGroceryTextView !== null){
                        selectedGroceryTextView?.background = null
                        selectedGroceryTextView?.setBackgroundColor(selectedGroceryOriginalColor)
                    }

                    //update tracking variables
                    selectedGroceryTextView = selectedGroceryItem as TextView
                    val selectedGroceryOriginalColorDrawable:ColorDrawable = selectedGroceryItem.background as ColorDrawable
                    selectedGroceryOriginalColor = selectedGroceryOriginalColorDrawable.color

                    currentGroceryId = selectedGroceryTextView?.hint.toString()
                    currentGroceryName = selectedGroceryTextView?.text.toString()

                    //highlight newly selected
                    selectedGroceryTextView?.background = selectedItemBackground
                }
            }
        }

        /**
         *  General Filtering Capability of RecyclerView was found here:
         *  https://johncodeos.com/how-to-add-search-in-recyclerview-using-kotlin/
         *  Minor tweaks were made for it to work with my list
         */
        override fun getFilter(): Filter {
            return object : Filter() {
                override fun performFiltering(constraint: CharSequence?): FilterResults {
                    val charSearch = constraint.toString()
                    if (charSearch.isEmpty()) {
                        groceryFilterList = groceryList
                    } else {
                        val resultList = ArrayList<HashMap<String,String>>()
                        for (row in groceryList) {
                            val groceryName = row["name"]
                            if (groceryName !== null && groceryName.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT))) {
                                resultList.add(row)
                            }
                        }
                        groceryFilterList = resultList
                    }
                    val filterResults = FilterResults()
                    filterResults.values = groceryFilterList
                    return filterResults
                }

                @Suppress("UNCHECKED_CAST")
                override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                    groceryFilterList = results?.values as ArrayList<HashMap<String,String>>
                    notifyDataSetChanged()
                }

            }
        }

        fun clearSelectedGrocery(){
            currentGroceryId = ""
            currentGroceryName = ""

            if(selectedGroceryTextView !== null){
                selectedGroceryTextView?.background = null
                selectedGroceryTextView?.setBackgroundColor(selectedGroceryOriginalColor)
                selectedGroceryTextView = null
            }

            selectedGroceryOriginalColor = 0
        }

        override fun getItemCount() = groceryFilterList.size
    }
}