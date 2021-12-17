package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.messages.PublishConferenceBroadcast;

import java.util.*;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConfrenceInformation {///think how to implement

    private String name;
    private int date;
    private int tick;
    private List<Model> modelCollection;
    public boolean publish;

    public ConfrenceInformation(String name,int date){
        this.name = name;
        this.date = date;
        this.tick = 1;
        this.publish = false;
        this.modelCollection = new ArrayList<>();
    }


    public String getName() { return this.name; }
    public int getDate() { return this.date; }
    public List<Model> getModels() { return this.modelCollection; }
    public void addResult(Model m){
        this.modelCollection.add(m);
    }
    public PublishConferenceBroadcast getPulishBrod(){
        return new PublishConferenceBroadcast(this.modelCollection);
    }
    private void nextTick(){
        tick++;
    }
    public void proccessNextTick(){
        nextTick();
        if(tick >= date){
            publish = true;
        }
    }

}
