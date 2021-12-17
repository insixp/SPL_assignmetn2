package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Output {


    public static class oModel {
        public String name;
        public Data data;
        public Model.Status status;
        public Model.Result result;

        public oModel(String name, Data data, Model.Status status, Model.Result result){
            this.name = name;
            this.data = data;
            this.status = status;
            this.result = result;
        }
    }

    public static class oStudent{
        public String name;
        public String department;
        public Student.Degree status;
        public int publications;
        public int papersRead;
        public List<oModel> models;

        public oStudent(String name, String department, Student.Degree status, int publications, int papersRead, List<oModel> models){
            this.name = name;
            this.department = department;
            this.status = status;
            this.publications = publications;
            this.papersRead = papersRead;
            this.models = models;
        }

    }

    public static class oConference{
        public String name;
        public int date;
        public List<oModel> publications;

        public oConference(String name, int date, List<oModel> publications){
            this.name = name;
            this.date = date;
            this.publications = publications;
        }
    }

    public Collection<oStudent>     studentCollection;
    public Collection<oConference>  conferenceCollection;
    public int                      cpuTime;
    public int                      gpuTime;
    public int                      batchesProcessed;

    public Output(){
        this.studentCollection = new ArrayList<>();
        this.conferenceCollection = new ArrayList<>();
    }

    public void addStudent(oStudent s) { this.studentCollection.add(s); }
    public void addConference(oConference c) { this.conferenceCollection.add(c); }
    public void setcpuTime(int t) { this.cpuTime = t; }
    public void setgpuTime(int t) { this.gpuTime = t; }
    public void setBatchesProcessed(int b) { this.batchesProcessed = b; }
}
