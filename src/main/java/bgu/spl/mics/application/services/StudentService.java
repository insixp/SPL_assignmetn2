package bgu.spl.mics.application.services;
import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Callback;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.PublishConferenceBroadcast;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.objects.Student;
import bgu.spl.mics.application.messages.PublishResultsEvent;

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

    public StudentService(String name, String department, Student.Degree status) {
        super("Student: " + name);
        this.student=new Student(name, department,status);
        // TODO Implement this

    }

    @Override
    protected void initialize() {
        // TODO Implement this

        MessageBusImpl.getInstance().register(this);
       // Class<Broadcast> e1= new PublishConferenceBroadcast();
       // this.subscribeBroadcast(e1, (PublishConferenceBroadcast e) ->{implement});


    }




}