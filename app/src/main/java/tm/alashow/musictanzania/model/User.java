package tm.alashow.musictanzania.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by pdahasla on 12/23/2016.
 */

@IgnoreExtraProperties
public class User {

    public String name;
    public String artist;

    public String downloadUrl;
    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email,String d) {
        this.name = username;
        this.artist = email;
        this.downloadUrl=d;
    }

}