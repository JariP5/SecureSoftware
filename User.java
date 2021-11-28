import java.io.Serializable;

public class User implements Serializable {

    private String password;
    private String email;
    private String data;
    
    public User() {
        this.password = "Not set";
        this.email = "Not set";
        this.data = "Whatever here";
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
        this.data = "Whatever here";
    }

    public String getPassword() {
        return this.password;
    }
    public String getEmail(){
        return this.email;
    }
    public String getData(){
        return this.data;
    }

}
