package base;

import java.util.Date;

public class User {
    public String username;
    public int gender;
    public String streak;
    public boolean streakOn;
    public User() {
        username = null;
        gender = -1;
        streak = null;
        streakOn = false;
    }
    public User(String name, int gend, String Date, boolean StreakOn) {
        username = name;
        gender = gend;
        streak = Date;
        streakOn = StreakOn;
    }
}
