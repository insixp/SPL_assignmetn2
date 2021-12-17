package bgu.spl.mics.application.services;
import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

import java.util.List;

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
    private MessageBusImpl msgBus;
    Future<Model> workingModelFuture;
    private boolean active;

    public StudentService(String name, String department, Student.Degree status,int id) {
        super("Student: " + name);
        this.student = new Student(name, department,status,id);
        msgBus = MessageBusImpl.getInstance();
        workingModelFuture = null;
        this.active = false;
    }

    public StudentService(Student student) {
        super("Student: " + student.getName());
        this.student = student;
        msgBus = MessageBusImpl.getInstance();
    }

    public Student getStudent() { return this.student; }

    @Override
    protected void initialize() {
        msgBus.register(this);

        Callback<TickBroadcast> tickB = (TickBroadcast e)-> {
            Model currentWorkingModel = this.student.getCurrentWorkingModel();
            if(currentWorkingModel == null)
                this.student.nextModel();
            else {
                if(currentWorkingModel.getStatus().equals(Model.Status.PreTrained) & !this.active){
                    workingModelFuture = this.sendEvent(new TrainModelEvent(currentWorkingModel));
                    this.active = true;
                }
                if(currentWorkingModel.getStatus().equals((Model.Status.Trained)) & !this.active) {
                    workingModelFuture = this.sendEvent(new TestModelEvent(currentWorkingModel));
                    this.active = true;
                }
                if(currentWorkingModel.getStatus().equals((Model.Status.Tested)) & !this.active) {
                    if(currentWorkingModel.getResult().equals(Model.Result.Good))
                        workingModelFuture = this.sendEvent(new PublishResultsEvent(currentWorkingModel));
                    this.student.nextModel();
                    this.active = true;
                }
                if(this.workingModelFuture != null && this.workingModelFuture.isDone()) {
                    this.student.updateModelStatus(this.workingModelFuture.get().getStatus(), this.workingModelFuture.get().getResult());
                    this.active = false;
                }
            }
        };
        Callback<PublishConferenceBroadcast> publishB = (PublishConferenceBroadcast e)->{
            List<Model> publishedModelList = e.getModelList();
            List<Model> studentModelList = this.student.getModelList();
            for(int i = 0; i < publishedModelList.size(); i++){
                if(publishedModelList.get(i).getStudent().getId() == this.student.getId())
                    this.student.increasePublished();
                else
                    this.student.increaseRead();
            }
        };
        this.subscribeBroadcast(TickBroadcast.class, tickB);
        this.subscribeBroadcast(PublishConferenceBroadcast.class, publishB);
    }
}
