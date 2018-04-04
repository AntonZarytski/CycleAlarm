package zaritsky.com.cyclealarm.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import zaritsky.com.cyclealarm.R;
import zaritsky.com.cyclealarm.interfaces.AbleToChangeFragment;
import zaritsky.com.cyclealarm.models.Cycle;
import zaritsky.com.cyclealarm.models.CycleList;
/**
 * RecyclerView for all cycles of user. The source of data - is the List<Cycle> that is loaded from
 * the model-class singleton CycleList
 */
public class CycleRecyclerList extends Fragment{
        private AbleToChangeFragment callBackAvtivity;
        private RecyclerView recyclerView;
        private CycleAdapter adapter;
        private CycleList cycleList;

    /**
     * Interface callBackAvtivity for call the method for change the fragment for the selected position
     */
        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            callBackAvtivity = (AbleToChangeFragment) context;
        }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cycles_recycler, container, false);
        recyclerView = view.findViewById(R.id.cycles_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        FloatingActionButton fab = view.findViewById(R.id.floating_button_add_cycle);
        /**
         * create new cycle
         */
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CycleAdd cycleAdd = new CycleAdd();
                callBackAvtivity.changeFragments(R.id.content_main, cycleAdd);
                Toast toast = Toast.makeText(view.getContext(), "Плавающая кнопка", Toast.LENGTH_LONG);
                toast.show();
            }
        });

        return view;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cycleList =  CycleList.getInstance(getContext());
        adapter = new CycleAdapter(cycleList.getCycleList(), getContext());
    }

    @Override
    public void onPause() {
        super.onPause();
        cycleList =  CycleList.getInstance(getContext());
        adapter = new CycleAdapter(cycleList.getCycleList(), getContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        cycleList =  CycleList.getInstance(getContext());
        adapter = new CycleAdapter(cycleList.getCycleList(), getContext());
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
    }

    private class CycleAdapter extends RecyclerView.Adapter<CycleViewHolder>{
            List<Cycle> cycleList;
            Context contex;

        CycleAdapter(List<Cycle> cycleList, Context context){
            this.cycleList = cycleList;
            this.contex = context;
        }

        @Override
        public CycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cycle_element, parent, false);
            return new CycleViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(CycleViewHolder holder, final int position) {
            holder.cycleName.setText(cycleList.get(position).getName());
            holder.cycleView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /**
                     * call fragment for the selected position
                     * */
                    CycleAdd cycleAdd = CycleAdd.newInstance(position);
                    callBackAvtivity.changeFragments(R.id.content_main, cycleAdd);
                }
            });
        }

        @Override
        public int getItemCount() {
            if (cycleList!= null) {
                return cycleList.size();
            } else return 0;
        }
    }
    static class CycleViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout cycleView;
        TextView cycleName;
        public CycleViewHolder(View itemView) {
            super(itemView);
            cycleView = itemView.findViewById(R.id.cycle_recycler_view);
            cycleName = itemView.findViewById(R.id.name_of_cycle_text_view);
        }
    }

}
