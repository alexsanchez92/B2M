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

import java.util.Objects;

import asm.uabierta.R;
import asm.uabierta.events.DeleteEvent;
import asm.uabierta.events.RecoverEvent;
import asm.uabierta.jobs.DeleteItem;
import asm.uabierta.jobs.LoadData.LoadItem;
import asm.uabierta.jobs.RecoverItem;
import asm.uabierta.listeners.LoadItemLostListener;
import asm.uabierta.models.Lost;
import asm.uabierta.responses.LostItemResponse;
import asm.uabierta.utils.BusProvider;
import asm.uabierta.utils.Constants;
import asm.uabierta.utils.ServerConnection;
import asm.uabierta.utils.UserPreferences;
import asm.uabierta.utils.UtilsFunctions;

public class ItemLostActivity extends AppCompatActivity implements LoadItemLostListener {

    private UserPreferences uP;
    private JobManager jobManager;
    private Lost lost;
    private int itemUserId;

    private ProgressDialog pDialog;

    private ImageButton btnImage;
    private FloatingActionButton btnFb;
    private Button btnBack;
    private TextView tvTitle, tvDescription, tvStartDate, tvEndDate, tvPlace, tvPlaceDetails, tvContact;
    private LinearLayout layContact, layButtons, layUser;

    private CallbackManager callbackManager;
    private ShareDialog shareDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lost_view);

        uP = new UserPreferences(getApplicationContext());
        jobManager = new JobManager(getApplicationContext());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        int itemId = Integer.parseInt(intent.getStringExtra(Constants.id));
        String title = intent.getStringExtra(Constants.title);
        itemUserId = Integer.parseInt(intent.getStringExtra(Constants.userId));

        jobManager.addJobInBackground(new LoadItem(ItemLostActivity.this, itemId));

        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        layContact = (LinearLayout) findViewById(R.id.layContact);
        layButtons = (LinearLayout) findViewById(R.id.layoutButtons);
        layUser = (LinearLayout) findViewById(R.id.layUser);
        btnImage = (ImageButton) findViewById(R.id.btnImage);

        tvTitle = (TextView) findViewById(R.id.itemTitle);
        tvDescription = (TextView) findViewById(R.id.itemDescription);
        tvStartDate = (TextView) findViewById(R.id.tvStartDate);
        tvEndDate = (TextView) findViewById(R.id.tvEndDate);
        tvPlace = (TextView) findViewById(R.id.tvPlace);
        tvPlaceDetails = (TextView) findViewById(R.id.tvPlaceDetails);
        tvContact = (TextView) findViewById(R.id.tvContact);

        btnBack = (Button) findViewById(R.id.btnBack);
        btnFb = (FloatingActionButton) findViewById(R.id.btnFb);

        FacebookSdk.sdkInitialize(getApplicationContext());
        shareDialog = new ShareDialog(ItemLostActivity.this);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        if(!uP.isLogged())
            getMenuInflater().inflate(R.menu.menu_index, menu);
        else if(uP.getIntId()==itemUserId)
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
                        jobManager.addJobInBackground(new DeleteItem(uP.getToken(), lost.getId(), 1));
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
    public void onInitGetLost() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
            }
        });
    }

    @Override
    public void onFinishGetLost(final LostItemResponse lostResponse) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (lostResponse != null) {
                    if (lostResponse.getData() != null) {
                        lost = lostResponse.getData();

                        if(itemUserId==uP.getIntId()){
                            layUser.setVisibility(View.VISIBLE);
                            if(Objects.equals(lost.getStatus(), "recovered")){
                                btnFb.setVisibility(View.GONE);
                                btnBack.setBackgroundColor(getResources().getColor(R.color.successColor));
                                btnBack.setEnabled(false);
                            }
                        }
                        else{
                            layContact.setVisibility(View.VISIBLE);
                            tvContact.setText(lost.getUser().getName());
                            layButtons.setVisibility(View.VISIBLE);
                        }

                        tvTitle.setText(lost.getTitle());
                        tvDescription.setText(lost.getDescription());

                        tvStartDate.setText(getString(R.string.between)+"  "+lost.getStartDate());
                        tvEndDate.setText("  "+getString(R.string.between_and)+"  "+lost.getEndDate());
                        tvPlace.setText(lost.getPlace().getName());
                        tvPlaceDetails.setText(lost.getPlace().getPlaceDetails());

                        if(lost.getHasPhoto()==1){
                            Picasso.with(getApplicationContext())
                                .load(lost.getImage())
                                .resize(200, 200)
                                //.fit()
                                .centerCrop()
                                .placeholder(R.drawable.no_image)
                                .into(btnImage);

                            btnImage.setOnClickListener(new ImageButton.OnClickListener() {
                                public void onClick(View v) {
                                    if(lost!=null) {
                                        Intent intent = new Intent(v.getContext(), ImageViewActivity.class);
                                        intent.putExtra(Constants.image, lost.getImage());
                                        intent.putExtra(Constants.title, lost.getTitle());
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
                        btnFb.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (ShareDialog.canShow(ShareLinkContent.class)) {
                                    ShareLinkContent linkContent = null;
                                    if(lost.getHasPhoto()==1){
                                        linkContent = new ShareLinkContent.Builder()
                                                .setContentTitle("Objeto perdido")
                                                .setContentDescription(lost.getTitle())
                                                .setImageUrl(Uri.parse(lost.getImage()))
                                                .setContentUrl(Uri.parse(ServerConnection.urlDashboardLost+lost.getId()))
                                                .build();
                                    }
                                    else{
                                        linkContent = new ShareLinkContent.Builder()
                                                .setContentTitle("Objeto perdido")
                                                .setContentDescription(lost.getTitle())
                                                .setContentUrl(Uri.parse(ServerConnection.urlDashboardLost+lost.getId()))
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
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void recoverItem(View view) {
        AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
        alertbox.setIcon(R.drawable.ic_back_black);
        alertbox.setTitle(R.string.recover);
        alertbox.setMessage(R.string.recover_text);

        alertbox.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                jobManager.addJobInBackground(new RecoverItem(uP.getToken(), lost.getId(), 1));
            }
        });

        alertbox.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
        alertbox.show();
        //jobManager
    }

    public void sendEmail(View view) {
        UtilsFunctions.sendMail(getApplicationContext(), lost.getUser().getEmail(), lost.getTitle());
    }

    public void callPhone(View view) {
        try {
            UtilsFunctions.callPhone(getApplicationContext(), lost.getUser().getPrefix().getPrefix(), lost.getUser().getPhone());
        }catch (NullPointerException e){
            UtilsFunctions.callPhone(getApplicationContext(), null, lost.getUser().getPhone());
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
                        pDialog = ProgressDialog.show(ItemLostActivity.this, "", getString(R.string.deleting), true);
                    break;
                    case "recover":
                        pDialog = ProgressDialog.show(ItemLostActivity.this, "", getString(R.string.recovering), true);
                    break;
                }
            }
        });
    }

    @Subscribe
    public void onRecoverFinish(final RecoverEvent event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try { pDialog.dismiss(); }catch (Exception e){}
                if (event.getSuccess() != null && event.getSuccess()==1) {
                    btnBack.setBackgroundColor(getResources().getColor(R.color.successColor));
                    btnBack.setEnabled(false);
                    btnFb.setVisibility(View.GONE);
                }
                else{
                    Snackbar.make(tvTitle, getString(R.string.some_error), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    @Subscribe
    public void onDeleteFinish(final DeleteEvent event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try { pDialog.dismiss(); }catch (Exception e){}
                if (event.getSuccess() != null && event.getSuccess()==1) {
                    try { pDialog.dismiss(); }catch (Exception e){}
                    finish();
                }
                else{
                    Snackbar.make(tvTitle, getString(R.string.some_error), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }
}