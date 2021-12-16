package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Model;
public class PublishResultsEvent implements Event<String> {

    private Model model;
    PublishResultsEvent(String name){
        this.model=model;
    }
    public Model getModel(){
        return this.model;
    }
}
