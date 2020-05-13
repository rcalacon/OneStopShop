package edu.sdsu.cs.android.onestopshop.ui.settings

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import edu.sdsu.cs.android.onestopshop.R
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : Fragment() {

    private lateinit var settingsViewModel: SettingsViewModel

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

        settingsViewModel.text.value = getString(R.string.title_settings).toLowerCase()

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val underConstructionColor:String = "#777700"
        reset_button.setOnClickListener {
            val underConstructionSnackbar:Snackbar = Snackbar.make(it, R.string.under_construction, Snackbar.LENGTH_SHORT)
            underConstructionSnackbar.view.setBackgroundColor(Color.parseColor(underConstructionColor))
            underConstructionSnackbar.show()
        }
        duration_button.setOnClickListener {
            val underConstructionSnackbar:Snackbar = Snackbar.make(it, R.string.under_construction, Snackbar.LENGTH_SHORT)
            underConstructionSnackbar.view.setBackgroundColor(Color.parseColor(underConstructionColor))
            underConstructionSnackbar.show()
        }
        dark_theme.setOnClickListener {
            val underConstructionSnackbar:Snackbar = Snackbar.make(it, R.string.under_construction, Snackbar.LENGTH_SHORT)
            underConstructionSnackbar.view.setBackgroundColor(Color.parseColor(underConstructionColor))
            underConstructionSnackbar.show()
        }
    }
}