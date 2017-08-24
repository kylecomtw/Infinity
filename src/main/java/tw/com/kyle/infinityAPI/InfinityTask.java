package tw.com.kyle.infinityAPI;

public class InfinityTask {
    public enum StatusEnum { SUCCESS, FAILED, ERROR }
    private final StatusEnum status;
    private final String content;

    public InfinityTask(StatusEnum status, String content){
        this.status = status;
        this.content = content;
    }

    public InfinityTask(){
        this.status = StatusEnum.SUCCESS;
        this.content = "";
    }

    public StatusEnum getStatus(){
        return status;
    }

    public String getContent(){
        return content;
    }
}
