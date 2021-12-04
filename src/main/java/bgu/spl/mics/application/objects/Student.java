package bgu.spl.mics.application.objects;

/**
 * Passive object representing single student.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Student {
    /**
     * Enum representing the Degree the student is studying for.
     */
    enum Degree {
        MSc, PhD
    }

    private String name;
    private String department;
    private Degree status;
    private int publications;
    private int papersRead;

    public Student(String name, String department, Degree status){
        this.name = name;
        this.department = department;
        this.status = status;
        this.publications = 0;
        this.papersRead = 0;
    }

    public String getName(){ return this.name; }
    public String getDepartment(){ return this.department; }
    public Degree getStatus(){ return this.status; }
    public int getPublications(){ return this.publications; }
    public int getPapersRead(){ return this.papersRead; }

}
