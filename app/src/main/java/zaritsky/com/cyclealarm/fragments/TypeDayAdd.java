package zaritsky.com.cyclealarm.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;;
import android.widget.Toast;

import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;

import zaritsky.com.cyclealarm.R;
import zaritsky.com.cyclealarm.interfaces.AbleToChangeFragment;
import zaritsky.com.cyclealarm.models.Alarm;
import zaritsky.com.cyclealarm.models.AlarmList;
import zaritsky.com.cyclealarm.models.TypeOfDay;
import zaritsky.com.cyclealarm.models.TypesList;

/**
 * class for creating custom typeOfDay with custom alarm for this type,
 * here user can check color of type(will be imposed on calendar`s element(some day)),
 * alarm for this day, and name for this type
 */
public class TypeDayAdd extends Fragment {
    final static String CURRENT_TYPE_POSITION = "CURRENT_TYPE_POSITION";
    final static String FROM_TYPE_DAY_ADD_FRAGMENT = "FROM_TYPE_DAY_ADD_FRAGMENT";
    final static String TYPE_DAY_ADD_FRAGMENT = "TYPE_DAY_ADD_FRAGMENT";
    final static String NAME_OF_TYPE = "NAME_OF_TYPE";
    final static String COLOR_OF_TYPE = "COLOR_OF_TYPE";
    private TypeOfDay currentTypeOfDay;
    private Alarm currentAlarm;
    private TextView nameOfType;
    private int currentTypePosition;
    private int alarmPosition;
    private ImageView colorOfType;
    private TextView nameOfCurrentAlarm;
    private TextView timeWakeUp;
    private LinearLayout alarmDataLayout;
    private Button saveButton;
    private TypesList typesList;
    private LayoutInflater inflater;

    private AbleToChangeFragment callBackAvtivity;

    /**
     * Interface callBackAvtivity for call the method for change the fragment for the selected position
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callBackAvtivity = (AbleToChangeFragment) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.edit_type_day, container, false);
        this.inflater = inflater;
        typesList = TypesList.getInstance(getContext());
        nameOfType = view.findViewById(R.id.name_of_type_text_view);
        alarmDataLayout = view.findViewById(R.id.alarm_type_of_day_layout);
        colorOfType = view.findViewById(R.id.color_of_type);
        nameOfCurrentAlarm = view.findViewById(R.id.name_alarm_of_type);
        timeWakeUp = view.findViewById(R.id.time_of_wake_up_type_day);
        saveButton = view.findViewById(R.id.save_type_button);
        if (savedInstanceState != null) {
            colorOfType.setBackgroundColor(savedInstanceState.getInt(COLOR_OF_TYPE, 0));
            nameOfType.setText(savedInstanceState.getString(NAME_OF_TYPE));
        }
        if (currentTypeOfDay != null) {
            currentTypePosition = getArguments().getInt(CURRENT_TYPE_POSITION);
            setCurrentParameters();
        }
        final int[] tempcolor = new int[1];
        /**
         * select color for this type
         * */
        final ColorPicker cp = new ColorPicker(getActivity(), 128, 128, 128, 128);
        colorOfType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cp.show();
            }
        });
        cp.setCallback(new ColorPickerCallback() {
            @Override
            public void onColorChosen(@ColorInt int color) {
                if (color == 0) {
                    color = Color.WHITE;
                }
                colorOfType.setBackgroundColor(color);
                tempcolor[0] = color;
                cp.closeOptionsMenu();
            }
        });
        /**
         * add new alarm for this type
         * */
        alarmDataLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlarmAdd newAlarm = new AlarmAdd();
                Bundle bundle = new Bundle();
                bundle.putBoolean(FROM_TYPE_DAY_ADD_FRAGMENT, true);
                newAlarm.setArguments(bundle);
                callBackAvtivity.changeFragments(R.id.content_main, newAlarm);
                newAlarm.getFragmentManager().putFragment(bundle, TYPE_DAY_ADD_FRAGMENT, TypeDayAdd.this);
            }
        });
        /**
         * add or change current type to typesList
         * */
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentTypeOfDay == null) {
                    if (currentAlarm != null) {
                        currentTypeOfDay = new TypeOfDay(nameOfType.getText().toString(), currentAlarm, tempcolor[0]);
                        currentTypeOfDay.setAlarmPosition(alarmPosition);
                        typesList.addType(currentTypeOfDay);
                        currentTypeOfDay = null;
                    } else {
                        Toast.makeText(getContext(), "Сначало добавьте будильник", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    currentTypeOfDay.setName(nameOfType.getText().toString());
                    currentTypeOfDay.setAlarmOfType(currentAlarm);
                    currentTypeOfDay.setColor(tempcolor[0]);
                    currentTypeOfDay.setAlarmPosition(alarmPosition);
                    typesList.editType(currentTypeOfDay, currentTypePosition);
                    currentTypeOfDay = null;
                    currentAlarm = null;
                }
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

    /**
     * set alarm for this type, if alarm was create for custom TypeOfDay (set parameters from
     * outside fragment)
     */
    @Override
    public void onResume() {
        super.onResume();
        if (getArguments() != null) {
            alarmPosition = getArguments().getInt(AlarmAdd.CURRENT_ALARM_POSITION);
            boolean fromAlarmAdd = getArguments().getBoolean(FROM_TYPE_DAY_ADD_FRAGMENT, false);
            if (fromAlarmAdd)
                currentAlarm = AlarmList.getInstance(getContext()).getAlarmList().get(alarmPosition);
            if (currentAlarm != null) {
                nameOfCurrentAlarm.setText(currentAlarm.getName());
                timeWakeUp.setText(currentAlarm.getFormatedTime());
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        ColorDrawable drawable = (ColorDrawable) colorOfType.getBackground();
        if (drawable != null) {
            int savedColor = drawable.getColor();
            outState.putInt(COLOR_OF_TYPE, savedColor);
        }
        outState.putString(NAME_OF_TYPE, nameOfType.getText().toString());
        super.onSaveInstanceState(outState);
    }

    /**
     * set parameters for current type
     */
    private void setCurrentParameters() {
        nameOfType.setText(currentTypeOfDay.getName());
        colorOfType.setBackgroundColor(currentTypeOfDay.getColor());
        currentAlarm = currentTypeOfDay.getAlarmOfType();
        String wakeUp = currentTypeOfDay.getTimeOfWakeUp();
        String[] time = wakeUp.split(":");
        int hour = Integer.valueOf(time[0]);
        int minute = Integer.valueOf(time[1]);
    }

    /**
     * create new TypeDayAdd fragmen for calling from the outside class
     */
    public static TypeDayAdd newInstance(int position) {
        TypeDayAdd fragment = new TypeDayAdd();
        Bundle args = new Bundle();
        fragment.setCurrentCycle(TypesList.getOurInstance().getTypes().get(position));
        args.putInt(CURRENT_TYPE_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    public void setCurrentCycle(TypeOfDay typeOfDay) {
        this.currentTypeOfDay = typeOfDay;
    }

    /**
     * show dialog window for edit some text
     */
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
