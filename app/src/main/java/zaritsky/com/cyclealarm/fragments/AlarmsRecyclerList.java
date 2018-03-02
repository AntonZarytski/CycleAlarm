package zaritsky.com.cyclealarm.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import zaritsky.com.cyclealarm.R;
import zaritsky.com.cyclealarm.interfaces.AbleToChangeFragment;
import zaritsky.com.cyclealarm.models.Alarm;
import zaritsky.com.cyclealarm.models.AlarmList;

import static android.provider.Telephony.Mms.Part.FILENAME;

public class AlarmsRecyclerList extends Fragment {
    private RecyclerView recyclerView;
    private AlarmAdapter adapter;
    private List<Alarm> alarmList;
    private AbleToChangeFragment callBackAvtivity;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callBackAvtivity = (AbleToChangeFragment) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.alarms_list_fragment, container, false);
        recyclerView = view.findViewById(R.id.alarms_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        FloatingActionButton fab = view.findViewById(R.id.floating_button_add_alarm);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlarmAdd newAlarm = new AlarmAdd();
                callBackAvtivity.replaceFragments(R.id.content_main, newAlarm);
                Toast toast = Toast.makeText(view.getContext(), "Плавающая кнопка", Toast.LENGTH_LONG);
                toast.show();
            }
        });
        return view;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final AlarmList alarms = AlarmList.getInstance(getContext());

                try {
                    File file = new File(getActivity().getApplicationContext().getFilesDir(), FILENAME);
                    if (!file.exists()) {
                        return;
                    }
                    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
                    alarmList = (List<Alarm>) ois.readObject();
                    if (alarmList==null){
                        alarmList = new ArrayList<>();
                    }
                    alarms.setAlarmList(alarmList);
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
        adapter = new AlarmAdapter(alarmList, getContext());


    }

    private class AlarmAdapter extends RecyclerView.Adapter<AlarmViewHolder> {
        List<Alarm> alarmList;
        Context context;

        public AlarmAdapter(List<Alarm> alarmList, Context context) {
            this.alarmList = alarmList;
            this.context = context;
        }

        @Override
        public AlarmViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_recycler_element, parent, false);
            return new AlarmViewHolder(itemView);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onBindViewHolder(final AlarmViewHolder holder, int position) {
            final Alarm alarm = alarmList.get(position);
            holder.timeOfAlarm.setText(alarm.getFormatedTime());
            holder.daysOfActive.setText("Пн Вт Ср Чт Пт Сб Вс"/*alarm.getDatesOfActive()*/);
            final Switch activeAlarm = holder.onOffAlarmSwitch;
            activeAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (activeAlarm.isChecked()) {
                        alarm.setActive(true);
                    } else alarm.setActive(false);
                }
            });
            holder.expandElementDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast toast = Toast.makeText(getContext(), "Меню раздвинуто", Toast.LENGTH_LONG);
                    toast.show();
                }
            });
            holder.alarmView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callBackAvtivity.onSelectedFragment(holder.getAdapterPosition());
                    /*Toast toast = Toast.makeText(getContext(), "На фрагмент будильника", Toast.LENGTH_LONG);
                    toast.show();*/
                }
            });

        }

        @Override
        public int getItemCount() {
            if (alarmList != null) {
                return alarmList.size();
            } else return 0;
        }
    }

    static class AlarmViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout alarmView;
        TextView timeOfAlarm;
        Switch onOffAlarmSwitch;
        TextView daysOfActive;
        ImageView expandElementDown;

        AlarmViewHolder(View itemView) {
            super(itemView);
            alarmView = itemView.findViewById(R.id.alarms_recycler_view);
            timeOfAlarm = itemView.findViewById(R.id.time_of_alarm_text_view);
            onOffAlarmSwitch = itemView.findViewById(R.id.on_or_off_switch);
            daysOfActive = itemView.findViewById(R.id.day_of_active_text_view);
            expandElementDown = itemView.findViewById(R.id.to_expand_element_down);
        }

    }
}
