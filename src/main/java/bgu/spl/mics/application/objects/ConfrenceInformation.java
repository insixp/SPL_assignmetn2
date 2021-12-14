package bgu.spl.mics.application.objects;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConfrenceInformation {///think how to implement

    private String name;
    private int date;
    int tick;
    public ConfrenceInformation(String name,int date){
        this.name=name;
        this.date=date;
        this.tick=1;
    }
    public void nextTick(){
        tick++;
    }
}
