package asm.uabierta.fragments.dialogs;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.path.android.jobqueue.JobManager;
import com.squareup.otto.Subscribe;

import asm.uabierta.R;
import asm.uabierta.events.SimpleResponseEvent;
import asm.uabierta.jobs.ChangeItemBuilding;
import asm.uabierta.jobs.LoadData.LoadBuildings;
import asm.uabierta.models.Building;
import asm.uabierta.responses.BuildingsResponse;
import asm.uabierta.utils.BusProvider;
import asm.uabierta.utils.Constants;
import asm.uabierta.utils.UserPreferences;

/**
 * Created by Alex on 04/08/2016.
 */

public class ChangeBuildingDialog extends DialogFragment{

    public int max_long_char = 300;

    public interface ChangeBuildingDialogListener {
        void onFinishBuildingDialog(Building building, String details);
    }

    private Context context;
    private UserPreferences userPreferences;
    private JobManager jobManager;

    private Integer itemId;
    private EditText etPlaceDetails;
    private Button btnAccept, btnCancel;
    private Spinner spinnerBuilding;

    private String placeDetails;
    private Building building = null;

    private ProgressDialog pDialog;

    public ChangeBuildingDialog() {
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
    public void onFinisLoadBuildings(final BuildingsResponse buildingsResponse) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (buildingsResponse != null) {
                    if (buildingsResponse.getData() != null) {
                        ArrayAdapter<Building> dataAdapter = new ArrayAdapter<Building>(getActivity(),
                                android.R.layout.simple_list_item_1, buildingsResponse.getData());
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerBuilding.setAdapter(dataAdapter);
                        dataAdapter.notifyDataSetChanged();

                        spinnerBuilding.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                                building = (Building) spinnerBuilding.getSelectedItem();
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
                        ChangeBuildingDialogListener activity = (ChangeBuildingDialogListener) getActivity();
                        activity.onFinishBuildingDialog(building, placeDetails);
                        getDialog().cancel();
                    }
                    else{
                        switch (responseEvent.getCode()){
                            default:
                                etPlaceDetails.setError(getString(R.string.some_error));
                                break;
                        }
                        etPlaceDetails.requestFocus();
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

        jobManager.addJobInBackground(new LoadBuildings());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_change_building, container);
        getDialog().setTitle(R.string.leave_place);

        itemId = Integer.valueOf(getArguments().getString(Constants.id));

        spinnerBuilding = (Spinner) view.findViewById(R.id.spinnerBuilding);

        etPlaceDetails = (EditText) view.findViewById(R.id.inputHavePlaceDetails);
        btnAccept = (Button) view.findViewById(R.id.accept_button);
        btnCancel = (Button) view.findViewById(R.id.cancel_button);

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etPlaceDetails.setError(null);
                placeDetails = etPlaceDetails.getText().toString();

                try {
                    if (building.getId() != null) {
                        if (placeDetails.length() <= max_long_char) {
                            jobManager.addJobInBackground(new ChangeItemBuilding(userPreferences.getToken(),
                                    itemId, building.getId(), placeDetails));
                        } else {
                            etPlaceDetails.setError(getString(R.string.long_max) + " " + max_long_char);
                            etPlaceDetails.requestFocus();
                        }
                    } else {
                        Toast.makeText(getActivity(), R.string.select_building, Toast.LENGTH_LONG).show();
                    }
                }catch (NullPointerException e){
                    Toast.makeText(getActivity(), R.string.select_building, Toast.LENGTH_LONG).show();
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
        etPlaceDetails.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        return view;
    }
}