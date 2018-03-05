package zaritsky.com.cyclealarm.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import zaritsky.com.cyclealarm.R;
import zaritsky.com.cyclealarm.models.TypeOfDay;
import zaritsky.com.cyclealarm.models.TypesList;

public class CycleAdd extends Fragment {
    private RecyclerView recyclerView;
    private TypesList typesList;
    private CycleAddAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cycle_editor_fragment, container, false);
        recyclerView = view.findViewById(R.id.cycles_add_recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 7);
        typesList = TypesList.getInstance(getContext());
        adapter = new CycleAddAdapter(typesList.getTypes(), getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        return view;
    }

    static class CycleAddAdapter extends RecyclerView.Adapter<CycleAddViewHolder> {
        List<TypeOfDay> typesList;
        Context context;
        static int numberOfType=0;

        public CycleAddAdapter(List<TypeOfDay> typesList, Context context) {
            this.typesList = typesList;
            this.context = context;
        }

        @Override
        public CycleAddViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.type_of_day_element, parent, false);
            return new CycleAddViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(CycleAddViewHolder holder, int position) {
            holder.wakeUpTime.setText("-");
            final TextView name = holder.nameOfType;
            final TextView wakeup = holder.wakeUpTime;
            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (typesList.size() > 0) {
                        name.setText(typesList.get(numberOfType).getName());
                        wakeup.setText(typesList.get(numberOfType).getTimeOfWakeUp());
                        numberOfType++;
                        if (numberOfType == typesList.size()) {
                            numberOfType = 0;
                        }
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return 35;
        }
    }

    static class CycleAddViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout linearLayout;
        TextView nameOfType;
        TextView wakeUpTime;

        public CycleAddViewHolder(View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.type_of_day_background);
            nameOfType = itemView.findViewById(R.id.type_of_day_name);
            wakeUpTime = itemView.findViewById(R.id.type_of_day_wake_up);
        }
    }
}
