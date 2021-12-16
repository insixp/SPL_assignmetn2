package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.objects.Model;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class PublishConferenceBroadcast implements Broadcast {
    Queue<Model> modelQueue;
    public PublishConferenceBroadcast(){
        modelQueue = new ArrayBlockingQueue<Model>(20);
    }
    public void addResult(Model model){
        this.modelQueue.add(model);
    }
}
