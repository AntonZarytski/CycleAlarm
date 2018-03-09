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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

import zaritsky.com.cyclealarm.R;
import zaritsky.com.cyclealarm.models.Cycle;
import zaritsky.com.cyclealarm.models.CycleList;
import zaritsky.com.cyclealarm.models.TypeOfDay;
import zaritsky.com.cyclealarm.models.TypesList;

import static android.provider.Telephony.Mms.Part.FILENAME;

public class CycleAdd extends Fragment {
    private Button saveCycle;
    private TextView nameOfCycle;
    private RecyclerView recyclerView;
    private TypesList typesList;
    private CycleAddAdapter adapter;
    private static TypeOfDay[] tempArr;
    private Cycle cycle;
    private CycleList cycleList = CycleList.getInstance(getContext());
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cycle_editor_recycler, container, false);
        cycle = new Cycle();
        recyclerView = view.findViewById(R.id.cycles_add_recycler_view);
        nameOfCycle = view.findViewById(R.id.name_of_cycle_text_view);
        saveCycle = view.findViewById(R.id.save_cycle_button);
        saveCycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cycle.setName(nameOfCycle.getText().toString());
                cycle.addAllToCycle(tempArr);
                cycleList.addToCycles(cycle);
                //TODO сохранение списка листа во внутреннюю память
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            ObjectOutputStream oos = new ObjectOutputStream(getActivity().openFileOutput(FILENAME, Context.MODE_PRIVATE));
//                            oos.writeObject(cycleList.getCycleList());
//                            oos.flush();
//                            oos.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }).start();
            }
        });
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 7);
        typesList = TypesList.getInstance(getContext());
        tempArr = new TypeOfDay[35];
        adapter = new CycleAddAdapter(typesList.getTypes(), getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        return view;
    }


    class CycleAddAdapter extends RecyclerView.Adapter<CycleAddViewHolder> {
        List<TypeOfDay> typesList;
        Context context;
        int numberOfType=0;

        public CycleAddAdapter(List<TypeOfDay> typesList, Context context) {
            this.typesList = typesList;
            this.context = context;
        }

        @Override
        public CycleAddViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.type_of_day_table_element, parent, false);
            return new CycleAddViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(CycleAddViewHolder holder, int position) {
            holder.wakeUpTime.setText("-");
            final TextView name = holder.nameOfType;
            final TextView wakeup = holder.wakeUpTime;
            final LinearLayout color = holder.linearLayout;
            final int tempPosition = position;
            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (typesList.size() > 0) {
                        name.setText(typesList.get(numberOfType).getName());
                        wakeup.setText(typesList.get(numberOfType).getTimeOfWakeUp());
                        color.setBackgroundColor(typesList.get(numberOfType).getColor());
                        //cycle.addToCycle(typesList.get(numberOfType), tempPosition);
                        tempArr[tempPosition]=typesList.get(numberOfType);
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

    class CycleAddViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout;
        TextView nameOfType;
        TextView wakeUpTime;

        CycleAddViewHolder(View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.type_of_day_background_table);
            nameOfType = itemView.findViewById(R.id.type_of_day_name_table);
            wakeUpTime = itemView.findViewById(R.id.type_of_day_wake_up_table);
        }
    }
}
