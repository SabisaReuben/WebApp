package co.ciao.bluclub.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import co.ciao.bluclub.databinding.FragmentCountrySelectionBinding;
import co.ciao.bluclub.model.CountryUrls;
import co.ciao.bluclub.model.LauncherInputs;
import co.ciao.bluclub.model.LauncherViewModel;
import co.ciao.bluclub.utils.ItemAnimation;


public class CountrySelectionFragment extends Fragment {


    private LauncherViewModel viewModel;
    private LauncherInputs inputs;
    public CountrySelectionFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       FragmentCountrySelectionBinding binding= FragmentCountrySelectionBinding.inflate(
               inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(LauncherViewModel.class);
        viewModel.getMutableLiveData().observe(requireActivity(), inputs1 -> inputs = inputs1);
        binding.setFragment(this);
        return binding.getRoot();
    }
    public void countrySelected(View view, CountryUrls urls){
        viewModel.addCountryAndUrl(formatCountryName(urls.name()),urls.getUrl());
        ItemAnimation.expandOnClick(view);
    }
    private String formatCountryName(String value) {
        value = value.toLowerCase();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if(i==0 && Character.isLetter(c)) c = Character.toUpperCase(c);
            if ('_' == c) {
                c = ' ';
            }
            builder.append(c);
        }
        return builder.toString().trim();
    }
    public void swipeNextFragment(View view){
        if (inputs.getCountry().isEmpty()) {
            Toast.makeText(requireContext(), "Select country", Toast.LENGTH_SHORT).show();
            return;
        }
        //TODO: logic to swipe new fragment
    }
}