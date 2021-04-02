package cz.zcu.fav.kiv.eitm.menubot;

import java.util.ArrayList;
import java.util.List;

public class Restaurant {

    String name;
    String gsm;
    List<String> menu;
    String menuString;

    public Restaurant(String name, String gsm, int menuSize) {
        this.name = name;
        this.gsm = gsm;
        this.menu = new ArrayList<>(menuSize);
        this.menuString = null;
    }

    public void createMenuString(StringBuilder sb) {
        for (String menu : this.menu) {
            sb.append(menu);
            sb.append("# ");
        }

        this.menuString = sb.toString();
        sb.setLength(0);
    }
}