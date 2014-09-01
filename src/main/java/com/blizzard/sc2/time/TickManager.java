package com.blizzard.sc2.time;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class TickManager implements TickListener {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

    private final List<TickListener> tickListeners = new ArrayList<>();

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------
    public TickManager(){}
    @Override
    public void applyTick() {
        for (TickListener tickListerner : this.tickListeners) {
            tickListerner.applyTick();
        }
    }

    public void addTickListerner(TickListener tickListerner) {
        this.tickListeners.add(tickListerner);
    }

}
