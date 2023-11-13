package base;

public class User {
    public String username;
    public int gender;
    public int streak;

    public User() {
        username = null;
        gender = -1;
        streak = 0;
    }
    public User(String name, int gend, int str) {
        username = name;
        gender = gend;
        streak = str;
    }
}
