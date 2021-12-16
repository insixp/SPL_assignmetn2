package bgu.spl.mics.application.services;
import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Callback;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
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
    public StudentService(String name, String department, Student.Degree status) {
        super("Student: " + name);
        this.student = new Student(name, department,status);
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
            if(this.student.canTrain())
                sendEvent(this.student.sendToTrain());
            if(this.student.canTest()) {
                sendEvent(this.student.sendToTest());
                }
            if(this.student.canPublish){
                sendEvent(this.student.toPublish());
            }
        };

        this.subscribeBroadcast(TickBroadcast.class,tickB);
    }








}