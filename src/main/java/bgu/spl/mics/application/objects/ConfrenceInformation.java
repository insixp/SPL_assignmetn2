package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.messages.PublishConferenceBroadcast;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConfrenceInformation {///think how to implement

    private String name;
    private int date;
    private int tick;
    private PublishConferenceBroadcast res;
    public boolean publish;
    public ConfrenceInformation(String name,int date){
        this.name = name;
        this.date = date;
        this.tick = 1;
        this.publish = false;
        this.res = new PublishConferenceBroadcast();
    }


    public String getName() { return this.name; }
    public int getDate() { return this.date; }
    public void addResult(Model result){
        this.res.addResult(result);
    }
    public PublishConferenceBroadcast getPulishBrod(){
        return this.res;
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
