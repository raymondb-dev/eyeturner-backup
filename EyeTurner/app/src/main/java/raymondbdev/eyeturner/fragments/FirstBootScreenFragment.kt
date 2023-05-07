package raymondbdev.eyeturner.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import raymondbdev.eyeturner.Model.ParentViewModel
import raymondbdev.eyeturner.R
import raymondbdev.eyeturner.activities.MainActivity

/**
 * A simple [Fragment] subclass.
 * Use the [FirstBootScreenFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FirstBootScreenFragment : Fragment() {

    private val PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
    private val REQ_PERMISSION = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermission()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_first_boot_screen, container, false)
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
                if (!Settings.canDrawOverlays(requireActivity())) {
                    return false
                }
            }
            result = ContextCompat.checkSelfPermission(requireActivity(), perms)
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
            permissionApproved()
        } else {
            requireActivity().finish()
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

    fun permissionApproved() {
        val preferences = requireActivity().getSharedPreferences("settings", 0)

        if(preferences.getBoolean("FirstBoot", true)) {
            Log.i("EyeTurner Log", "User's first time using the application")

            // delete any existing data
            val parentViewModel = ViewModelProvider(requireActivity()).get(ParentViewModel::class.java)
            parentViewModel.readingTracker!!.clearDB()

            NavHostFragment.findNavController(this@FirstBootScreenFragment)
                .navigate(R.id.BootToTutorial_Transition)
        } else {
            NavHostFragment.findNavController(this@FirstBootScreenFragment)
                .navigate(R.id.BootToLibrary_Transition)
        }

    }

}