package raymondbdev.eyeturner.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import camp.visual.gazetracker.callback.GazeCallback;
import raymondbdev.eyeturner.Model.EyeGesture;
import raymondbdev.eyeturner.Model.EyeGestureParser;
import raymondbdev.eyeturner.Model.GazeTrackerHelper;
import raymondbdev.eyeturner.Model.ParentViewModel;
import raymondbdev.eyeturner.databinding.FragmentPageBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PageFragment extends Fragment {

    private ParentViewModel parentViewModel;
    private GazeTrackerHelper gazeTrackerHelper;
    private EyeGestureParser eyeGestureParser;

    private static final String ARG_PAGE_CONTENTS = "PageContents";
    private String mPageContents;
    private Integer currentPageIndex = 0;

    private final List<String> pages;

    private FragmentPageBinding binding;

    GazeCallback readingCallback = gazeInfo -> {
        EyeGesture gesture = eyeGestureParser.calculateEyeGesture(gazeInfo);

        if(gesture == EyeGesture.LOOK_LEFT) {
            getActivity().runOnUiThread(this::previousPage);
        } else if (gesture == EyeGesture.LOOK_RIGHT) {
            getActivity().runOnUiThread(this::nextPage);

        }
    };

    public PageFragment() {
        // Required empty public constructor
        pages = new ArrayList<>();
        pages.add("The notion that useful conclusions could be drawn from information about the movement of\n" +
                "a person's eyes has been around for well over a century. For a sighted person, the primary\n" +
                "means of interacting with the world is through visual input, so it is natural to assume that\n" +
                "information about what a person is looking at in any given moment would be instrumental\n" +
                "in determining how that person is interacting with the world. In the field of humancomputer\n" +
                "interface design, knowledge about such interactions can be critical to designing a\n" +
                "powerful, intuitive and ultimately helpful user interface\n\nPage 1");
        pages.add("Eye tracking began in the late 1800s with mechanical devices that tracked light reflection\n" +
                "patterns or even materials directly embedded in the cornea. With the growth of\n" +
                "photography and video recording technology, far more reliable and less invasive means\n" +
                "were developed to simply observe a user's eye motions during long periods of activity.\n" +
                "These recordings would then be analyzed manually, often on a painstaking frame-by-frame\n" +
                "basis, generating mountains of data to analyze. The task was daunting and even in a\n" +
                "perfectly performed experiment, the data could very easily defy all attempts at\n" +
                "understanding.\n\nPage 2");
        pages.add("The growth of computing technology eventually eased the data analysis\n" +
                "task to the point of feasibility, and eye-tracking experiments became more popular, and\n" +
                "devices for incorporating eye gaze data into psychological and medial studies began to\n" +
                "appear. It is only recently, however, that computing power and video recording capability\n" +
                "have become inexpensive and powerful enough to cross the gap into the demanding, realtime\n" +
                "world of interface design. It is this frontier of the eye-tracking field that our project\n" +
                "seeks to explore. (For a detailed history of eye-tracking research, see [3]).\n\nPage 3");
        pages.add("Eye tracking devices have historically fallen into two categories. The first is passive, and\n" +
                "focused on detecting the gaze of the user relative to the rest of the world and in particular\n" +
                "what elements of the visible field are currently being focused upon. The second is more\n" +
                "active, and considers the eye not as simply a means of observation, but a means of control\n" +
                "as well. We think that a device capable of identifying deliberate movements of the eye area\n" +
                "(pupils, eyelids and eyebrows), we can provide a new means of interaction that could\n" +
                "replace or complement more standard interfaces.\n\nPage 4");
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param pageContents Parameter 1.
     * @return A new instance of fragment PageFragment.
     */
    // TODO: Rename and change types and number of parameters
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

        binding.pageTextView.setText(pages.get(0));

    }

    /**
     * Increments the page index and moves to the next page.
     */
    public void nextPage() {

        if(currentPageIndex < pages.size() - 1) {
            currentPageIndex++;
            binding.pageTextView.setText(pages.get(currentPageIndex));
        }

    }

    /**
     * Decrements the page index and moves to the previous page.
     */
    public void previousPage() {

        if(currentPageIndex > 0) {
            currentPageIndex--;
            binding.pageTextView.setText(pages.get(currentPageIndex));
        }

    }
}