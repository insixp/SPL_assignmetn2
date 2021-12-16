package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Data {
    /**
     * Enum representing the Data type.
     */
    public enum Type {
        Images, Text, Tabular
    }

    private Type type;
    private int processed;
    private int size;

    public Data(Type type, int size){
        this.type = type;
        this.processed = 0;
        this.size = size;
    }

    public Type getType(){ return this.type; }
    public int getProcessed() { return this.processed; }
    public int getSize() { return this.size; }
    public int getTicksToProcess(){
        if(this.type == Type.Images)
            return 4;
        if(this.type == Type.Text)
            return 2;
        if(this.type == Type.Tabular)
            return 1;
        return -1;
    }
    public static Type stringToType(String type){
        if(type.toLowerCase() == "images")
            return Type.Text;
        if(type.toLowerCase() == "text")
            return Type.Text;
        if(type.toLowerCase() == "tabular")
            return Type.Tabular;
        return null;
    }

    public void incProcessed(int size){
        this.processed += size;
    }
}
