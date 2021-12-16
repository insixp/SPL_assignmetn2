package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.objects.Model;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;

public class PublishConferenceBroadcast implements Broadcast {
    Queue<Model> modelQueue;
    public PublishConferenceBroadcast(){
        modelQueue=new Queue<Model>() {
            @Override
            public boolean add(Model model) {
                return false;
            }

            @Override
            public boolean offer(Model model) {
                return false;
            }

            @Override
            public Model remove() {
                return null;
            }

            @Override
            public Model poll() {
                return null;
            }

            @Override
            public Model element() {
                return null;
            }

            @Override
            public Model peek() {
                return null;
            }

            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean contains(Object o) {
                return false;
            }

            @Override
            public Iterator<Model> iterator() {
                return null;
            }

            @Override
            public Object[] toArray() {
                return new Object[0];
            }

            @Override
            public <T> T[] toArray(T[] a) {
                return null;
            }

            @Override
            public boolean remove(Object o) {
                return false;
            }

            @Override
            public boolean containsAll(Collection<?> c) {
                return false;
            }

            @Override
            public boolean addAll(Collection<? extends Model> c) {
                return false;
            }

            @Override
            public boolean removeAll(Collection<?> c) {
                return false;
            }

            @Override
            public boolean retainAll(Collection<?> c) {
                return false;
            }

            @Override
            public void clear() {

            }
        };
    }
    public void addResult(Model model){
        this.modelQueue.add(model);
    }
}
