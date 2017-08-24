package tw.com.kyle.infinityAPI;

import tw.com.kyle.infinityAPI.InfinityTask.StatusEnum;

import java.util.ArrayList;
import java.util.List;

public class InfinityOntoInfo {
    private List<String> classes = new ArrayList<>();
    private List<String> properties = new ArrayList<>();
    private List<String> individuals = new ArrayList<>();
    private StatusEnum status;


    public static InfinityOntoInfo EmptyInfo(){
        InfinityOntoInfo info = new InfinityOntoInfo(null, null, null);
        info.status = StatusEnum.ERROR;
        return info;
    }

    public InfinityOntoInfo(List<String> c, List<String> p, List<String> i){
        this.classes = c;
        this.properties = p;
        this.individuals = i;
        this.status = StatusEnum.SUCCESS;
    }

    public StatusEnum getStatus() {
        return status;
    }

    public List<String> getProperties() {
        return properties;
    }

    public List<String> getIndividuals() {
        return individuals;
    }

    public List<String> getClasses(){
        return classes;
    }


}
