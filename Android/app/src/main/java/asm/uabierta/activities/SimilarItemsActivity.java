package asm.uabierta.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.path.android.jobqueue.JobManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import asm.uabierta.R;
import asm.uabierta.adapters.AdapterFounds;
import asm.uabierta.adapters.AdapterLosts;
import asm.uabierta.fragments.FoundsFragment;
import asm.uabierta.fragments.LostsFragment;
import asm.uabierta.jobs.LoadData.LoadFounds;
import asm.uabierta.jobs.LoadData.LoadSimilars;
import asm.uabierta.listeners.LoadFoundsListener;
import asm.uabierta.listeners.LoadLostsListener;
import asm.uabierta.models.Found;
import asm.uabierta.models.Lost;
import asm.uabierta.responses.FoundsResponse;
import asm.uabierta.responses.LostsResponse;
import asm.uabierta.utils.Constants;
import asm.uabierta.utils.UserPreferences;

public class SimilarItemsActivity extends AppCompatActivity implements LoadFoundsListener, LoadLostsListener {

    private UserPreferences uP;
    private ProgressDialog pDialog;
    private AdapterFounds adapterFounds;
    private ArrayList<Found> dataFounds = new ArrayList<Found>();
    private AdapterLosts adapterLosts;
    private ArrayList<Lost> dataLosts = new ArrayList<Lost>();
    private JobManager jobManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_similar);

        uP = new UserPreferences(getApplicationContext());
        jobManager = new JobManager(getApplicationContext());

        Intent intent = getIntent();
        int itemId = Integer.parseInt(intent.getStringExtra(Constants.id));
        int itemType = Integer.parseInt(intent.getStringExtra(Constants.type));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        jobManager = new JobManager(this);

        RecyclerView foundsRecyclerView = (RecyclerView) findViewById(R.id.rv_items);
        foundsRecyclerView.setHasFixedSize(true);
        foundsRecyclerView.setNestedScrollingEnabled(false);

        switch (itemType) {
            case 1:
                Log.e("TIPO ","LOST");
                jobManager.addJobInBackground(new LoadSimilars((LoadFoundsListener) SimilarItemsActivity.this, itemId, itemType));
                adapterFounds = new AdapterFounds(getApplicationContext(), dataFounds);
                foundsRecyclerView.setAdapter(adapterFounds);
            break;
            case 0:
                Log.e("TIPO ","FOUND");
                jobManager.addJobInBackground(new LoadSimilars((LoadLostsListener) SimilarItemsActivity.this, itemId, itemType));
                adapterLosts = new AdapterLosts(getApplicationContext(), dataLosts);
                foundsRecyclerView.setAdapter(adapterLosts);
            break;
            default:
                finish();
                break;
        }
        Log.e("ID ",itemId+"");

        //LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        //foundsRecyclerView.setLayoutManager(llm);
        //RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 12);
        RecyclerView.LayoutManager mLayoutManager = new StaggeredGridLayoutManager( Constants.columns,StaggeredGridLayoutManager.VERTICAL);
        foundsRecyclerView.setLayoutManager(mLayoutManager);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_similar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.index:
                Intent iIndex = new Intent(getApplicationContext(), IndexActivity.class);
                iIndex.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(iIndex);
                finish();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent iIndex = new Intent(getApplicationContext(), IndexActivity.class);
        iIndex.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(iIndex);
        finish();
    }

    @Override
    public void onInitLoad() {
        //pDialog = ProgressDialog.show(getApplicationContext(), "Cargando anuncios similares", getString(R.string.loading), true);
        //pDialog.setCancelable(true);
    }

    @Override
    public void onFinishLoad(final FoundsResponse foundsResponse) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if(foundsResponse!=null) {
                    int success = foundsResponse.isSuccess();
                    //if(success==0)
                    //    noVoices = true;
                    if(foundsResponse.getData()!=null) {
                        if(foundsResponse.getCount()==0) {
                            Intent iIndex = new Intent(getApplicationContext(), IndexActivity.class);
                            iIndex.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(iIndex);
                            finish();
                        }
                        //count = foundsResponse.getCount();
                        dataFounds.addAll(foundsResponse.getData());
                    }
                }
                adapterFounds.notifyDataSetChanged();

                //for(Found found : dataFounds) {
                    //    Log.e("FOUND ",found.getTitle()+" "+found.getUser().getPrefix().getName());
                    //}
                //pDialog.dismiss();
            }
        });
    }

    @Override
    public void onFinishLoad(final LostsResponse lostsResponse) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if(lostsResponse!=null) {
                    int success = lostsResponse.isSuccess();
                    //if(success==0)
                    //    noVoices = true;
                    if(lostsResponse.getData()!=null) {
                        if(lostsResponse.getCount()==0) {
                            Intent iIndex = new Intent(getApplicationContext(), IndexActivity.class);
                            iIndex.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(iIndex);
                            finish();
                        }
                        //count = lostsResponse.getCount();
                        dataLosts.addAll(lostsResponse.getData());
                    }
                }
                adapterLosts.notifyDataSetChanged();

                //for(Lost lost : dataLosts) {
                    //    Log.e("Lost ",lost.getTitle()+" "+lost.getUser().getPrefix().getName());
                    //}
                //pDialog.dismiss();
            }
        });
    }
}