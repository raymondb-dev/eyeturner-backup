package raymondbdev.eyeturner.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.github.mertakdut.Reader;
import com.github.mertakdut.exception.ReadingException;
import com.hbisoft.pickit.PickiT;
import com.hbisoft.pickit.PickiTCallbacks;

import java.io.File;
import java.util.ArrayList;

import camp.visual.gazetracker.callback.GazeCallback;
import raymondbdev.eyeturner.Model.EyeGestureParser;
import raymondbdev.eyeturner.Model.GazeTrackerHelper;
import raymondbdev.eyeturner.Model.ParentViewModel;
import raymondbdev.eyeturner.R;
import raymondbdev.eyeturner.databinding.FragmentLibraryBinding;

public class LibraryFragment extends Fragment implements PickiTCallbacks {

    private FragmentLibraryBinding binding;

    private ParentViewModel parentViewModel;
    private GazeTrackerHelper gazeTrackerHelper;
    private EyeGestureParser eyeGestureParser;

    private boolean readerInitialised = false;

    GazeCallback libraryGazeCallback = gazeInfo -> {
        /*
        EyeGesture gesture = eyeGestureParser.calculateEyeGesture(gazeInfo);

        if(gesture == EyeGesture.LOOK_UP) {

            Log.i("Eye Gesture Performed:", "Looked Up");
            requireActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gazeTrackerHelper.deinitGazeTracker();
                    NavHostFragment.findNavController(LibraryFragment.this)
                            .navigate(R.id.LibraryToSearch_Transition);
                }
            });

        } else if(gesture == EyeGesture.LOOK_DOWN) {
            Log.i("Eye Gesture Performed:", "Looked Down");
            requireActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gazeTrackerHelper.deinitGazeTracker();
                    NavHostFragment.findNavController(LibraryFragment.this)
                            .navigate(R.id.LibraryToSettings_Transition);
                }
            });

        }
        */
    };

    ActivityResultLauncher<Intent> openFileResultLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                // There are no request codes
                Intent data = result.getData();
                Log.i("File Opened", data.getDataString());
                dumpImageMetaData(data.getData());

                PickiT pickiT = new PickiT(requireActivity(), this, requireActivity());
                pickiT.getPath(data.getData(), Build.VERSION.SDK_INT);

                // InputStream inputStream = requireActivity().getContentResolver().openInputStream(data.getData());
                // OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("aliceinwonderland.epub", MODE_PRIVATE))

            }
    });

    public LibraryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        eyeGestureParser = new EyeGestureParser();
        parentViewModel = new ViewModelProvider(requireActivity()).get(ParentViewModel.class);
        gazeTrackerHelper = parentViewModel.getTracker().getValue();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        gazeTrackerHelper.setGazeCallback(libraryGazeCallback);
        gazeTrackerHelper.initGazeTracker();

        // Inflate the layout for this fragment
        binding = FragmentLibraryBinding.inflate(inflater, container, false);

        binding.buttonTutorial.setOnClickListener(view -> {
            gazeTrackerHelper.deinitGazeTracker();
            NavHostFragment.findNavController(LibraryFragment.this)
                    .navigate(R.id.LibraryToTutorial_Transition);
        });

        binding.buttonBook.setOnClickListener(view -> {
            if(readerInitialised) {
                gazeTrackerHelper.deinitGazeTracker();
                NavHostFragment.findNavController(LibraryFragment.this)
                        .navigate(R.id.LibraryToPage_Transition);
            } else {
                Toast.makeText(requireActivity(), "EPUB file is invalid.", Toast.LENGTH_SHORT);
            }
        });

        binding.buttonAddFile.setOnClickListener(view -> {
            openFilePicker();
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void openFilePicker() {
        Intent selectEBook = new Intent(Intent.ACTION_GET_CONTENT);
        selectEBook.setType("application/epub+zip");
        openFileResultLauncher.launch(selectEBook);
    }

    public void writeToDirectory(Uri uri) {
        File f = new File(uri.getPath());
        Log.i("Does File Exist:", String.valueOf(f.exists()));
//        FileOutputStream fOut = openFileOutput("file name", Context.MODE_PRIVATE);
    }

    public void createEpubFile() {

    }

    public void dumpImageMetaData(Uri uri) {

        // The query, because it only applies to a single document, returns only
        // one row. There's no need to filter, sort, or select fields,
        // because we want all fields for one document.
        Cursor cursor = requireActivity().getContentResolver()
                .query(uri, null, null, null, null, null);
        String TAG = "Metadata";

        try {
            // moveToFirst() returns false if the cursor has 0 rows. Very handy for
            // "if there's anything to look at, look at it" conditionals.
            if (cursor != null && cursor.moveToFirst()) {

                // Note it's called "Display Name". This is
                // provider-specific, and might not necessarily be the file name.
                String displayName = cursor.getString(
                        cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                Log.i(TAG, "Display Name: " + displayName);

                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                // If the size is unknown, the value stored is null. But because an
                // int can't be null, the behavior is implementation-specific,
                // and unpredictable. So as
                // a rule, check if it's null before assigning to an int. This will
                // happen often: The storage API allows for remote files, whose
                // size might not be locally known.
                String size = null;
                if (!cursor.isNull(sizeIndex)) {
                    // Technically the column stores an int, but cursor.getString()
                    // will do the conversion automatically.
                    size = cursor.getString(sizeIndex);
                } else {
                    size = "Unknown";
                }
                Log.i(TAG, "Size: " + size);
            }
        } finally {
            cursor.close();
        }
    }


    @Override
    public void PickiTonUriReturned() {

    }

    @Override
    public void PickiTonStartListener() {

    }

    @Override
    public void PickiTonProgressUpdate(int progress) {

    }

    @Override
    public void PickiTonCompleteListener(String path, boolean wasDriveFile, boolean wasUnknownProvider, boolean wasSuccessful, String Reason) {
        Log.i("File Opened", path);
        Reader reader = parentViewModel.getBookReader();
        reader.setMaxContentPerSection(1200);
        reader.setIsIncludingTextContent(true);

        try {
            reader.setFullContent(path);
            readerInitialised = true;
        } catch (ReadingException e) {
            // TODO: Tell user it doesn't work.
            throw new RuntimeException(e);
        }
    }

    @Override
    public void PickiTonMultipleCompleteListener(ArrayList<String> paths, boolean wasSuccessful, String Reason) {

    }
}