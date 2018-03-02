package zaritsky.com.cyclealarm.models;


import java.util.ArrayList;
import java.util.List;

public class Cycle {
    private String name;
    private List<TypeOfDay> cycle;

    public Cycle(String name) {
        this.name = name;
        cycle = new ArrayList<>();
    }
    public List<TypeOfDay> getCycle(){
        return cycle;
    }
    public void addToCycle(TypeOfDay typeOfDay){
        cycle.add(typeOfDay);
    }
    public void removeFromCycle(TypeOfDay typeOfDay){
        cycle.remove(typeOfDay);
    }

    public String getName() {
        return name;
    }
}
