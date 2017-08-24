package tw.com.kyle.infinityAPI;

import tw.com.kyle.infinity.InfQueryResult;
import tw.com.kyle.infinityAPI.InfinityTask.StatusEnum;

public class InfinityQueryResult {
    private final StatusEnum status;
    private final InfQueryResult result;

    public InfinityQueryResult(StatusEnum status, InfQueryResult result){
        this.status = status;
        this.result = result;
    }

    public StatusEnum getStatus(){
        return status;
    }

    public InfQueryResult getResult() {
        return result;
    }
}
