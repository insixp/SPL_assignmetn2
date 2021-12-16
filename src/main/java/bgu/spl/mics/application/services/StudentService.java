package bgu.spl.mics.application.services;
import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

/**
 * Student is responsible for sending the {@link TrainModelEvent},
 * {@link TestModelEvent} and {@link PublishResultsEvent}.
 * In addition, it must sign up for the conference publication broadcasts.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class StudentService extends MicroService {
    Student student;
    private MessageBusImpl messegebus;
    public StudentService(String name, String department, Student.Degree status,int id) {
        super("Student: " + name);
        this.student = new Student(name, department,status,id);
        messegebus=MessageBusImpl.getInstance();
    }

    public StudentService(Student student) {
        super("Student: " + student.getName());
        this.student = student;
        messegebus=MessageBusImpl.getInstance();
    }

    @Override
    protected void initialize() {
        messegebus.register(this);

        Callback<TickBroadcast> tickB= e-> {
            if(this.student.canAct())
                sendEvent(this.student.sendToTrain());
            if(this.student.canTest()) {
                sendEvent(this.student.sendToTest());
                }
            if(this.student.canPublish){
                sendEvent(this.student.toPublish());
            }
        };
        Callback<PublishConferenceBroadcast> publishB =e->{
            Model m ;
            for(int i=0;i<e.getModelList().size();i++){
                m =e.getModelList().get(i);
                if(m.getStudent().getId()==this.student.getId())
                    this.student.incPublications();
                else
                    this.student.incPapersRead();
            }


        };
        this.subscribeBroadcast(TickBroadcast.class,tickB);
        this.subscribeBroadcast(PublishConferenceBroadcast.class,publishB);
    }








}
