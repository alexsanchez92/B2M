package asm.uabierta.fragments.dialogs;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.path.android.jobqueue.JobManager;
import com.squareup.otto.Subscribe;

import asm.uabierta.R;
import asm.uabierta.events.SimpleResponseEvent;
import asm.uabierta.jobs.UpdateUser;
import asm.uabierta.utils.BusProvider;
import asm.uabierta.utils.Constants;
import asm.uabierta.utils.UserPreferences;
import asm.uabierta.utils.UtilsFunctions;

/**
 * Created by Alex on 04/08/2016.
 */

public class ChangeEmailDialog extends DialogFragment{

    public int max_long_char = 50;

    public interface ChangeEmailDialogListener {
        void onFinishEmailDialog(String inputText);
    }

    private Context context;
    private UserPreferences userPreferences;
    private JobManager jobManager;

    private TextView tvNew, currentValue;
    private EditText etNewValue;
    private Button btnAccept, btnCancel;
    private String new_email;

    private ProgressDialog pDialog;

    public ChangeEmailDialog() {
        // Empty constructor required for DialogFragment
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    //JOB EVENTS
    @Subscribe
    public void onInitUpdate(final String caseInit) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnAccept.setEnabled(false);
                pDialog = ProgressDialog.show(getActivity(), "", getResources().getString(R.string.sending), true);
                pDialog.setCancelable(true);
            }
        });
    }

    @Subscribe
    public void onFinishUpdate(final SimpleResponseEvent responseEvent) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pDialog.dismiss();
                btnAccept.setEnabled(true);

                if(responseEvent!=null) {
                    if(responseEvent.getSuccess()==1){
                        ChangeEmailDialogListener activity = (ChangeEmailDialogListener) getActivity();
                        activity.onFinishEmailDialog(new_email);
                        getDialog().cancel();
                    }
                    else{
                        switch (responseEvent.getCode()){
                            case 409:
                                etNewValue.setError(getString(R.string.email_exist));
                            break;
                            default:
                                etNewValue.setError(getString(R.string.some_error));
                            break;
                        }
                        etNewValue.requestFocus();
                    }
                }
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        context=activity;
        userPreferences = new UserPreferences(context);
        jobManager = new JobManager(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_change_name_email, container);
        getDialog().setTitle(R.string.prompt_email);

        currentValue = (TextView) view.findViewById(R.id.currentValue);
        currentValue.setText(userPreferences.getEmail());
        tvNew = (TextView) view.findViewById(R.id.tvNew);
        tvNew.setText(R.string.new_email);

        etNewValue = (EditText) view.findViewById(R.id.inputNewValue);

        btnAccept = (Button) view.findViewById(R.id.accept_button);
        btnCancel = (Button) view.findViewById(R.id.cancel_button);

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etNewValue.setError(null);
                new_email = etNewValue.getText().toString();

                if(new_email.length()<=max_long_char) {
                    if (UtilsFunctions.isEmailValid(new_email)) {
                        jobManager.addJobInBackground(new UpdateUser(Constants.email, userPreferences.getToken(), userPreferences.getIntId(), new_email));
                    }
                    else {
                        etNewValue.setError(getString(R.string.email_invalid));
                        etNewValue.requestFocus();
                    }
                }
                else{
                    etNewValue.setError(getString(R.string.long_max)+" "+max_long_char);
                    etNewValue.requestFocus();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().cancel();
            }
        });

        // Show soft keyboard automatically
        etNewValue.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        return view;
    }
}