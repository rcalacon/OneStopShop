package edu.sdsu.cs.android.onestopshop.ui.home

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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.sdsu.cs.android.onestopshop.R
import kotlinx.android.synthetic.main.fragment_home.*

private const val USER_INTENT_KEY:String = "username"

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private val db = Firebase.firestore
    lateinit var username:String

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: GroceryListAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    val args: HomeFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        homeViewModel.text.observe(this, Observer {
            textView.text = it
        })

        val loginArguments:Bundle? = arguments

        if(loginArguments !== null){
            val username:String? = loginArguments.getString(USER_INTENT_KEY)
            if(username !== null){
                this.username = username
                homeViewModel.text.value = getString(R.string.intro).toLowerCase() + " " + username.toLowerCase()
            }
        }else if(args !== null){
            val username:String = args.username
            this.username = username
            homeViewModel.text.value = getString(R.string.intro).toLowerCase() + " " + username.toLowerCase()
        }
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        grocery_progress.visibility = View.VISIBLE
        initializeGroceryList(username)
    }

    private fun initRecyclerView(recyclerData:ArrayList<HashMap<String,String>>, numGroceries:Int){
        viewManager = LinearLayoutManager(context)
        viewAdapter = GroceryListAdapter(recyclerData, edit_button, delete_button)
        recyclerView = grocery_list.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
        list_summary.text = (getString(R.string.list_summary) + " " + numGroceries).toLowerCase()
    }

    private fun initializeGroceryList(username:String){
        db.collection("items")
            .whereEqualTo("owner", username)
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
                initRecyclerView(recyclerData, result.size())
            }
            .addOnFailureListener { exception ->
                Log.w("RCA", "Error getting documents.", exception)
                grocery_progress.visibility = View.INVISIBLE
            }
    }

    class GroceryListAdapter(private val myDataset: ArrayList<HashMap<String,String>>,
                             private val editButton: Button,
                             private val deleteButton: Button) :
        RecyclerView.Adapter<GroceryListAdapter.MyViewHolder>() {

        class MyViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)
        private val whiteRow:String = "#FFFFFF"
        private val selectedRow:String = "#5FD47E"

        private var currentGroceryId:String = ""
        private var currentGroceryName:String = ""

        private var selectedGroceryTextView:TextView? = null
        private var selectedGroceryOriginalColor:Int = 0

        var selectedGroceryId:String
            get() = currentGroceryId
            set(updatedGroceryId) { currentGroceryId = updatedGroceryId }

        var selectedGroceryName:String
            get() = currentGroceryName
            set(updatedGroceryName) { currentGroceryName = updatedGroceryName }

        override fun onCreateViewHolder(parent: ViewGroup,
                                        viewType: Int): GroceryListAdapter.MyViewHolder {
            val textView = LayoutInflater.from(parent.context)
                .inflate(R.layout.grocery_item_text_view, parent, false) as TextView
            return MyViewHolder(textView)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val groceryData = myDataset[position]
            holder.textView.text = groceryData["name"]
            holder.textView.hint = groceryData["id"]

            holder.textView.setBackgroundColor(Color.parseColor(whiteRow))

            holder.textView.setOnClickListener {selectedGroceryItem ->
                if(selectedGroceryTextView !== null && currentGroceryId == (selectedGroceryItem as TextView).hint.toString()) {
                    editButton.setEnabled(false)
                    deleteButton.setEnabled(false)

                    selectedGroceryTextView?.setBackgroundColor(Color.parseColor(whiteRow))
                    currentGroceryId = ""
                    currentGroceryName = ""
                    selectedGroceryTextView = null
                    selectedGroceryOriginalColor = 0
                }else{
                    editButton.setEnabled(true)
                    deleteButton.setEnabled(true)

                    //Clear highlight of previous
                    if(selectedGroceryTextView !== null){
                        selectedGroceryTextView?.setBackgroundColor(selectedGroceryOriginalColor)
                    }

                    //update tracking variables
                    selectedGroceryTextView = selectedGroceryItem as TextView
                    val selectedGroceryOriginalColorDrawable: ColorDrawable = selectedGroceryItem.background as ColorDrawable
                    selectedGroceryOriginalColor = selectedGroceryOriginalColorDrawable.color

                    currentGroceryId = selectedGroceryTextView?.hint.toString()
                    currentGroceryName = selectedGroceryTextView?.text.toString()

                    //highlight newly selected
                    selectedGroceryTextView?.setBackgroundColor(Color.parseColor(selectedRow))
                }

            }
        }
        override fun getItemCount() = myDataset.size
    }
}