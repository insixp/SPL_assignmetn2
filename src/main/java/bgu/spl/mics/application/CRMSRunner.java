package bgu.spl.mics.application;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */
public class CRMSRunner {

    public static void main(String[] args) {
        try {
            Gson gson = new Gson();
            JsonReader jsReader = new JsonReader(new FileReader(args[0]));
            Setup s = gson.fromJson(jsReader, Setup.class);
            MessageBusImpl msgBus = MessageBusImpl.getInstance();   //  Activate message bus
            Cluster cluster = Cluster.getInstance();    //  Activate cluster
            int gpuId = 0;
            int cpuId = 0;
            int studentID=0;
            final int BATCHSIZE = 1000;
            List<Thread> threadList = new ArrayList<>();
            List<CPUService> cpuServices = new ArrayList<>();
            List<GPUService> gpuServices = new ArrayList<>();
            List<ConferenceService> conferenceServices = new ArrayList<>();
            List<StudentService> studentServices = new ArrayList<>();

            for(String gpu_type : s.GPUS){  //  Add gpu micro service.
                GPUService gpuService = new GPUService(gpuId, GPU.stringToType(gpu_type), BATCHSIZE, cluster);
                gpuServices.add(gpuService);
                Thread t = new Thread(gpuService);
                t.setName(gpuService.getName());
                threadList.add(t);
                gpuId++;
            }
            for(int cpu_cores : s.CPUS){ //  Add cpu microservice.
                CPUService cpuService = new CPUService(cpuId, cpu_cores, BATCHSIZE, cluster);
                cpuServices.add(cpuService);
                Thread t = new Thread(cpuService);
                t.setName(cpuService.getName());
                threadList.add(t);
                cpuId++;
            }
            for(ConfrenceInformation ci : s.Conferences){ //  Add Conference Micro Service.
                ConferenceService conferenceService = new ConferenceService(ci.getName(), ci.getDate());
                conferenceServices.add(conferenceService);
                Thread t = new Thread(conferenceService);
                t.setName(conferenceService.getName());
                threadList.add(t);
            }
            for(Setup.sStudent setupStudent : s.Students){ //  Add student microservice.
                Student student = new Student(setupStudent.name, setupStudent.department, Student.stringToDegree(setupStudent.status),studentID);
                for(Setup.sModel m : setupStudent.models){
                    student.addModel(new Model(m.name, new Data(Data.stringToType(m.type), m.size), student));
                }
                StudentService studentService = new StudentService(student);
                studentServices.add(studentService);
                Thread t = new Thread(studentService);
                t.setName(studentService.getName());
                threadList.add(t);
                studentID++;
            }
            //  Tick MicroService
            TimeService timeService = new TimeService(s.TickTime, s.Duration);
            Thread t = new Thread(timeService);
            t.setName(timeService.getName());
            threadList.add(t);
            for(int i = 0; i < threadList.size(); i++){
                threadList.get(i).start();
            }
            for(int i = 0;i < threadList.size(); i++){
                threadList.get(i).join();
            }

            Output outputClass = new Output();
            for(StudentService studentService : studentServices){
                List<Output.oModel> oModelCollection = new ArrayList<>();
                Student student = studentService.getStudent();
                for(Model m : student.getModelList()){
                    oModelCollection.add(new Output.oModel(m.getName(), m.getData(), m.getStatus(), m.getResult()));
                }
                Output.oStudent ostudent = new Output.oStudent(student.getName(), student.getDepartment(), student.getStatus(),
                        student.getPublished(), student.getRead(), oModelCollection);
                outputClass.addStudent(ostudent);
            }
            for(ConferenceService cfService : conferenceServices){
                List<Output.oModel> oModelCollection = new ArrayList<>();
                ConfrenceInformation cfINfo = cfService.getConferenceInformation();
                for(Model m : cfINfo.getModels()){
                    oModelCollection.add(new Output.oModel(m.getName(), m.getData(), m.getStatus(), m.getResult()));
                }
                Output.oConference oconference = new Output.oConference(cfINfo.getName(), cfINfo.getDate(), oModelCollection);
                outputClass.addConference(oconference);
            }
            outputClass.setBatchesProcessed(cluster.getStatistics().getTotalDataBatches());
            outputClass.setcpuTime(cluster.getStatistics().getTotalCpuTime());
            outputClass.setgpuTime(cluster.getStatistics().getTotalGpuTime());

            try(FileWriter writer = new FileWriter("output.json")){
                Gson gsonBuilder = new GsonBuilder().setPrettyPrinting().create();
                gsonBuilder.toJson(outputClass, writer);
            } catch (IOException e){
                e.printStackTrace();
            }

            System.out.println("END!");

        } catch (FileNotFoundException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
