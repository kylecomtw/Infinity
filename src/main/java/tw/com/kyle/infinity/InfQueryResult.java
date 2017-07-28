package tw.com.kyle.infinity;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class InfQueryResult {
    List<String> subClasses = new ArrayList<>();
    List<String> superClasses = new ArrayList<>();
    List<String> equivalents = new ArrayList<>();
    List<String> instances = new ArrayList<>();

    public String ToJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
