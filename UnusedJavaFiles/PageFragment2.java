package raymondbdev.eyeturner.UnusedJavaFiles;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mertakdut.BookSection;
import com.github.mertakdut.Reader;
import com.github.mertakdut.exception.OutOfPagesException;
import com.github.mertakdut.exception.ReadingException;

import camp.visual.gazetracker.callback.GazeCallback;
import raymondbdev.eyeturner.Fragments.PageFragment;
import raymondbdev.eyeturner.Model.Enums.EyeGesture;
import raymondbdev.eyeturner.Model.EyeGestureParser;
import raymondbdev.eyeturner.Model.GazeTrackerHelper;
import raymondbdev.eyeturner.Model.ParentViewModel;
import raymondbdev.eyeturner.Model.SettingsManager;
import raymondbdev.eyeturner.databinding.FragmentPageBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PageFragment2 extends Fragment {

    private ParentViewModel parentViewModel;
    private GazeTrackerHelper gazeTrackerHelper;
    private EyeGestureParser eyeGestureParser;
    private SettingsManager settingsManager;
    private Reader reader;

    private static final String ARG_PAGE_CONTENTS = "PageContents";
    private String mPageContents;
    private Integer currentPageIndex = 1;

    private FragmentPageBinding binding;

    GazeCallback readingCallback = gazeInfo -> {
        EyeGesture gesture = eyeGestureParser.calculateEyeGesture(gazeInfo);

        if(gesture == EyeGesture.LOOK_LEFT) {
            getActivity().runOnUiThread(this::previousPage);
        } else if (gesture == EyeGesture.LOOK_RIGHT) {
            getActivity().runOnUiThread(this::nextPage);

        }
    };

    public static PageFragment newInstance(String pageContents) {
        PageFragment fragment = new PageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PAGE_CONTENTS, pageContents);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mPageContents = getArguments().getString(ARG_PAGE_CONTENTS);
        }

        eyeGestureParser = new EyeGestureParser();
        parentViewModel = new ViewModelProvider(requireActivity()).get(ParentViewModel.class);
        gazeTrackerHelper = parentViewModel.getTracker().getValue();
        settingsManager = parentViewModel.getSettingsManager().getValue();
        reader = parentViewModel.getBookReader();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        gazeTrackerHelper.setGazeCallback(readingCallback);
        gazeTrackerHelper.initGazeTracker();

        // Inflate the layout for this fragment
        binding = FragmentPageBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.pageTextView.setText(retrievePageFromReader(currentPageIndex));
        binding.pageTextView.setTextSize(settingsManager.getFontSize());

    }

    /**
     * Increments the page index and moves to the next page.
     */
    public void nextPage() {

        // TODO: add OutOfPagesException
        if(currentPageIndex < 100) {
            currentPageIndex++;
            binding.pageTextView.setText(retrievePageFromReader(currentPageIndex));
        }

    }

    /**
     * Decrements the page index and moves to the previous page.
     */
    public void previousPage() {

        if(currentPageIndex > 1) {
            currentPageIndex--;
            binding.pageTextView.setText(retrievePageFromReader(currentPageIndex));
        }

    }

    /**
     * Retrieves content at PageIndex
     * @param index
     * @return
     */
    public String retrievePageFromReader(int index) {
        String content = "";

        try {
            BookSection bookSection = reader.readSection(index);
            content = bookSection.getSectionTextContent();
        } catch(OutOfPagesException | ReadingException readingError) {
            Log.w("Reading Error", readingError);
        }

        return content;

    }
}