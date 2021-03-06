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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import zaritsky.com.cyclealarm.R;
import zaritsky.com.cyclealarm.interfaces.AbleToChangeFragment;
import zaritsky.com.cyclealarm.models.TypeOfDay;
import zaritsky.com.cyclealarm.models.TypesList;

/**
 * RecyclerView for all typesOfDay of user. The source of data - is the List<TypeOfDay> that is loaded from
 * the model-class singleton TypesList
 */
public class TypeDayRecyclerList extends Fragment {
    private static AbleToChangeFragment callBackAvtivity;
    private FloatingActionButton fab;
    private TypesList typesList;
    private TypeDayAdapter adapter;
    private RecyclerView recyclerView;

    /**
     * Interface callBackAvtivity for call the method for change the fragment for the selected position
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callBackAvtivity = (AbleToChangeFragment) context;
        typesList = TypesList.getInstance(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.types_list_recycler, container, false);
        recyclerView = view.findViewById(R.id.types_recycler_view);
        adapter = new TypeDayAdapter(typesList, getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        fab = view.findViewById(R.id.floating_button_add_type_day);
        /**
         * create new TypeOfDay
         * */
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TypeDayAdd editTypeDay = new TypeDayAdd();
                callBackAvtivity.changeFragments(R.id.content_main, editTypeDay);
            }
        });


        return view;
    }

    /**
     * adapter for recyclerView
     */
    static class TypeDayAdapter extends RecyclerView.Adapter<TypeDayViewHolder> {
        List<TypeOfDay> types;
        Context context;

        TypeDayAdapter(TypesList typesList, Context context) {
            this.types = typesList.getTypes();
            this.context = context;
        }

        @Override
        public TypeDayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.type_of_day_linear_element, parent, false);
            return new TypeDayViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final TypeDayViewHolder holder, final int position) {
            TypeOfDay current = types.get(position);
            holder.colorOfType.setBackgroundColor(current.getColor());
            holder.nameOfType.setText(current.getName());
            holder.wakeUpTime.setText(current.getTimeOfWakeUp());
            holder.typeElement.setOnClickListener(new View.OnClickListener() {
                /**
                 * call fragment for the selected position
                 * */
                @Override
                public void onClick(View v) {
                    TypeDayAdd type = TypeDayAdd.newInstance(position);
                    callBackAvtivity.onSelectedFragment(type, position);
                }
            });
        }

        @Override
        public int getItemCount() {
            if (types != null)
                return types.size();
            else
                return 0;
        }
    }
    /**
     * viewHolder for recyclerView
     */
    static class TypeDayViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout typeElement;
        TextView nameOfType;
        TextView wakeUpTime;
        ImageView colorOfType;

        public TypeDayViewHolder(View itemView) {
            super(itemView);
            typeElement = itemView.findViewById(R.id.type_of_day_element_linear);
            nameOfType = itemView.findViewById(R.id.type_of_day_name_linear);
            wakeUpTime = itemView.findViewById(R.id.type_of_day_wake_up_linear);
            colorOfType = itemView.findViewById(R.id.color_of_type_linear);
        }
    }
}
