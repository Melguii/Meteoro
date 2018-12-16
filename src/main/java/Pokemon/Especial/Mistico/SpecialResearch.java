package Pokemon.Especial.Mistico;

import java.util.ArrayList;
import java.util.List;

public class SpecialResearch {

    private String name;

    @Override
    public String toString() {
        return "SpecialResearch{" +
                "name='" + name + '\'' +
                ", quests=" + quests +
                '}';
    }

    private List<Quest> quests;

    public SpecialResearch() {
        this.quests = new ArrayList<Quest>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Quest> getQuests() {
        return quests;
    }

    public void setQuests(List<Quest> quests) {
        this.quests = quests;
    }

}