package bgu.spl.mics.application;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.objects.Cluster;
import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.application.objects.Setup;
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
            //  Activate message bus
            MessageBusImpl msgBus = MessageBusImpl.getInstance();
            //  Activate cluster
            Cluster cluster = Cluster.getInstance();
            //  Add gpu micro service
            for(String gpu_type : s.GPUS){
                //  Run new thread of GPU
            }
            //  Add cpu micro service
            for(int cpu_cores : s.CPUS){
                //  Run new thread of CPU
            }
            //  Add Conference Micro Service
            for(ConfrenceInformation ci : s.Conferences){
                //  Run new Microservice of conference
            }
            //  Add student micro service
            for(Setup.sStudent student: s.Students){
                //  Run new Microservice of Student
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
