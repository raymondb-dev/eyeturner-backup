package raymondbdev.eyeturner.Activities

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
import raymondbdev.eyeturner.Model.GazeTrackerHelper
import raymondbdev.eyeturner.Model.ParentViewModel
import raymondbdev.eyeturner.Model.ReadingTracker
import raymondbdev.eyeturner.Model.SettingsManager
import raymondbdev.eyeturner.R
import raymondbdev.eyeturner.databinding.ActivityMainBinding

/**
 * Permissions adapted from SeeSo API Quick Start (Java) Guide:
 * https://docs.seeso.io/nonversioning/quick-start/android-quick-start/
 */
class MainActivity: AppCompatActivity() {

    private var parentViewModel: ParentViewModel? = null
    private var appBarConfiguration: AppBarConfiguration? = null
    private var binding: ActivityMainBinding? = null
    private var gazeTrackerHelper: GazeTrackerHelper? = null

    companion object {
        private val PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        private const val REQ_PERMISSION = 1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        gazeTrackerHelper = GazeTrackerHelper(applicationContext)

        // Set up ViewModel to pass use single instances between
        parentViewModel = ViewModelProvider(this).get(ParentViewModel::class.java)
        parentViewModel!!.setTracker(gazeTrackerHelper!!)
        parentViewModel!!.setSettingsManager(SettingsManager())
        parentViewModel!!.setVibrator(this.getSystemService(Vibrator::class.java))
        parentViewModel!!.setReadingHelper(ReadingTracker(Reader()))
        parentViewModel!!.setMutableContentResolver(contentResolver)

        binding = ActivityMainBinding.inflate(layoutInflater)

        // Get camera and storage writing permissions
        checkPermission()

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


    // Checking Permissions
    private fun checkPermission() {
        // Check permission status
        if (!hasPermissions(PERMISSIONS)) {
            requestPermissions(PERMISSIONS, REQ_PERMISSION)
        } else {
            checkPermission(true)
        }
    }

    private fun hasPermissions(permissions: Array<String>): Boolean {
        var result: Int
        // Check permission status in string array
        for (perms in permissions) {
            if (perms == Manifest.permission.SYSTEM_ALERT_WINDOW) {
                if (!Settings.canDrawOverlays(this)) {
                    return false
                }
            }
            result = ContextCompat.checkSelfPermission(this, perms)
            if (result == PackageManager.PERMISSION_DENIED) {
                // When if unauthorized permission found
                return false
            }
        }

        // When if all permission allowed
        return true
    }

    private fun checkPermission(isGranted: Boolean) {
        if (isGranted) {
            permissionGranted()
        } else {
            finish()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQ_PERMISSION) {
            if (grantResults.isNotEmpty()) {
                val cameraPermissionAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                checkPermission(cameraPermissionAccepted)
            }
        }
    }

    fun permissionGranted() {
        // gazeTrackerHelper.initGazeTracker()
    }
}