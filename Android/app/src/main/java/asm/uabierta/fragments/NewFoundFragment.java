package asm.uabierta.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.path.android.jobqueue.JobManager;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.io.FileOutputStream;

import asm.uabierta.R;
import asm.uabierta.activities.SimilarItemsActivity;
import asm.uabierta.events.CreateFoundEvent;
import asm.uabierta.events.SimpleResponseEvent;
import asm.uabierta.jobs.CreateItem;
import asm.uabierta.jobs.LoadData.LoadBuildings;
import asm.uabierta.models.Building;
import asm.uabierta.responses.BuildingsResponse;
import asm.uabierta.utils.BusProvider;
import asm.uabierta.utils.Constants;
import asm.uabierta.utils.UserPreferences;
import asm.uabierta.utils.UtilsFunctions;

public class NewFoundFragment extends Fragment{

    private static int RESULT_LOAD_IMAGE = 2;
    private static final int CAMERA_REQUEST = 1;

    private String photoName;
    private Bitmap photoFile = null;
    private String photoUri = null;

    private UserPreferences uP;
    private View focusView;
    public ProgressDialog pDialog;
    private JobManager jobManager;
    private RadioGroup radioHaveIt;
    private LinearLayout lyHaveIt;
    private Spinner spinnerPlace, spinnerHaveIt;

    private EditText tvTitle, tvDescription, tvProperty, tvPlaceDetails, tvHaveDetails;
    private ImageButton btnImage;
    private DatePicker dtFoundDate;

    private String title, description, foundDate, property, placeDetails, placeHaveDetails;
    private Integer haveIt = 1;
    private Integer placeId = null;
    private Integer placeHaveId = null;

    public NewFoundFragment() {
        // Required empty public constructor
    }

    public void createFound(){
        tvTitle.setError(null);
        focusView = null;

        title = tvTitle.getText().toString();
        description = tvDescription.getText().toString();
        property = tvProperty.getText().toString();
        placeDetails = tvPlaceDetails.getText().toString();
        placeHaveDetails = tvHaveDetails.getText().toString();
        int day = dtFoundDate.getDayOfMonth(); int month = dtFoundDate.getMonth()+1; int year = dtFoundDate.getYear();
        foundDate = day+"-"+month+"-"+year;

        if(TextUtils.isEmpty(title)){
            tvTitle.setError(getString(R.string.is_required));
            focusView = tvTitle;
            return;
        }

        switch (haveIt){
            case 1:
                jobManager.addJobInBackground(new CreateItem(uP.getToken(), photoUri, title, description,
                        placeId, placeDetails, foundDate, property));
                break;
            case 0:
                jobManager.addJobInBackground(new CreateItem(uP.getToken(), photoUri, title, description,
                        placeId, placeDetails, foundDate, property, placeHaveId, placeHaveDetails));
                break;
            default:
                break;
        }
        //Log.e("CREANDO ", title+" "+description+" "+placeDetails+" "+placeId+" "+placeDetails+" "+foundDate
        //+" "+haveIt+" "+placeHaveId+" "+placeHaveDetails);
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
    public void onInitCreate(final String caseReport) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
            switch (caseReport){
                case "found":
                    pDialog = ProgressDialog.show(getActivity(), "", "Creando", true);
                    break;
            }
            }
        });
    }

    @Subscribe
    public void onFinishCreate(final CreateFoundEvent createResponse) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try { pDialog.dismiss(); }catch (Exception e){ Log.e("Create", "Exception dialog"); }
                if(createResponse!=null) {
                    if(createResponse.getId()!=null) {
                        Log.e("CREATE FOUND ID",createResponse.getId()+"");
                        Intent intentSimilar = new Intent(getActivity(), SimilarItemsActivity.class);
                        intentSimilar.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intentSimilar.putExtra(Constants.id, Integer.toString(createResponse.getId()));
                        intentSimilar.putExtra(Constants.type, Integer.toString(0));
                        startActivity(intentSimilar);
                        getActivity().finish();
                    }
                    else{
                        Snackbar.make(tvTitle, "Error creando", Snackbar.LENGTH_LONG).show();
                    }
                }
                else{
                    Snackbar.make(tvTitle, "Error creando", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    @Subscribe
    public void onFinishCreate(final SimpleResponseEvent createResponse) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try { pDialog.dismiss(); }catch (Exception e){ Log.e("Create", "Exception dialog"); }
            }
        });
    }

    @Subscribe
    public void onFinisLoadBuilding(final BuildingsResponse buildingsResponse) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(buildingsResponse!=null) {
                    if(buildingsResponse.getData()!=null) {

                        ArrayAdapter<Building> dataAdapter = new ArrayAdapter<Building>(getActivity(),
                                android.R.layout.simple_list_item_1, buildingsResponse.getData());
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerPlace.setAdapter(dataAdapter);
                        spinnerHaveIt.setAdapter(dataAdapter);
                        dataAdapter.notifyDataSetChanged();

                        spinnerPlace.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                                Building b = (Building) spinnerPlace.getSelectedItem();
                                placeId = b.getId();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> arg0) {
                            }
                        });

                        spinnerHaveIt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                                Building b = (Building) spinnerHaveIt.getSelectedItem();
                                placeHaveId = b.getId();
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_new_found, container, false);
        uP = new UserPreferences(getActivity());
        jobManager = new JobManager(getActivity());

        tvTitle = (EditText) rootView.findViewById(R.id.title);
        tvDescription = (EditText) rootView.findViewById(R.id.description);
        tvProperty = (EditText) rootView.findViewById(R.id.property);
        tvPlaceDetails = (EditText) rootView.findViewById(R.id.place_details);
        tvHaveDetails = (EditText) rootView.findViewById(R.id.have_details);
        dtFoundDate = (DatePicker) rootView.findViewById(R.id.dtFoundDate);

        spinnerPlace = (Spinner) rootView.findViewById(R.id.spinnerPlace);
        spinnerHaveIt = (Spinner) rootView.findViewById(R.id.spinnerHaveIt);
        jobManager.addJobInBackground(new LoadBuildings());

        lyHaveIt = (LinearLayout) rootView.findViewById(R.id.lyHaveIt);
        radioHaveIt = (RadioGroup) rootView.findViewById(R.id.radioHaveIt);
        radioHaveIt.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.noHaveIt:
                        haveIt = 1;
                        lyHaveIt.setVisibility(View.GONE);
                        break;
                    case R.id.haveIt:
                        haveIt = 0;
                        lyHaveIt.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        btnImage = (ImageButton) rootView.findViewById(R.id.btnImage);
        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Cargar imagen");
                alert.setMessage("Desea hacer una foto con la camara o cargar una existente de la galería");
                alert.setPositiveButton("GALERÍA", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
                    }
                });
                alert.setNegativeButton("CÁMARA", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        File imagesFolder = new File(UtilsFunctions.getImagesFolder());
                        Time today = new Time(Time.getCurrentTimezone()); today.setToNow();

                        photoName = uP.getId() + "_" +String.valueOf(today.format("%Y%m%d_%H%M%S")+".jpg");
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(imagesFolder, photoName)));
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    }

                }); //End of alert.setNegativeButton
                AlertDialog alertDialog = alert.create();
                alertDialog.show();
            }
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK  ) {
            File path = new File(UtilsFunctions.getImagesFolder());
            photoUri = path+"/"+photoName;
            photoFile = UtilsFunctions.resizePhoto(photoUri);

            try {
                FileOutputStream fOut = new FileOutputStream(photoUri);
                photoFile.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                fOut.flush();
                fOut.close();
            } catch (Exception ignored) {
            }

            if (photoFile !=null) {
                photoFile = UtilsFunctions.scaleDownBitmapWithHeight(photoFile,90, getContext());
                btnImage.setBackground(null);
                btnImage.setImageBitmap(photoFile);
            }
        }else
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            photoUri = cursor.getString(columnIndex);
            cursor.close();
            photoFile = UtilsFunctions.resizePhoto(photoUri);
            File file = new File(photoUri);

            try {
                FileOutputStream fOut = new FileOutputStream(file);
                photoFile.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                fOut.flush();
                fOut.close();
            } catch (Exception ignored) {
            }

            if (photoFile !=null) {
                photoFile = UtilsFunctions.scaleDownBitmapWithHeight(photoFile,90, getContext());
                btnImage.setBackground(null);
                btnImage.setImageBitmap(photoFile);
            }
        }
    }
}