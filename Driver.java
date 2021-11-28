// Java program to illustrate
// Console readPassword() method
  
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
  
public class Driver {

    // declare console outside of method to access it in different methods
    private static Console cnsl;
    private static ArrayList<User> users = new ArrayList<>();

    // main menu presented when first time opening the program
    private static User inMainMenu() {

        // Welcome message
        System.out.println("\nWelcome to ...\n");

        boolean inMenu = true;
        User user = null; // set to null or new User(); what is better?

        while (inMenu) {
            // User choices
            System.out.println("1. Create an account.");
            System.out.println("2. Log into existing account.");
            System.out.println("3. Exit program.");
            
            // validate user input to be in between 1 and 3
            int choice = getChoiceFromUser();

            if (choice == 1) { // create user
                user = createUser();
                // if user wanted to go back to the menu createUser returns null
                if (user != null) {
                    users.add(user);
                    int result = storeUsers();
                    if (result == 1) { // success storing user
                        System.out.println("You have succesfully created an acccount and you are now logged in.");
                    } else { // failure
                        System.out.println("You are logged in, but we had trouble storing your account in the database.");
                    }
                    return user;
                }
            } else if (choice == 2) { // log in
                user = loginUser();
                // if user wanted to go back to the menu loginUser returns null
                if (user != null) {
                    System.out.println("You are successfully logged in.");
                    return user;
                }
            } else if (choice == 3){ // exit program
                System.out.println("See you soon:)");
                return null;
            } else { // should not happen just in case
                System.out.println("Something went wrong. Please start again.");
            }
        }
        return user;
    }
    
     // menu presented when a user is logged into the system
     public static void menuWhenLoggedIn(User user) {
                
        System.out.println("\nWhat would you like to do?\n");

        boolean inMenu = true;

        while (inMenu) {
            // User choices
            System.out.println("1. Do Whatever here");
            System.out.println("2. Log out.");
            System.out.println("3. Delete account.");
            
            // validate input to be an int between 1 and 3
            int choice = getChoiceFromUser();

            if (choice == 1) { 
                // do whatever
            } else if (choice == 2) { // log out
                user = null;
                System.out.println("You are logged out. Good bye.");
                inMenu = false;
            } else if (choice == 3){ // delete account and update database
                users.remove(user);
                user = null;
                int result = storeUsers();
                if (result == 1) { // success deleting user
                    System.out.println("We have deleted your account. Good bye!");
                } else {
                    System.out.println("We logged you out, but we had trouble deleting your account from our database.");
                }
                inMenu = false;
            } else { // in case validation method is flawed
                System.out.println("Something went wrong. Please start again.");
            }
        }
}

    private static User createUser() {
        System.out.println("\nCreate user");
        
        String email = ""; // make amil accessable outside of loop for later use
        Boolean gettingEmail = true;
        while (gettingEmail) { // get email from user
            email = cnsl.readLine("Enter E-Mail : ");
            
            // user can go back to the main menu by only pressing 1
            int choice = intFromString(email);
            if (choice == 1) {
                return null;
            }

            // check if email has valid syntax
            if (!email.contains("@") || email.length() < 8 || !email.contains(".") ) {
                System.out.println("Invalid E-Mail. Try again or press 1 to see the options again.\n");
            } else if (findUser(email) != null) { // check if user already exists
                System.out.println("User with that email already exists. Try another email or press 1 to see the main options again.\n");
            } else {
                // ask user to repeat email 
                String emailRepeat = cnsl.readLine("Repeat E-Mail : ");
                if (email.equals(emailRepeat)) {
                    gettingEmail = false;
                } else {
                    System.out.println("E-Mails do not match.\n Try again or press 1 to see the main options again.");
                }
            }
        }

        
        char[] password = null; // define password outside of loop of later use
        Boolean gettingPassword = true;
        while (gettingPassword) { // get password once email was verified
        
            System.out.println("\nPassword requirements:\n - between 8 and 15 symbols\n - at least one capital letter\n - at least one integer\n - no spaces\n");
            password = cnsl.readPassword("Enter password : "); // read password into character array
            
            // user can go back to the main menu by only pressing 1
            int choice = intFromString(String.valueOf(password));
            if (choice == 1) {
                return null;
            }

            // validate password
            if (!passwordIsValid(String.valueOf(password))) {
                System.out.println("Try again or press 1 to see the main options again.");
            } else {
                // force user to repeat password
                char[] passwordRepeat = cnsl.readPassword("Repeat password : ");
                if (Arrays.equals(password, passwordRepeat)) { // check if passwords match
                    passwordRepeat = null;
                    gettingPassword = false; // loop condition
                } else {
                    System.out.println("Passwords do not match. Try again or press 1 to see the main options again.");
                }
            }
        }

        // encrypt password
        String encryptedPassword = encryptPassword(String.valueOf(password));
        password = null;
        User user = new User(email, encryptedPassword); // create a new user

        return user;
    }

    private static User loginUser() {
        System.out.println("Log in user");
        boolean login = true;
        while (login) {
            String email = cnsl.readLine("\nEnter E-Mail : ");
            
            // user can go back to the main menu by only pressing 1
            int choice = intFromString(email);
            if (choice == 1) {
                return null;
            }

            // read passoword method will hide the input of the user on the screen
            char[] password = cnsl.readPassword("Enter password : "); // read passwor int character array

            // check if user exists with tht email
            User user = findUser(email);


            if (user != null) { // succesfull log in
                String decryptedPassword = decryptPassword(user.getPassword());
                if (decryptedPassword.equals(String.valueOf(password))) {
                    decryptedPassword = null;
                    password = null; // reset password 
                    return user;
                }
                password = null; // reset password
            }
            System.out.println("Log in failed. Try again or press 1 to see the main options again.");
        }
        return null;
    }

    // load the array list from text file using object input stream
    private static void loadUsers() {
        // try with resources -> no need to close
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("groceries.txt"))) { // indescriptive name for text file
            Object obj = inputStream.readObject();
            // Check it's an ArrayList
            if (obj instanceof ArrayList<?>) {
                // Get the List.
                ArrayList<?> al = (ArrayList<?>) obj;
                if (al.size() > 0) {
                    // Iterate.
                    for (int i = 0; i < al.size(); i++) {
                        Object o = al.get(i);
                        if (o instanceof User) {
                            User user = (User) o;
                            // use user
                            users.add(user);
                        }
                    }
                }
            } else {
                System.out.println("Database could not be found.");
            }
        } catch (Exception ex) {
            System.out.println("Database could not be found.");
        }
    }

    // store the arraylist of users in a text file using object output stream
    private static int storeUsers() {
        // try with resources -> stream does not need to be closed
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("groceries.txt"))) { // indescriptive name for text file
            outputStream.writeObject(users);
            return 1;
        } catch (Exception ex) {
            return -1;
        }
    }

    // find User by email address in database
    private static User findUser(String email) {
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        return null;
    }
  
    private static int getChoiceFromUser () {
        // error message
        String invalidChoice = "Invalid Choice. Please try again.";

        // boolean loop condition
        Boolean findChoice = true; 
        while (findChoice) {

            String input = cnsl.readLine("\nPlease enter the number of your choice : ");
            input = input.replaceAll("\\s", ""); // remove all spaces from input

            if (input == null) {
                System.out.println("Something went wrong. Please try again.");
            } else if (input.length() != 1) {
                System.out.println(invalidChoice);
            } else if (input.charAt(0) < '1' || input.charAt(0) > '3') {
                System.out.println(invalidChoice);
            } else {
                findChoice = false;
                return Integer.parseInt(input);
            }
        }
        return -1;
    }

    private static int intFromString(String s) {
        try {
            return Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return -1;
        }
    }

    private static boolean passwordIsValid(String password) { 
  
        // For checking if password length 
        // is between 8 and 15 
        if (password.length() < 8 || password.length() > 15){
            System.out.println("Your password does no meet length requirements.");
            return false; 
        }
    
        // To check space 
        if (password.contains(" ")) {
            System.out.println("Your password cannot contain any spaces.");
            return false; 
        }
    
        // Check digits from 0 to 9 
        int count = 0;
        for(int i = 0; i <= 9; i++) { 
                
            // To convert int to string 
            String str = String.valueOf(i); 
            if (password.contains(str)) {
                count += 1;
            } 
        }
        if (count == 0) {
            System.out.println("Your password must contain a number.");
            return false;
        }
        
        // Checking capital letters 
        // capital letters are from 65 to 09 in ascii table
        int counter = 65;
        while (counter <= 90) {
                // get char from ascii value 
                char capital = (char)counter;              
                if (password.contains(String.valueOf(capital))) {
                    counter = 100;
                } else {
                    counter ++;
                }
        } 
        if (counter == 91) {
            System.out.println("Your password must contain a capital letter.");
            return false;
        } 

        // If all conditions fails 
        return true; 
    }


    // needs to be completed
    private static String encryptPassword(String password) {
        return password;
    }

    // needs to be completed
    private static String decryptPassword(String password) {
        return password;
    }

    
    public static void main(String[] args) {
        
        // Create the console object to be able to hide password input on screen
        cnsl = System.console();

        // get the database from file
        loadUsers();
  
        // check if console was succesfully instaniated
        if (cnsl == null) {
            System.out.println("We apologize. System cannot run right now. Please come back later.");
            return;
        }

        // main loop while the user is working with the system
        boolean working = true;
        while (working) {
            // Mian menu lets the user decide if user wants to create an account,
            // log into an account or exit the program
            User user = inMainMenu();

            // user can only be null when user wants to exit the program
            if (user != null) {
                // menu when logged in gives option to sign out, delete account or
                // handle the sensitive data connected to that accoutn
                menuWhenLoggedIn(user);
            } else {
                working = false;
            }
        }

        System.out.println("The system is shut down and safe.");
        System.exit(0);
    }
}
