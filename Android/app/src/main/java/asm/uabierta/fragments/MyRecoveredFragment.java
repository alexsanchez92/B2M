package asm.uabierta.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.path.android.jobqueue.JobManager;

import java.util.ArrayList;

import asm.uabierta.R;
import asm.uabierta.adapters.AdapterLosts;
import asm.uabierta.jobs.LoadData.LoadMyRecovered;
import asm.uabierta.listeners.LoadLostsListener;
import asm.uabierta.models.Lost;
import asm.uabierta.responses.LostsResponse;
import asm.uabierta.utils.Constants;
import asm.uabierta.utils.UserPreferences;

public class MyRecoveredFragment extends Fragment implements LoadLostsListener {

    public ProgressDialog pDialog;
    private AdapterLosts adapterLosts;
    private ArrayList<Lost> dataLosts = new ArrayList<Lost>();
    private JobManager jobManager;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Button btnLoadMore;
    private int page;
    private Integer count = 0;
    private UserPreferences uP;

    public MyRecoveredFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = 1;
        uP = new UserPreferences(getActivity());
        jobManager = new JobManager(getActivity());
        jobManager.addJobInBackground(new LoadMyRecovered(MyRecoveredFragment.this, Integer.parseInt(uP.getId()),  Constants.limit, page));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_recycler, container, false);

        RecyclerView foundsRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_items);
        foundsRecyclerView.setHasFixedSize(true);
        foundsRecyclerView.setNestedScrollingEnabled(false);

        adapterLosts = new AdapterLosts(getContext(), dataLosts);
        foundsRecyclerView.setAdapter(adapterLosts);

        //LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        //foundsRecyclerView.setLayoutManager(llm);
        //RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 12);
        RecyclerView.LayoutManager mLayoutManager = new StaggeredGridLayoutManager( Constants.columns,StaggeredGridLayoutManager.VERTICAL);
        foundsRecyclerView.setLayoutManager(mLayoutManager);

        mSwipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.swipeRefreshLayout);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
                //page++;
                //btnLoadMore.setEnabled(false);
                //jobManager.addJobInBackground(new LoadLosts(LostsFragment.this, Constants.limit, page));
            }
        });

        btnLoadMore = (Button)rootView.findViewById(R.id.btn_load_more);
        btnLoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (!noUsers && cd.isConnected(FoundsFragment.this)) {
                //posTop += adapter.getCount();
                //page += Constants.queryLimit;
                page++;
                btnLoadMore.setEnabled(false);
                jobManager.addJobInBackground(new LoadMyRecovered(MyRecoveredFragment.this, Integer.parseInt(uP.getId()), Constants.limit, page));
                //}
            }
        });

        return rootView;
    }

    @Override
    public void onInitLoad() {
        //pDialog = ProgressDialog.show(getActivity(), "", "Cargando", true);
    }

    @Override
    public void onFinishLoad(final LostsResponse lostsResponse) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(lostsResponse!=null) {
                    int success = lostsResponse.isSuccess();
                    //if(success==0)
                    //    noVoices = true;
                    if(lostsResponse.getData()!=null) {
                        count = lostsResponse.getCount();
                        dataLosts.addAll(lostsResponse.getData());
                    }
                }
                adapterLosts.notifyDataSetChanged();

                if(adapterLosts.getItemCount()>=count && count>0){
                    //mSwipeRefreshLayout.setEnabled(false);
                    btnLoadMore.setVisibility(View.GONE);
                }else{
                    btnLoadMore.setVisibility(View.VISIBLE);
                    if(count==0){
                        btnLoadMore.setEnabled(false);
                        btnLoadMore.setText(getString(R.string.dont_have_recovered));
                    }
                    else {
                        btnLoadMore.setEnabled(true);
                    }
                }

                /*for(Lost lost : dataLosts) {
                    Log.e("LOST ",lost.getTitle()+" "+lost.getUser().getPrefix().getName());
                }*/
                //pDialog.dismiss();
            }
        });
    }
}