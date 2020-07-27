package asm.uabierta.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.path.android.jobqueue.JobManager;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import asm.uabierta.R;
import asm.uabierta.events.DeleteEvent;
import asm.uabierta.fragments.dialogs.ChangeBuildingDialog;
import asm.uabierta.jobs.DeleteItem;
import asm.uabierta.jobs.LoadData.LoadItem;
import asm.uabierta.listeners.LoadItemFoundListener;
import asm.uabierta.models.Building;
import asm.uabierta.models.Found;
import asm.uabierta.responses.FoundItemResponse;
import asm.uabierta.utils.BusProvider;
import asm.uabierta.utils.Constants;
import asm.uabierta.utils.ServerConnection;
import asm.uabierta.utils.UserPreferences;
import asm.uabierta.utils.UtilsFunctions;

public class ItemFoundActivity extends AppCompatActivity implements LoadItemFoundListener,
        ChangeBuildingDialog.ChangeBuildingDialogListener {

    private UserPreferences uP;
    private JobManager jobManager;
    private Found found;
    private int itemUserId, itemHaveIt;

    private ProgressDialog pDialog;

    private LinearLayout layContact, layButtons, layUniversity;
    private ImageButton btnImage;
    private TextView tvTitle, tvDescription, tvFoundDate, tvPlace, tvPlaceDetails, tvContact, tvHolderPlace, tvHolderDetails;
    private Button btnPhone, btnEmail;
    private FloatingActionButton btnFb, btnLeave;

    private CallbackManager callbackManager;
    private ShareDialog shareDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.found_view);

        uP = new UserPreferences(getApplicationContext());
        jobManager = new JobManager(getApplicationContext());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        int itemId = Integer.parseInt(intent.getStringExtra(Constants.id));
        String title = intent.getStringExtra(Constants.title);
        itemUserId = Integer.parseInt(intent.getStringExtra(Constants.userId));
        itemHaveIt = Integer.parseInt(intent.getStringExtra(Constants.haveIt));
        jobManager.addJobInBackground(new LoadItem(ItemFoundActivity.this, itemId));

        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        layContact = (LinearLayout) findViewById(R.id.layContact);
        layButtons = (LinearLayout) findViewById(R.id.layoutButtons);
        layUniversity = (LinearLayout) findViewById(R.id.layUniversity);

        btnImage = (ImageButton) findViewById(R.id.btnImage);
        tvTitle = (TextView) findViewById(R.id.itemTitle);
        tvDescription = (TextView) findViewById(R.id.itemDescription);
        tvFoundDate = (TextView) findViewById(R.id.tvFoundDate);
        tvPlace = (TextView) findViewById(R.id.tvPlace);
        tvPlaceDetails = (TextView) findViewById(R.id.tvPlaceDetails);
        tvContact = (TextView) findViewById(R.id.tvContact);
        tvHolderPlace = (TextView) findViewById(R.id.tvHolderPlace);
        tvHolderDetails = (TextView) findViewById(R.id.tvHolderDetails);

        btnEmail = (Button) findViewById(R.id.btnEmail);
        btnPhone = (Button) findViewById(R.id.btnPhone);
        btnFb = (FloatingActionButton) findViewById(R.id.btnFb);
        btnLeave = (FloatingActionButton) findViewById(R.id.btnLeave);

        FacebookSdk.sdkInitialize(getApplicationContext());
        shareDialog = new ShareDialog(ItemFoundActivity.this);
        callbackManager = CallbackManager.Factory.create();
        shareDialog.registerCallback(callbackManager, new
                FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {}

                    @Override
                    public void onCancel() {}

                    @Override
                    public void onError(FacebookException error) {}
                });
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        super.onPrepareOptionsMenu(menu);
        //menu.setGroupVisible(R.id.delete, showDelete);
        //menu.findItem(R.id.delete).setEnabled(showDelete);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(!uP.isLogged())
            getMenuInflater().inflate(R.menu.menu_index, menu);
        else if(uP.getIntId()==itemUserId && itemHaveIt==1)
            getMenuInflater().inflate(R.menu.menu_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.login:
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.delete:
                AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
                alertbox.setIcon(R.drawable.com_facebook_close);
                alertbox.setTitle(R.string.delete);
                alertbox.setMessage(R.string.delete_item);

                alertbox.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        jobManager.addJobInBackground(new DeleteItem(uP.getToken(), found.getId(), 0));
                    }
                });

                alertbox.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });
                alertbox.show();
            break;
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onFinishBuildingDialog(Building b, String haveDetails) {
        Toast.makeText(getApplicationContext(), R.string.object_left, Toast.LENGTH_LONG).show();
        if(b!=null){
            layUniversity.setVisibility(View.VISIBLE);
            btnLeave.setVisibility(View.GONE);
            tvHolderPlace.setText(b.getName());
            tvHolderDetails.setText(haveDetails);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void sendEmail(View view) {
        UtilsFunctions.sendMail(getApplicationContext(), found.getUser().getEmail(), found.getTitle());
    }

    public void callPhone(View view) {
        try {
            UtilsFunctions.callPhone(getApplicationContext(), found.getUser().getPrefix().getPrefix(), found.getUser().getPhone());
        }catch (NullPointerException e){
            UtilsFunctions.callPhone(getApplicationContext(), null, found.getUser().getPhone());
        }
    }

    //JOB EVENTS
    @Subscribe
    public void onSimpleEventAdded(final String caseReport) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
            switch (caseReport){
                case "delete":
                    pDialog = ProgressDialog.show(ItemFoundActivity.this, "", getString(R.string.deleting), true);
                    break;
                case "recover":
                    pDialog = ProgressDialog.show(ItemFoundActivity.this, "", getString(R.string.recovering), true);
                    break;
            }
            }
        });
    }

    @Subscribe
    public void onFinishDelete(final DeleteEvent event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (event.getSuccess() != null && event.getSuccess()==1) {
                    try { pDialog.dismiss(); }catch (Exception e){}
                    finish();
                }
                else{
                    Snackbar.make(tvTitle, getString(R.string.some_error), Snackbar.LENGTH_LONG).show();
                }
                pDialog.dismiss();
            }
        });
    }

    @Override
    public void onInitGetFound() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pDialog = ProgressDialog.show(ItemFoundActivity.this, "", getString(R.string.loading), true);
            }
        });
    }

    @Override
    public void onFinishGetFound(final FoundItemResponse foundResponse) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (foundResponse != null) {
                    if (foundResponse.getData() != null) {
                        found = foundResponse.getData();

                        if(found.getHaveIt()==0){
                            layUniversity.setVisibility(View.VISIBLE);
                            //btnLeave.setVisibility(View.GONE);
                            //layContact.setVisibility(View.GONE);
                            //layButtons.setVisibility(View.GONE);
                        }
                        else{
                            if(found.getUser().getId()==uP.getIntId()) {
                                btnLeave.setVisibility(View.VISIBLE);
                                //layUniversity.setVisibility(View.GONE);
                                //layContact.setVisibility(View.GONE);
                                //layButtons.setVisibility(View.VISIBLE);
                            }
                            else{
                                //layUniversity.setVisibility(View.GONE);
                                layContact.setVisibility(View.VISIBLE);
                                layButtons.setVisibility(View.VISIBLE);
                            }
                        }

                        tvTitle.setText(found.getTitle());
                        tvDescription.setText(found.getDescription());

                        tvFoundDate.setText(getString(R.string.the)+"  "+found.getFoundDate());
                        if(found.getPlace()!=null){
                            tvPlace.setText(found.getPlace().getName());
                            tvPlaceDetails.setText(found.getPlace().getPlaceDetails());
                        }
                        tvContact.setText(found.getUser().getName());

                        if(found.getHolder()!=null){
                            tvHolderPlace.setText(found.getHolder().getName());
                            tvHolderDetails.setText(found.getHolder().getHavePlaceDetails());
                        }

                        if(found.getHasPhoto()==1){
                            Picasso.with(getApplicationContext())
                                    .load(found.getImage())
                                    .resize(200, 200)
                                    //.fit()
                                    .centerCrop()
                                    .placeholder(R.drawable.no_image)
                                    .into(btnImage);
                            btnImage.setOnClickListener(new ImageButton.OnClickListener() {
                                public void onClick(View v) {
                                    if(found!=null) {
                                        Intent intent = new Intent(v.getContext(), ImageViewActivity.class);
                                        intent.putExtra(Constants.image, found.getImage());
                                        intent.putExtra(Constants.title, found.getTitle());
                                        startActivity(intent);
                                    }
                                }
                            });
                        }
                        else{
                            Picasso.with(getApplicationContext())
                                    .load(R.drawable.no_image)
                                    .resize(200, 200)
                                    //.fit()
                                    .centerCrop()
                                    .into(btnImage);
                        }

                        btnLeave.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Bundle bundle = new Bundle();
                                bundle.putString(Constants.id, String.valueOf(found.getId()));
                                ChangeBuildingDialog changeDialog = new ChangeBuildingDialog();
                                changeDialog.setArguments(bundle);
                                changeDialog.show(getSupportFragmentManager(), null);
                            }
                        });

                        btnFb.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (ShareDialog.canShow(ShareLinkContent.class)) {
                                    ShareLinkContent linkContent = null;
                                    if(found.getHasPhoto()==1){
                                        linkContent = new ShareLinkContent.Builder()
                                                .setContentTitle("Objeto encontrado")
                                                .setContentDescription(found.getTitle())
                                                .setImageUrl(Uri.parse(found.getImage()))
                                                .setContentUrl(Uri.parse(ServerConnection.urlDashboardFound+found.getId()))
                                                .build();
                                    }
                                    else{
                                        linkContent = new ShareLinkContent.Builder()
                                                .setContentTitle("Objeto encontrado")
                                                .setContentDescription(found.getTitle())
                                                .setContentUrl(Uri.parse(ServerConnection.urlDashboardFound+found.getId()))
                                                .build();
                                    }
                                    shareDialog.show(linkContent);
                                }
                                else{
                                    Snackbar.make(tvTitle, "Error con Facebook", Snackbar.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }
                pDialog.dismiss();
            }
        });
    }
}