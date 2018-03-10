package zaritsky.com.cyclealarm.models;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Cycle implements Serializable {
    private String name;
    private List<TypeOfDay> cycle;

    public Cycle(String name) {
        this.name = name;
        cycle = new ArrayList<>(35);
    }

    public Cycle() {
        cycle = new ArrayList<>(35);
    }

    public List<TypeOfDay> getCycle(){
        return cycle;
    }
    public void addToCycle(TypeOfDay typeOfDay){
        cycle.add(typeOfDay);
    }
    public void addToCycle(TypeOfDay typeOfDay, int index){
        cycle.set(index, typeOfDay);
    }
    public void addAllToCycle(TypeOfDay ... types){
        Collections.addAll(cycle, types);
    }
    public void removeFromCycle(TypeOfDay typeOfDay){
        cycle.remove(typeOfDay);
    }
    public void removeFromCycle(int index){
        cycle.remove(index);
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
