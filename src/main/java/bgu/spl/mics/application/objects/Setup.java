package bgu.spl.mics.application.objects;

import java.util.Deque;

public class Setup {
    public class sStudent{
        public String name;
        public String department;
        public String status;
        public Deque<sModel> models;
    }

    public class sModel{
        public String name;
        public String type;
        public int size;
    }

    public Deque<sStudent> Students;
    public Deque<String> GPUS;
    public Deque<Integer> CPUS;
    public Deque<ConfrenceInformation> Conferences;
    public int TickTime;
    public int Duration;

    Setup(){}
}