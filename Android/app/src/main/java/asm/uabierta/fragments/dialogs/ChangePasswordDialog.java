package asm.uabierta.fragments.dialogs;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.path.android.jobqueue.JobManager;
import com.squareup.otto.Subscribe;

import java.util.Objects;

import asm.uabierta.R;
import asm.uabierta.events.SimpleResponseEvent;
import asm.uabierta.jobs.UpdateUser;
import asm.uabierta.utils.BusProvider;
import asm.uabierta.utils.Constants;
import asm.uabierta.utils.MD5;
import asm.uabierta.utils.UserPreferences;

/**
 * Created by Alex on 04/08/2016.
 */

public class ChangePasswordDialog extends DialogFragment{

    public interface ChangePasswordDialogListener {
        void onFinishPasswordDialog(String currentPass, String newPass);
    }

    private Context context;
    private UserPreferences userPreferences;
    private JobManager jobManager;
    private EditText etCurrent, etNew, etRepeat;
    private Button btnAccept, btnCancel;

    private String currentPass, newPass, repeatPass;

    private ProgressDialog pDialog;

    public ChangePasswordDialog() {
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
                        ChangePasswordDialogListener activity = (ChangePasswordDialogListener) getActivity();
                        activity.onFinishPasswordDialog(currentPass, newPass);
                        getDialog().cancel();
                    }
                    else{
                        switch (responseEvent.getCode()){
                            case 403:
                                etCurrent.setError(getString(R.string.current_pass_invalid));
                                break;
                            default:
                                etCurrent.setError(getString(R.string.some_error));
                                break;
                        }
                        etCurrent.requestFocus();
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

        View view = inflater.inflate(R.layout.fragment_change_password, container);
        getDialog().setTitle(R.string.prompt_password);

        etCurrent = (EditText) view.findViewById(R.id.tvCurrent);
        etNew = (EditText) view.findViewById(R.id.tvNew);
        etRepeat = (EditText) view.findViewById(R.id.tvRepeat);

        btnAccept = (Button) view.findViewById(R.id.accept_button);
        btnCancel = (Button) view.findViewById(R.id.cancel_button);

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etNew.setError(null);
                etRepeat.setError(null);

                currentPass = etCurrent.getText().toString();
                newPass = etNew.getText().toString();
                repeatPass = etRepeat.getText().toString();

                if(!TextUtils.isEmpty(newPass)) {
                    try {
                        currentPass = MD5.createMd5(currentPass);
                        newPass = MD5.createMd5(newPass);
                        repeatPass = MD5.createMd5(repeatPass);
                    } catch (Exception ignored) {
                    }

                    if (Objects.equals(newPass, repeatPass)){
                        jobManager.addJobInBackground(new UpdateUser(Constants.password, userPreferences.getToken(), userPreferences.getIntId(),
                                currentPass, newPass));
                    } else {
                        etRepeat.setError(getString(R.string.error_password_not_match));
                        etRepeat.requestFocus();
                    }
                }
                else{
                    etNew.setError(getString(R.string.error_invalid_password));
                    etNew.requestFocus();
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
        etCurrent.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        return view;
    }
}