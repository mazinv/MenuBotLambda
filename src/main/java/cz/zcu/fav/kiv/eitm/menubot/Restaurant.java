package cz.zcu.fav.kiv.eitm.menubot;

import java.util.ArrayList;
import java.util.List;

public class Restaurant {

    String name;
    List<String> menu;
    String menuString;

    public Restaurant(String name, int menuSize) {
        this.name = name;
        this.menu = new ArrayList<>(menuSize);
        this.menuString = null;
    }

    public void createMenuString(StringBuilder sb) {
        for (int i = 0; i < this.menu.size(); i++) {
            sb.append(this.menu.get(i));
            if (i != this.menu.size() -1)
                sb.append(" ••• ");
        }

        this.menuString = sb.toString();
        sb.setLength(0);
    }
}