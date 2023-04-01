package my.edu.tarc.epf

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import my.edu.tarc.epf.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_dividend, R.id.nav_investment
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        //Back Press Handler
        val backPressedCallback = object: OnBackPressedCallback(true){
            override fun handleOnBackPressed(){
                val exitDialogFragment = ExitDialogFragment()

                exitDialogFragment.show(supportFragmentManager, "ExitDialog")
            }
        }

        //Managing the ToolBar or Menu
        navController.addOnDestinationChangedListener{_, destination, _ ->
            if(destination.id == R.id.nav_about){
                binding.appBarMain.toolbar.menu.findItem(R.id.action_about).isVisible = false
                binding.appBarMain.toolbar.menu.findItem(R.id.action_settings).isVisible = false
            }
        }

        onBackPressedDispatcher.addCallback(backPressedCallback)

        //Handling the header click event
        val view = navView.getHeaderView(0)
        view.setOnClickListener{
            findNavController(R.id.nav_host_fragment_content_main)
                .navigate(R.id.nav_profile)
            binding.drawerLayout.closeDrawers()
        }
    }// end of onCreate

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.action_settings){
            Snackbar.make(findViewById(R.id.nav_host_fragment_content_main),"Settings",Snackbar.LENGTH_SHORT).show()
        }else if(item.itemId == R.id.action_about){
            findNavController(R.id.nav_host_fragment_content_main)
                    .navigate(R.id.nav_about)
        }
        return super.onOptionsItemSelected(item)
    }
    class ExitDialogFragment: DialogFragment(){
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val builder = AlertDialog.Builder(requireActivity())

            builder.setMessage(getString(R.string.exit_message))
                .setPositiveButton(getString(R.string.exit) ,
            {dialog, id ->
                requireActivity().finish() //terminate this app
            })
                .setNegativeButton(getString(R.string.cancel) ,
            {dialog, id ->
                //DO NOTHING
            })
            return builder.create()
        }
    }//Exit
}//Exit