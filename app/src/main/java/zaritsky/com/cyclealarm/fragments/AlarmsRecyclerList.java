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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import zaritsky.com.cyclealarm.R;
import zaritsky.com.cyclealarm.models.Alarm;
import zaritsky.com.cyclealarm.models.AlarmListModel;

public class AlarmsList extends Fragment {
    private RecyclerView recyclerView;
    private AlarmAdapter adapter;
    private List<Alarm> alarmList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.alarms_list_fragment, container, false);
        recyclerView = view.findViewById(R.id.alarms_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        FloatingActionButton fab = view.findViewById(R.id.floating_button_main);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(view.getContext(), "Плавающая кнопка", Toast.LENGTH_LONG);
                toast.show();
            }
        });

        return view;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        alarmList = AlarmListModel.getInstance(getContext()).getAlarmList();
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

        @Override
        public void onBindViewHolder(AlarmViewHolder holder, int position) {
            final Alarm alarm = alarmList.get(position);
            holder.timeOfAlarm.setText("00:00" /*alarm.getTimeOfActive().toString()*/);
            holder.daysOfActive.setText("Пн Вт Ср Чт Пт Сб Вс"/*alarm.getDatesOfActive()*/);
            final Switch activeAlarm = holder.onOffAlarmSwitch;
            activeAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
                    Toast toast = Toast.makeText(getContext(), "На фрагмент будильника", Toast.LENGTH_LONG);
                    toast.show();
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
