package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.objects.Model;

import java.util.ArrayList;
import java.util.List;


public class PublishConferenceBroadcast implements Broadcast {
    private List<Model> modelList;
    public PublishConferenceBroadcast( List<Model> modelList){
        this.modelList = modelList;
    }
    public List<Model> getModelList(){
        return modelList;
    }
}
