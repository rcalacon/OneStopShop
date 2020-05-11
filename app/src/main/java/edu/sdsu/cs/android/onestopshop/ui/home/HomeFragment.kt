package edu.sdsu.cs.android.onestopshop.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import edu.sdsu.cs.android.onestopshop.R
import kotlinx.android.synthetic.main.fragment_home.*

private const val USER_INTENT_KEY:String = "username"

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    lateinit var username:String
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
}