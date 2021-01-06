package co.ciao.bluclub.widget;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;

import co.ciao.bluclub.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class BluClubAlertDialog extends DialogFragment implements DialogInterface {
    private static final String MESSAGE = "message";
    private static final String TITLE = "title";
    private static final String ICON_RES_ID = "resource_id";
    private static final String POSITIVE = "positive";
    private static final String NEGATIVE = "negative";

    private Callback callback;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public static BluClubAlertDialog newInstance(String message, String title, @DrawableRes int icon,
                                                 String positive, String negative) {

        Bundle args = new Bundle();
        args.putString(MESSAGE, message);
        args.putString(TITLE, title);
        args.putString(POSITIVE, positive);
        args.putString(NEGATIVE, negative);
        args.putInt(ICON_RES_ID,icon);

        BluClubAlertDialog fragment = new BluClubAlertDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        /*MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        if (savedInstanceState!=null) {
            builder.setIcon(savedInstanceState.getInt(ICON_RES_ID));
            builder.setMessage(savedInstanceState.getString(MESSAGE));
            builder.setTitle(savedInstanceState.getString(TITLE));
            builder.setPositiveButton(savedInstanceState.getString(POSITIVE,"OK"),
                    (dialogInterface, i) -> {if(callback!=null) callback.onPositiveClick(dialogInterface);});
            builder.setNegativeButton(savedInstanceState.getString(NEGATIVE,"OK"),
                    (dialogInterface, i) -> {if(callback!=null) callback.onNegativeClick(dialogInterface);});
        }
        return builder.create();*/
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void cancel() {
        if ((getFragmentManager()!=null)) {
            getFragmentManager().beginTransaction().remove(this).commit();
        }
        this.dismiss();
    }


    public interface Callback{
        void onPositiveClick(DialogInterface dialogInterface);
        void onNegativeClick(DialogInterface dialogInterface);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if(container!=null){
        View view = inflater.inflate(R.layout.dialog_layout, container);
        MaterialTextView title = view.findViewById(R.id.title);
        MaterialTextView message = view.findViewById(R.id.message);
        MaterialButton positive = view.findViewById(R.id.positive);
        MaterialButton negative = view.findViewById(R.id.negative);
        CircleImageView icon = view.findViewById(R.id.icon);
        if (savedInstanceState != null) {
            title.setText(savedInstanceState.getString(TITLE));
            message.setText(savedInstanceState.getString(MESSAGE));
            positive.setText(savedInstanceState.getString(POSITIVE));
            negative.setText(savedInstanceState.getString(NEGATIVE));
            icon.setImageDrawable(ContextCompat.getDrawable(requireContext(),savedInstanceState
                    .getInt(ICON_RES_ID)));
        }
        positive.setOnClickListener(v->{if(callback!=null)callback.onPositiveClick(this);
        });
        negative.setOnClickListener(v->{if(callback!=null)callback.onNegativeClick(this);});
        return view;
    }
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
