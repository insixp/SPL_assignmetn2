package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.objects.Model;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class PublishConferenceBroadcast implements Broadcast {
    private List<Model> modelList;
    public PublishConferenceBroadcast(){
        this.modelList=new ArrayList<Model>();
    }
    public void addResult(Model model){
        this.modelList.add(model);
    }
    public List<Model> getModelList(){
        return modelList;
    }
}
