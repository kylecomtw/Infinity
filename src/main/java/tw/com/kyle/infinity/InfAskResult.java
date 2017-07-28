package tw.com.kyle.infinity;

import com.google.gson.Gson;

public class InfAskResult {
    public boolean isSatisfiable;
    public String ToJson(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
