package bgu.spl.mics.application;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.objects.*;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;

/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */
public class CRMSRunner {
    public static void main(String[] args) {
        try {
            Gson gson = new Gson();
            JsonReader jsReader = new JsonReader(new FileReader("example_input.json"));
            Setup s = gson.fromJson(jsReader, Setup.class);
            MessageBusImpl msgBus = MessageBusImpl.getInstance();   //  Activate message bus
            Cluster cluster = Cluster.getInstance();    //  Activate cluster
            for(String gpu_type : s.GPUS){  //  Add gpu micro service.

            }
            for(int cpu_cores : s.CPUS){ //  Add cpu microservice.
                //  Run new thread of CPU
            }
            for(ConfrenceInformation ci : s.Conferences){ //  Add Conference Micro Service.
                //  Run new Microservice of conference
            }
            for(Setup.sStudent setupStudent : s.Students){ //  Add student microservice.
                Student student = new Student(setupStudent.name, setupStudent.department, Student.stringToDegree(setupStudent.status));
                for(Setup.sModel m : setupStudent.models){
                    student.addModel(new Model(m.name, new Data(Data.stringToType(m.type), m.size), student));
                }
                //  Run new Microservice of student
            }
            //  Add Tick microservice

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
