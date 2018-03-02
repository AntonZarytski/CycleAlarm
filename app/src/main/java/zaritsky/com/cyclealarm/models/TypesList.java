package zaritsky.com.cyclealarm.models;


import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import zaritsky.com.cyclealarm.models.dataBase.CycleDataSource;

public class TypesList {
    private List<TypeOfDay> types;
    private static final TypesList ourInstance = null;
    private CycleDataSource cycleDataSource;

    public static TypesList getInstance(Context context) {
        if (ourInstance == null) {
            return new TypesList(context);
        }else
        return ourInstance;
    }

    private TypesList(Context context) {
        types = new ArrayList<>();
        cycleDataSource = new CycleDataSource(context);
        cycleDataSource.open();
        types = cycleDataSource.getAllTypes();
        cycleDataSource.close();
    }
    public void addType(TypeOfDay type){
        types.add(type);
        cycleDataSource.open();
        cycleDataSource.addType(type);
        cycleDataSource.close();
    }
    public  void removeTypeOfDay(TypeOfDay type){
        types.remove(type);
        cycleDataSource.open();
        cycleDataSource.deleteType(type);
        cycleDataSource.close();
    }

    public List<TypeOfDay> getTypes() {
        return types;
    }

    public void setTypes(List<TypeOfDay> types) {
        this.types = types;
        cycleDataSource.open();
        cycleDataSource.deleteAll();
        for (int i = 0; i <this.types.size() ; i++) {
            cycleDataSource.addType(this.types.get(i));
        }
        cycleDataSource.close();
    }
}
