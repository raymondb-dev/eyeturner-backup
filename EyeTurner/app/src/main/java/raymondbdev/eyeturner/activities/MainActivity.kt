package raymondbdev.eyeturner.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Vibrator
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.navigateUp
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import com.github.mertakdut.Reader
import raymondbdev.eyeturner.Model.*
import raymondbdev.eyeturner.R
import raymondbdev.eyeturner.databinding.ActivityMainBinding


/**
 * Permissions code adapted from SeeSo API Quick Start (Java) Guide:
 * https://docs.seeso.io/nonversioning/quick-start/android-quick-start/
 */
class MainActivity: AppCompatActivity() {

    private var parentViewModel: ParentViewModel? = null
    private var appBarConfiguration: AppBarConfiguration? = null
    private var binding: ActivityMainBinding? = null
    private var gazeTrackerHelper: GazeTrackerHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        // TODO: PASTE YOUR DEV KEY HERE
        val yourDevKey = "dev_p41hlwcm3l84jjb55ujlflb6hf8mwjxq49l774yu"

        gazeTrackerHelper = GazeTrackerHelper(applicationContext, yourDevKey)

        val reader = Reader()
        val libraryDBHelper = LibraryDBHelper(this)

        // Set up ViewModel to pass use single instances between
        parentViewModel = ViewModelProvider(this).get(ParentViewModel::class.java)
        parentViewModel!!.setTracker(gazeTrackerHelper!!)
        parentViewModel!!.setSettingsManager(SettingsManager())
        parentViewModel!!.setVibrator(this.getSystemService(Vibrator::class.java))
        parentViewModel!!.setReadingHelper(ReadingTracker(reader, libraryDBHelper))
        parentViewModel!!.setMutableContentResolver(contentResolver)

        binding = ActivityMainBinding.inflate(layoutInflater)

        // Setting up navigation components
        setContentView(binding!!.getRoot())
        setSupportActionBar(binding!!.toolbar)

        val navController = findNavController(this, R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration.Builder(navController.graph).build()
        setupActionBarWithNavController(this, navController, appBarConfiguration!!)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(this, R.id.nav_host_fragment_content_main)
        return (navigateUp(navController, appBarConfiguration!!)
                || super.onSupportNavigateUp())
    }

}