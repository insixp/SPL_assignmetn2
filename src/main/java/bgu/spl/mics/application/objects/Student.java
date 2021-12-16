package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.messages.PublishResultsEvent;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TrainModelEvent;
import junit.framework.TestResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Passive object representing single student.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Student {
    /**
     * Enum representing the Degree the student is studying for.
     */
    public enum Degree {
        MSc, PhD
    }

    private String name;
    private String department;
    private Degree status;
    private List<Model> models;
    private int id;
    private int publications;
    private int papersRead;
    private int modelInPross;
    public  int publish;
    public boolean canPublish;
    public Student(String name, String department, Degree status, int id){
        this.name = name;
        this.department = department;
        this.models = new ArrayList<Model>();
        this.status = status;
        this.publications = 0;
        this.papersRead = 0;
        this.modelInPross=-1;
        this.publish=-1;
        this.canPublish=false;
        this.id=id;
    }
    public int getId(){
        return this.id;
    }

    public String getName(){ return this.name; }
    public String getDepartment(){ return this.department; }
    public Degree getStatus(){ return this.status; }
    public int getPublications(){ return this.publications; }
    public int getPapersRead(){ return this.papersRead; }
    public Model getModel(int index){ return this.models.get(index); }
    public void addModel(Model m){ this.models.add(m); }
    public static Degree stringToDegree(String status){
        if(status.toLowerCase() == "msc")
            return Degree.MSc;
        if(status.toLowerCase() == "phd")
            return Degree.PhD;
        return null;
    }
    public boolean canTest(){
        if(this.models.get(this.modelInPross).getStatus()== Model.Status.Trained) {
            return true;
        }
        return false;
    }
    public boolean canAct(){
       if(this.modelInPross==-1){///FIRST TIME
           this.modelInPross += 1;
           if (this.models.size() > this.modelInPross)
                return true;
        }
            if (this.models.get(this.modelInPross).getStatus() == Model.Status.Tested) {
                if (this.models.get(this.modelInPross).getResult() == Model.Result.Good) {
                    this.publish = modelInPross;
                    this.canPublish = true;
                }
                this.modelInPross += 1;
                if (this.models.size() > this.modelInPross)
                    return true;
            }
        return false;
    }
    public PublishResultsEvent toPublish(){
        this.canPublish=false;
        return new PublishResultsEvent(this.models.get(publish));
    }
    public TrainModelEvent sendToTrain(){
            return new TrainModelEvent(this.models.get(this.modelInPross));
    }
    public TestModelEvent sendToTest(){
            return new TestModelEvent(this.models.get(this.modelInPross));
    }
    public void incPublications(){
        this.publications+=1;
    }
    public void incPapersRead(){
        this.papersRead+=1;
    }
}
