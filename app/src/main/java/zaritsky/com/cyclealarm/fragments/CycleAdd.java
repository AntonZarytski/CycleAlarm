package zaritsky.com.cyclealarm.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

import zaritsky.com.cyclealarm.R;
import zaritsky.com.cyclealarm.models.AlarmList;
import zaritsky.com.cyclealarm.models.Cycle;
import zaritsky.com.cyclealarm.models.CycleList;
import zaritsky.com.cyclealarm.models.TypeOfDay;
import zaritsky.com.cyclealarm.models.TypesList;

import static android.provider.Telephony.Mms.Part.FILENAME;

public class CycleAdd extends Fragment {
    private final String CYCLEFILE = "CycleList";
    private final static String CURRENT_CYCLE_POSITION = "CURRENT_CYCLE_POSITION";
    private Cycle currentCycle;
    private Button saveCycle;
    private TextView nameOfCycle;
    private RecyclerView recyclerView;
    private TypesList typesList;
    private CycleAddAdapter adapter;
    private static TypeOfDay[] tempArr;
    private Cycle cycle;
    private CycleList cycleList = CycleList.getInstance(getContext());
    private LayoutInflater inflater;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cycle_editor_recycler, container, false);
        this.inflater = inflater;
        recyclerView = view.findViewById(R.id.cycles_add_recycler_view);
        nameOfCycle = view.findViewById(R.id.name_of_cycle_text_view);
        saveCycle = view.findViewById(R.id.save_cycle_button);
        saveCycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cycle = new Cycle(nameOfCycle.getText().toString());
                cycle.addAllToCycle(tempArr);
                cycleList.addToCycles(cycle);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ObjectOutputStream oos = new ObjectOutputStream(getActivity().openFileOutput(CYCLEFILE, Context.MODE_PRIVATE));
                            oos.writeObject(cycleList.getCycleList());
                            oos.flush();
                            oos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        nameOfCycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(nameOfCycle);
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

    public static CycleAdd newInstance(int position) {
        CycleAdd fragment = new CycleAdd();
        Bundle args = new Bundle();
        fragment.setCurrentCycle(CycleList.getOurInstance().getCycleList().get(position));
        args.putInt( CURRENT_CYCLE_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    public void setCurrentCycle(Cycle currentCycle) {
        this.currentCycle = currentCycle;
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
                        name.setBackgroundColor(typesList.get(numberOfType).getColor());
                        wakeup.setBackgroundColor(typesList.get(numberOfType).getColor());
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
    private void showDialog(final TextView textView) {
        View dialog = inflater.inflate(R.layout.edit_dialog, null);
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(getContext());
        mDialogBuilder.setView(dialog);
        final EditText userInput = dialog.findViewById(R.id.edit_text_dialog);
        mDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                textView.setText(userInput.getText());
                            }
                        })
                .setNegativeButton("Отмена",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = mDialogBuilder.create();
        alertDialog.show();
    }
}
