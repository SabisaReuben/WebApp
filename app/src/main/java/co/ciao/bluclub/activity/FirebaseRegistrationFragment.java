package co.ciao.bluclub.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import co.ciao.bluclub.R;
import co.ciao.bluclub.data.SharedPref;
import co.ciao.bluclub.databinding.FragmentRegistarationFirebaseBinding;
import co.ciao.bluclub.model.LauncherInputs;
import co.ciao.bluclub.model.LauncherViewModel;
import co.ciao.bluclub.network.AccountVerificationAPI;
import co.ciao.bluclub.network.ConnectionService;
import co.ciao.bluclub.network.Success;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FirebaseRegistrationFragment extends Fragment implements Callback<Success> {
    private static final String TAG = FirebaseRegistrationFragment.class.getName();
    private ProgressBar progressBar;

    private DatabaseReference databaseReference;
    private LauncherInputs inputs;
    private SharedPref sharedPref;
    private FirebaseAuth firebaseAuth;
    public FirebaseRegistrationFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentRegistarationFirebaseBinding binding = FragmentRegistarationFirebaseBinding.inflate(
                inflater, container, false);
        LauncherViewModel launcherViewModel = new ViewModelProvider(requireActivity()).get(
                LauncherViewModel.class);
        launcherViewModel.getMutableLiveData().observe(requireActivity(),binding::setInput);
        binding.setFragment(this);
        progressBar = binding.progressFirebase;

        sharedPref = new SharedPref(requireContext());
        return binding.getRoot();
    }
    public void verifyEmailWithDataBase(View view, LauncherInputs inputs){
        progressBar.setVisibility(View.VISIBLE);
       this.inputs = inputs;
        AccountVerificationAPI api = ConnectionService.create(AccountVerificationAPI.class);
        api.verify(inputs.getEmailAddress(), inputs.getCountry()).enqueue(this);
    }

    @Override
    public void onResponse(@NonNull Call<Success> call, @NonNull Response<Success> response) {
        Toast.makeText(requireContext(), response.body()!=null?response.body().message:"",
                Toast.LENGTH_SHORT).show();
        if (response.body().isSuccess) {
            if (inputs != null) {
                sharedPref.setVerifiedFirebaseEmailAndPassword(inputs.getEmailAddress(),
                        inputs.getEmailAddress());

                //store the information to the server
                firebaseAuth.createUserWithEmailAndPassword(inputs.getEmailAddress(),"password")
                        .addOnCompleteListener(task -> {
                            progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                Toast.makeText(requireContext(), "Registration completed successfully"
                                        , Toast.LENGTH_SHORT).show();
                                //TODO:close the fragment and update the shared pref

                            }else {
                                Toast.makeText(requireContext(), "Request failed", Toast.LENGTH_SHORT).show();
                            }
                        });
            }


        }else{
            Toast.makeText(requireContext(), "No user registered with email", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFailure(@NonNull Call<Success> call, @NonNull Throwable t) {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(requireContext(), "Request could not be completed", Toast.LENGTH_SHORT)
                .show();
    }

}