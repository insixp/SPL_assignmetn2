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

    public ConfrenceInformation(String name,int date){
        this.name = name;
        this.date = date;
        this.tick = 1;
        this.modelCollection = new ArrayList<>();
    }
    /**
     *   getters,
     *  @PRE: trivial
     *  @POST: trivial
     **/
    public String getName() { return this.name; }
    public int getDate() { return this.date; }
    public List<Model> getModels() { return this.modelCollection; }
    /**
     * add new result for the collection
     *  @PRE: none
     *  @Invariant m!=null
     *  @POST: this.modelCollection.size()=@pre this.modelCollection.size()+1
     **/
    public void addResult(Model m){
        this.modelCollection.add(m);
    }
    /**
     *   return PublishConferenceBroadcast
     *  @PRE: this.modelCollection!=null
     *  @POST: this.modelCollection==@pre this.modelCollection
     **/
    public PublishConferenceBroadcast getPulishBrod(){
        return new PublishConferenceBroadcast(this.modelCollection);
    }
}
