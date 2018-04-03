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
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import zaritsky.com.cyclealarm.R;
import zaritsky.com.cyclealarm.models.Cycle;
import zaritsky.com.cyclealarm.models.CycleList;
import zaritsky.com.cyclealarm.models.TypeOfDay;
import zaritsky.com.cyclealarm.models.TypesList;

public class CycleAdd extends Fragment {
    private final String CYCLEFILE = "CycleList";
    private final static String CURRENT_CYCLE_POSITION = "CURRENT_CYCLE_POSITION";
    private Button saveCycle;
    private TextView nameOfCycle;
    private RecyclerView recyclerView;
    private TypesList typesList;
    private CycleAddAdapter adapter;
    private static TypeOfDay[] tempArr;
    private Cycle currentCycle;
    private CycleList cycleList;
    private LayoutInflater inflater;
    private int currentCyclePosition;
    private GridLayoutManager layoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cycle_editor_recycler, container, false);
        this.inflater = inflater;
        cycleList = CycleList.getInstance(getContext());
        recyclerView = view.findViewById(R.id.cycles_add_recycler_view);
        nameOfCycle = view.findViewById(R.id.name_of_cycle_text_view);
        if (currentCycle != null) {
            currentCyclePosition = getArguments().getInt(CURRENT_CYCLE_POSITION);
            currentCycle = cycleList.getCycleList().get(currentCyclePosition);
            nameOfCycle.setText(currentCycle.getName());
        }
        saveCycle = view.findViewById(R.id.save_cycle_button);
        saveCycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentCycle == null) {
                    currentCycle = new Cycle(nameOfCycle.getText().toString());
                    currentCycle.addAllToCycle(tempArr);
                    cycleList.addToCycles(currentCycle);
                } else {
                    List<TypeOfDay> cycle = new ArrayList<>();
                    for (TypeOfDay aTempArr : tempArr) {
                        cycle.add(aTempArr);
                    }
                    currentCycle.setCycle(cycle);
                    currentCycle.setName(nameOfCycle.getText().toString());
                    cycleList.changeCycle(currentCycle, currentCyclePosition);
                }
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
        layoutManager = new GridLayoutManager(getContext(), 7);
        typesList = TypesList.getInstance(getContext());
        tempArr = new TypeOfDay[35];
        adapter = new CycleAddAdapter(typesList.getTypes(), getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        currentCycle = null;
    }

    public static CycleAdd newInstance(int position) {
        CycleAdd fragment = new CycleAdd();
        Bundle args = new Bundle();
        fragment.setCurrentCycle(CycleList.getOurInstance().getCycleList().get(position));
        args.putInt(CURRENT_CYCLE_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    public void setCurrentCycle(Cycle currentCycle) {
        this.currentCycle = currentCycle;
    }

    class CycleAddAdapter extends RecyclerView.Adapter<CycleAddViewHolder> {
        List<TypeOfDay> typesList;
        Context context;
        int numberOfType = 0;
        int defaulColor = getResources().getColor(R.color.colorAccent);

        public CycleAddAdapter(List<TypeOfDay> typesList, Context context) {
            this.typesList = typesList;
            this.context = context;
        }

        @Override
        public CycleAddViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.type_of_day_table_element, parent, false);
            return new CycleAddViewHolder(itemView);
        }

        private void setDataForTypeDayFrame(TextView nameView, TextView wakeupView, String name, String wakeUpTime, int color) {
            nameView.setText(name);
            nameView.setBackgroundColor(color);
            wakeupView.setText(wakeUpTime);
            wakeupView.setBackgroundColor(color);
        }

        @Override
        public void onBindViewHolder(CycleAddViewHolder holder, int position) {
            TypeOfDay currentType = null;
            final TextView name = holder.nameOfType;
            final TextView wakeup = holder.wakeUpTime;
            final int tempPosition = position;
            if (currentCycle == null) {
                setDataForTypeDayFrame(name, wakeup, "", "-", defaulColor);
            } else {
                if (currentCycle.getCycle().get(position) != null) {
                    currentType = currentCycle.getCycle().get(position);
                    setDataForTypeDayFrame(name, wakeup, currentType.getName(),
                            currentType.getTimeOfWakeUp(), currentType.getColor());
                    tempArr[position] = currentType;
                } else {
                    setDataForTypeDayFrame(name, wakeup, "", "-", defaulColor);
                    tempArr[position] = null;
                }
            }
            holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (typesList.size() > 0) {
                        if (numberOfType >= typesList.size()) {
                            numberOfType = 0;
                            setDataForTypeDayFrame(name, wakeup, "", "-", defaulColor);
                        } else {
                            setDataForTypeDayFrame(name, wakeup, typesList.get(numberOfType).getName(),
                                    typesList.get(numberOfType).getTimeOfWakeUp(), typesList.get(numberOfType).getColor());
                            tempArr[tempPosition] = typesList.get(numberOfType);
                            numberOfType++;
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
        RelativeLayout relativeLayout;
        TextView nameOfType;
        TextView wakeUpTime;

        CycleAddViewHolder(View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.type_of_day_background_table);
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
