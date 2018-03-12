package zaritsky.com.cyclealarm.fragments;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.icu.util.Calendar;

import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;

import zaritsky.com.cyclealarm.R;
import zaritsky.com.cyclealarm.models.CycleList;
import zaritsky.com.cyclealarm.models.TypeOfDay;
import zaritsky.com.cyclealarm.models.TypesList;

public class TypeDayAdd extends Fragment {
    final static String CURRENT_TYPE_POSITION = "CURRENT_TYPE_POSITION";
    TypeOfDay typeOfDay;
    TextView nameOfType;
    int position;
    ImageView colorOfType;
    TimePicker timeWakeUp;
    Button saveButton;
    TypesList typesList;
    LayoutInflater inflater;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_type_day, container, false);
        this.inflater = inflater;
        typesList = TypesList.getInstance(getContext());
        nameOfType = view.findViewById(R.id.name_of_type_text_view);
        colorOfType = view.findViewById(R.id.color_of_type);
        timeWakeUp = view.findViewById(R.id.time_to_wake_up);
        timeWakeUp.setIs24HourView(true);
        saveButton = view.findViewById(R.id.save_type_button);
        if (typeOfDay!=null){
            position = getArguments().getInt(CURRENT_TYPE_POSITION);
            setCurrentParameters();
        }
        final int[] tempcolor = new int[1];
        final ColorPicker cp = new ColorPicker(getActivity(), 0, 0, 0, 0);
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
                tempcolor[0] = color;
                cp.closeOptionsMenu();
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(typeOfDay==null) {
                    @SuppressLint({"NewApi", "LocalSuppress"})
                    Calendar calendar = Calendar.getInstance();
                    typeOfDay = new TypeOfDay(nameOfType.getText().toString(), calendar, tempcolor[0]);
                    typesList.addType(typeOfDay);
                }else
                    typesList.editType(typeOfDay, position);
            }
        });
        nameOfType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(nameOfType);
            }
        });
        return view;
    }
    @SuppressLint("NewApi")
    private void setCurrentParameters(){
        nameOfType.setText(typeOfDay.getName());
        colorOfType.setBackgroundColor(typeOfDay.getColor());
        String wakeUp = typeOfDay.getTimeOfWakeUp();
        String[] time = wakeUp.split(":");
        int hour = Integer.valueOf(time[0]);
        int minute = Integer.valueOf(time[1]);
        timeWakeUp.setHour(hour);
        timeWakeUp.setMinute(minute);
    }
    public static TypeDayAdd newInstance(int position) {
        TypeDayAdd fragment = new TypeDayAdd();
        Bundle args = new Bundle();
        fragment.setCurrentCycle(TypesList.getOurInstance().getTypes().get(position));
        args.putInt(CURRENT_TYPE_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    public void setCurrentCycle(TypeOfDay typeOfDay) {
        this.typeOfDay = typeOfDay;
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
