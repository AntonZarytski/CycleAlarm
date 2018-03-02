package zaritsky.com.cyclealarm.fragments;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import zaritsky.com.cyclealarm.R;
import zaritsky.com.cyclealarm.models.Cycle;
import zaritsky.com.cyclealarm.models.CycleList;
import zaritsky.com.cyclealarm.models.TypeOfDay;
import zaritsky.com.cyclealarm.models.TypesList;

public class CycleAdd extends Fragment{
    private TypesList typesList;
    private CycleList cycleList = CycleList.getInstance(getContext());
    private Cycle cycle;
    private LinearLayout typeOfDayFrame;
    private TextView nameOfType;
    private TextView timeOfWakeUp;
    private Button saveCycle;
    //TODO листенер на изменение имя
    private TextView nameOfCycle;
    private int numberOfType=0;
    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cycle_add_fragment, container, false);
        typeOfDayFrame = view.findViewById(R.id.type_of_day_background);
        nameOfType = view.findViewById(R.id.type_of_day_name);
        timeOfWakeUp = view.findViewById(R.id.type_of_day_wake_up);
        nameOfCycle = view.findViewById(R.id.name_of_cycle_text_view);
        saveCycle = view.findViewById(R.id.save_cycle_button);
        typesList = TypesList.getInstance(getContext());
        test();

        typeOfDayFrame.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                //typeOfDayFrame.setBackgroundColor(typeOfDays.get(numberOfType).getColor().getComponentCount());
                nameOfType.setText(typesList.getTypes().get(numberOfType).getName());
                timeOfWakeUp.setText(typesList.getTypes().get(numberOfType).getTimeOfWakeUp());
                numberOfType++;
            }
        });

        saveCycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cycle = new Cycle(nameOfCycle.getText().toString());
                cycleList.addToCycles(cycle);
            }
        });
        return view;
    }

    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void test() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 2);
        calendar.set(Calendar.MINUTE, 36);
        Color color =  new Color();
        TypeOfDay type1 = new TypeOfDay("Утро", calendar, color);
        Calendar calendar1 = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 2);
        calendar.set(Calendar.MINUTE, 36);
        Color color2 =  new Color();
        TypeOfDay type2 = new TypeOfDay("День", calendar1, color2);
        Calendar calendar2 = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 2);
        calendar.set(Calendar.MINUTE, 36);
        Color color3 =  new Color();
        TypeOfDay type3 = new TypeOfDay("Ночь", calendar2, color3);
        typesList.addType(type1);
        typesList.addType(type2);
        typesList.addType(type3);

    }
}
