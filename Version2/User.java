import java.io.Serializable;

public class User implements Serializable {

    private String password;
    private String email;
    private Double data;
    private static final Double max = 1000000.00;
    
    public User() {
        this.password = "Not set";
        this.email = "Not set";
        this.data = 0.0;
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
        this.data = 0.0;
    }

    public String getPassword() {
        return this.password;
    }
    public String getEmail(){
        return this.email;
    }
    public Double getData(){
        return this.data;
    }

    public boolean add(Double x) {
        if (this.data >= Double.MAX_VALUE - x) {
            return false;
        }
        
        if (this.data + x <= max) {
            this.data += x;
            return true;
        } else {
            return false;
        }
        
    }

    public boolean subtract(Double x) {
        if (x <= this.data) {
            this.data -= x;
            return true;
        } else {
            return false;
        }
    } 



}
