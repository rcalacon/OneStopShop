package edu.sdsu.cs.android.onestopshop.ui.groceries

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import edu.sdsu.cs.android.onestopshop.R

class GroceriesFragment : Fragment() {

    private lateinit var groceriesViewModel: GroceriesViewModel

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
}