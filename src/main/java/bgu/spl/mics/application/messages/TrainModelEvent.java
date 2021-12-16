package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Data;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

public class TrainModelEvent implements Event<Model> {

    private Model model;
    public TrainModelEvent(Model model){
    this.model=model;
    }
    public Model getModel(){
        return this.model;
    }
}

