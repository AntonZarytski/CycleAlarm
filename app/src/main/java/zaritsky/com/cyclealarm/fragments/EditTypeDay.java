package zaritsky.com.cyclealarm.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.icu.util.Calendar;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;

import zaritsky.com.cyclealarm.R;
import zaritsky.com.cyclealarm.models.TypeOfDay;
import zaritsky.com.cyclealarm.models.TypesList;

public class EditTypeDay extends Fragment {
    TypeOfDay typeOfDay;
    TextView nameOfType;
    ImageView colorOfType;
    TimePicker timeWakeUp;
    Button saveButton;
    TypesList typesList;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_type_day, container, false);
        typesList = TypesList.getInstance(getContext());
        nameOfType = view.findViewById(R.id.name_of_type_text_view);
        colorOfType = view.findViewById(R.id.color_of_type);
        timeWakeUp = view.findViewById(R.id.time_to_wake_up);
        timeWakeUp.setIs24HourView(true);
        saveButton = view.findViewById(R.id.save_type_button);
        final ColorPicker cp = new ColorPicker(getActivity(), 0,0,0,0);
        colorOfType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cp.show();
            }
        });
        cp.setCallback(new ColorPickerCallback() {
            @Override
            public void onColorChosen(@ColorInt int color) {
                colorOfType.setBackgroundColor(color);
                cp.closeOptionsMenu();
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                @SuppressLint({"NewApi", "LocalSuppress"})
                Calendar calendar = Calendar.getInstance();
                typeOfDay = new TypeOfDay(nameOfType.getText().toString(),calendar,  colorOfType.getDrawingCacheBackgroundColor());
                typesList.addType(typeOfDay);
            }
        });
        return view;
    }
}
