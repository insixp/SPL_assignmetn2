package bgu.spl.mics.application.objects;

/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Model {

    public enum Status {
        PreTrained, Training, Trained, Tested
    }

    public enum Result {
        None, Good, Bad
    }

    private String name;
    private Data data;
    private Student student;
    private Status status;
    private Result result;

    public Model(String name, Data data, Student student){
        this.name = name;
        this.data = data;
        this.student = student;
        this.status = Status.PreTrained;
        this.result = Result.None;
    }

    public String getName(){ return this.name; }
    public Data getData(){ return this.data; }
    public Student getStudent(){ return this.student; }
    public Result getResult(){ return this.result; }
    public Status getStatus() { return this.status; }
    public void setResult(Result r){this.result=r;}
    public void setStatus(Status s){this.status=s;}
    public Status getStatus(){return this.status;}

}
