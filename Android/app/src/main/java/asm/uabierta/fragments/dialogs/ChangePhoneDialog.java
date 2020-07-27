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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.path.android.jobqueue.JobManager;
import com.squareup.otto.Subscribe;

import java.util.Objects;

import asm.uabierta.R;
import asm.uabierta.events.SimpleResponseEvent;
import asm.uabierta.jobs.LoadData.LoadCountries;
import asm.uabierta.jobs.UpdateUser;
import asm.uabierta.models.Country;
import asm.uabierta.responses.CountriesResponse;
import asm.uabierta.utils.BusProvider;
import asm.uabierta.utils.Constants;
import asm.uabierta.utils.UserPreferences;
import asm.uabierta.utils.UtilsFunctions;

/**
 * Created by Alex on 04/08/2016.
 */

public class ChangePhoneDialog extends DialogFragment{

    public int max_long_char = 25;

    public interface ChangePhoneDialogListener {
        void onFinishPhoneDialog(String phone, Country prefix);
    }

    private Context context;
    private UserPreferences userPreferences;
    private JobManager jobManager;

    private TextView tvNew, currentValue;
    private EditText etNewValue;
    private Button btnAccept, btnCancel;
    private Spinner spinnerCountry;

    private String new_phone;
    private Country prefix = null;

    private ProgressDialog pDialog;

    public ChangePhoneDialog() {
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
    public void onFinisLoadCountries(final CountriesResponse countriesResponse) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (countriesResponse != null) {
                    if (countriesResponse.getData() != null) {
                        ArrayAdapter<Country> dataAdapter = new ArrayAdapter<Country>(getActivity(),
                                android.R.layout.simple_list_item_1, countriesResponse.getData());
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerCountry.setAdapter(dataAdapter);
                        dataAdapter.notifyDataSetChanged();

                        String item = userPreferences.getPrefix()+" "+userPreferences.getPrefixName();
                        for(int i=0; i<spinnerCountry.getAdapter().getCount(); i++) {
                            if(Objects.equals(item, spinnerCountry.getAdapter().getItem(i).toString())) {
                                spinnerCountry.setSelection(i);
                                break;
                            }
                        }

                        spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                                prefix = (Country) spinnerCountry.getSelectedItem();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> arg0) {
                            }
                        });
                    }
                }
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
                        ChangePhoneDialogListener activity = (ChangePhoneDialogListener) getActivity();
                        activity.onFinishPhoneDialog(new_phone, prefix);
                        getDialog().cancel();
                    }
                    else{
                        switch (responseEvent.getCode()){
                            case 409:
                                etNewValue.setError(getString(R.string.phone_exist));
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

        jobManager.addJobInBackground(new LoadCountries());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_change_phone, container);
        getDialog().setTitle(R.string.prompt_phone);

        spinnerCountry = (Spinner) view.findViewById(R.id.spinnerCountry);
        currentValue = (TextView) view.findViewById(R.id.currentValue);

        currentValue.setText(userPreferences.getPrefixName()+" "+userPreferences.getPrefix()+"  "+userPreferences.getPhone());

        tvNew = (TextView) view.findViewById(R.id.tvNew);
        tvNew.setText(R.string.new_phone);

        etNewValue = (EditText) view.findViewById(R.id.inputNewValue);
        btnAccept = (Button) view.findViewById(R.id.accept_button);
        btnCancel = (Button) view.findViewById(R.id.cancel_button);

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etNewValue.setError(null);
                new_phone = etNewValue.getText().toString();

                if (new_phone.length() <= max_long_char) {
                    if(UtilsFunctions.isPhoneValid(new_phone)) {
                        jobManager.addJobInBackground(new UpdateUser(Constants.phone, userPreferences.getToken(),
                                userPreferences.getIntId(), new_phone, prefix.getId()));
                    }
                    else{
                        etNewValue.setError(getString(R.string.phone_invalid));
                        etNewValue.requestFocus();
                    }
                } else {
                    etNewValue.setError(getString(R.string.long_max) + " " + max_long_char);
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