package zaritsky.com.cyclealarm.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import zaritsky.com.cyclealarm.R;
import zaritsky.com.cyclealarm.models.Cycle;
import zaritsky.com.cyclealarm.models.CycleList;
import zaritsky.com.cyclealarm.models.TypeOfDay;

public class CalendarFragment extends Fragment {
    EventDecorator decorator;
    MaterialCalendarView calendarView;
    Spinner cycleSelector;
    Cycle currentCycle;
    List<Cycle> cycleList;
    List<TypeOfDay> typeCycle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        cycleList = CycleList.getInstance(getContext()).getCycleList();
        View view = inflater.inflate(R.layout.calendar_fragment, container, false);
        cycleSelector = view.findViewById(R.id.cycle_selector);
        List<CharSequence> cyclesNames = new ArrayList<>();
        for (int i = 0; i < cycleList.size(); i++) {
            cyclesNames.add(cycleList.get(i).getName());
        }
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, cyclesNames);
        cycleSelector.setAdapter(adapter);
        if (cycleList!=null){
         typeCycle = new ArrayList<>();
        currentCycle = cycleList.get(0);
            for (int i = 0; i <currentCycle.getCycle().size() ; i++) {
                if (currentCycle.getCycle().get(i)!=null){
                    typeCycle.add(currentCycle.getCycle().get(i));
                }
            }
        }
        cycleSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentCycle = cycleList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
               // currentCycle = cycleList.get(0);
            }
        });
        final int[] count = new int[1];
        count[0]=0;
        final ArrayList<CalendarDay> days = new ArrayList<>();
        calendarView = view.findViewById(R.id.calendarView);
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                if(typeCycle!=null) {
                    if (typeCycle.size() == count[0])
                        count[0] = 0;
                    widget.setSelectionColor(typeCycle.get(count[0]).getColor());
                    count[0]++;
                    days.add(date);
                    decorator = new EventDecorator(typeCycle.get(count[0]).getColor(), days);
                    //decorator.decorate();
                }
            }
        });
        return view;
    }

    public class EventDecorator implements DayViewDecorator {

        private final int color;
        private final HashSet<CalendarDay> dates;

        public EventDecorator(int color, Collection<CalendarDay> dates) {
            this.color = color;
            this.dates = new HashSet<>(dates);
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new DotSpan(5, color));
        }
    }
}
