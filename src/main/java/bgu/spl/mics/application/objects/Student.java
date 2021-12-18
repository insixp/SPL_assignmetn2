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
    private Model currentWorkingModel;
    private int modelIndex;
    private int id;
    public int publication;
    public int papersRead;

    public Student(String name, String department, Degree status, int id){
        this.name = name;
        this.department = department;
        this.models = new ArrayList<Model>();
        this.status = status;
        this.id = id;
        this.currentWorkingModel = null;
        this.publication = 0;
        this.papersRead = 0;
        this.modelIndex = -1;
    }

    //  Getters
    public int getId(){
        return this.id;
    }
    public String getName(){ return this.name; }
    public String getDepartment(){ return this.department; }
    public Degree getStatus(){ return this.status; }
    public Model getModel(int index){
        if(this.models.size() < index)
            return null;
        return this.models.get(index);
    }
    public Model getCurrentWorkingModel() { return this.currentWorkingModel; }
    public int getPublished() { return this.publication; }
    public int getRead() { return this.papersRead; }
    public List<Model> getModelList() { return this.models; }
    public int getModelsToProcessLeft() { return this.models.size() - this.modelIndex; }

    public void nextModel(){
        if(this.modelIndex < this.models.size() - 1){
            this.modelIndex++;
            this.currentWorkingModel = this.models.get(this.modelIndex);
        }
    }
    public void updateModelStatus(Model.Status status, Model.Result result){
        this.currentWorkingModel.setStatus(status);
        this.models.get(this.modelIndex).setStatus(status);
        this.models.get(this.modelIndex).setResult(result);
    }
    public void increasePublished() { this.publication++; }
    public void increaseRead() { this.papersRead++; }

    //
    public void addModel(Model m){ this.models.add(m); }
    public static Degree stringToDegree(String status){
        if(status.toLowerCase().equals("msc"))
            return Degree.MSc;
        if(status.toLowerCase().equals("phd"))
            return Degree.PhD;
        return null;
    }
}
