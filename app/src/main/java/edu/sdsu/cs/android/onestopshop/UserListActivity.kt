package edu.sdsu.cs.android.onestopshop

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavGraph
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import edu.sdsu.cs.android.onestopshop.ui.groceries.GroceriesFragment
import edu.sdsu.cs.android.onestopshop.ui.groceries.GroceriesFragmentDirections
import edu.sdsu.cs.android.onestopshop.ui.home.HomeFragmentDirections
import edu.sdsu.cs.android.onestopshop.ui.settings.SettingsFragmentDirections
import kotlinx.android.synthetic.main.activity_user_list.*

private const val USER_INTENT_KEY:String = "username"

class UserListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)

        val navController = findNavController(R.id.nav_host_fragment)
        navController.setGraph(R.navigation.mobile_navigation, intent.extras)

        val username:String? = intent.getStringExtra(USER_INTENT_KEY)

        home_button.setOnClickListener{
            val originFragment:String? = navController.currentDestination?.label.toString()
            if(originFragment !== null && username !== null){
                if(originFragment == getString(R.string.title_groceries)){
                    navController.navigate(GroceriesFragmentDirections.groceriesToHome(username))
                }else if(originFragment == getString(R.string.title_settings)){
                    navController.navigate(SettingsFragmentDirections.settingsToHome(username))
                }
            }
        }

        groceries_button.setOnClickListener{
            val originFragment:String? = navController.currentDestination?.label.toString()
            if(originFragment !== null && username !== null){
                if(originFragment == getString(R.string.title_home)){
                    navController.navigate(HomeFragmentDirections.homeToGroceries(username))
                }else if(originFragment == getString(R.string.title_settings)){
                    navController.navigate(SettingsFragmentDirections.settingsToGroceries(username))
                }
            }
        }

        settings_button.setOnClickListener{
            val originFragment:String? = navController.currentDestination?.label.toString()
            if(originFragment !== null && username !== null){
                if(originFragment == getString(R.string.title_home)){
                    navController.navigate(HomeFragmentDirections.homeToSettings(username))
                }else if(originFragment == getString(R.string.title_groceries)){
                    navController.navigate(GroceriesFragmentDirections.groceriesToSettings(username))
                }
            }
        }
    }


    companion object {
        fun newIntent(packageContext: Context, userName:String) : Intent {
            return Intent(packageContext, UserListActivity::class.java).apply {
                putExtra(USER_INTENT_KEY, userName)
            }
        }
    }
}
