import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
  
public class Driver {

    // declare console outside of main method to access it in different methods
    private static Console cnsl;
    private static ArrayList<User> users = new ArrayList<>();
    private static String errorMessage = "Try again or enter 'b' to go back.";
    private static final String secretKey = "ssshhhhhhhhhhh!!!!";

    // main menu presented when first time opening the program
    private static User inMainMenu() {

        // Welcome message
        System.out.println("\nWelcome to your favorite money app...");

        boolean inMenu = true;
        User user = null; // set to null or new User(); what is better?

        while (inMenu) {
            // User choices
            System.out.println("\n1. Create an account.");
            System.out.println("2. Log into existing account.");
            System.out.println("3. Exit program.");
            
            // validate user input to be in between 1 and 3
            int choice = getChoiceFromUser('3');

            if (choice == 1) { // create user
                user = createUser();
                // if user wanted to go back to the menu createUser returns null
                if (user != null) {
                    users.add(user);
                    if (storeUsers()) { // success storing user
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
                System.out.println("\nWe hope to see you soon again!");
                return null;
            } else { // should not happen just in case
                System.out.println("Something went wrong. Please start again.");
            }
        }
        return user;
    }
    
    private static void addMoney (User user) {

        System.out.println("\nPlease get an employer to verify the amount of money you want to add.");

        boolean verifying = true;
        while (verifying) {
            char[] password = cnsl.readPassword("Enter employee password : "); // read password into character array
            // read password from file
            String decryptedPassword = AES.decrypt("xCjlaIOc56fpAFXUlGsv0g==", secretKey);
            if (decryptedPassword.equals(String.valueOf(password))) {
                verifying = false;
            } else {
                System.out.println("Password failed. " + errorMessage + "\n");
            }
        }

        boolean addingMoney = true;
        while (addingMoney) {
            String line = cnsl.readLine("Amount of money to add : ");
            if (line.length() == 1 && line.contains("b")) {
                System.out.println();
                addingMoney = false;
            } else {
                try {
                    Double money = Double.parseDouble(line);
                    if (!user.add(money)) {
                        System.out.println("Adding the money would exceed the maximum allowed amount. " + errorMessage + "\n");
                    } else if (money < 0) {
                        System.out.println("You need to enter a positive amount. " + errorMessage + "\n");
                    } else {
                        if (storeUsers()) {
                            System.out.println("Account updated.\n");
                            addingMoney = false;
                        } else {
                            System.out.println("Unable to add money right now.");
                            addingMoney = false;
                        }
                    }
                } catch(NumberFormatException ex) {
                    System.out.println("Unaccepted input. " + errorMessage + "\n");
                }
            }
        }
    }

    private static void withdraw (User user) {
        boolean verifying = true;
        while (verifying) {
            String line = cnsl.readLine("How much do you want to withdraw : "); 
            
            if (line.length() == 1 && line.contains("b")) {
                System.out.println();
                verifying = false;
            } else {
                try {
                    Double money = Double.parseDouble(line);
                    if (!user.subtract(money)) {
                        System.out.println("You do not have that much money on your account " + errorMessage + "\n");
                    } else if (money < 0) {
                        System.out.println("You need to enter a positive amount. " + errorMessage + "\n");
                    } else {
                        if (storeUsers()) {
                            System.out.println("Account updated.");
                            verifying = false;
                        } else {
                            System.out.println("Unable to withdraw money. " + errorMessage + "\n");
                        }
                    }
                } catch(NumberFormatException ex) {
                    System.out.println("Unaccepted input. " + errorMessage + "\n");
                }
            }
        }
    }

    // menu presented when a user is logged into the system
    private static void menuWhenLoggedIn(User user) {
                
        System.out.println("\nWhat would you like to do?\n");

        boolean inMenu = true;

        while (inMenu) {
            // User choices
            System.out.println("1. Withdraw money.");
            System.out.println("2. Put money on my account.");
            System.out.println("3. See my balance.");
            System.out.println("4. Log out.");
            System.out.println("5. Delete account and automatically donate left over money on your account.");
            
            // validate input to be an int between 1 and 5
            int choice = getChoiceFromUser('5');

            if (choice == 1) {
                withdraw(user);
            } else if (choice == 2) {
                addMoney(user);
            } else if (choice == 3) {
                System.out.printf("You have $ %.2f on your account.\n\n", user.getData());
            } else if (choice == 4) { // log out
                user = null;
                System.out.println("You are logged out. You are data is safe. Good bye.");
                inMenu = false;
            } else if (choice == 5){ // delete account and update database
                users.remove(user);
                if (storeUsers()) { // success deleting user
                    System.out.println("\nWe have deleted your account and all your data. Good bye!");
                    System.out.println("Your leftover money $" + user.getData() + " was donated.");
                } else {
                    System.out.println("We logged you out, but we had trouble deleting your account from our database.");
                }
                inMenu = false;
                user = null;
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
            
            // user can go back to the main menu by only pressing "b"
            if (email.length() == 1 && email.contains("b")) {
                return null;
            } else {
                // check if email has valid syntax
                if (!email.contains("@") || email.length() < 8 || !email.contains(".") ) {
                    System.out.println("Invalid E-Mail. " + errorMessage + "\n");
                } else if (findUser(email) != null) { // check if user already exists
                    System.out.println("User with that email already exists. " + errorMessage + "\n");
                } else {
                    // ask user to repeat email 
                    String emailRepeat = cnsl.readLine("Repeat E-Mail : ");
                    if (email.equals(emailRepeat)) {
                        gettingEmail = false;
                    } else {
                        System.out.println("E-Mails do not match.\n");
                        System.out.println(errorMessage);
                    }
                }
            }
        }

        
        char[] password = null; // define password outside of loop of later use
        Boolean gettingPassword = true;
        while (gettingPassword) { // get password once email was verified
        
            System.out.println("\nPassword requirements:\n - between 8 and 15 symbols\n - at least one capital letter\n - at least one integer\n - no spaces\n");
            password = cnsl.readPassword("Enter password : "); // read password into character array
            
            // user can go back to the main menu by only pressing "b"
            if (password.length == 1 && password[0] == 'b') {
                return null;
            } else {
                // validate password
                if (!passwordIsValid(String.valueOf(password))) {
                    System.out.println(errorMessage);
                } else {
                    // force user to repeat password
                    char[] passwordRepeat = cnsl.readPassword("Repeat password : ");
                    if (Arrays.equals(password, passwordRepeat)) { // check if passwords match
                        passwordRepeat = null;
                        gettingPassword = false; // loop condition
                    } else {
                        System.out.println("Passwords do not match. " + errorMessage + "\n");
                    }
                }
            }
        }

        // encrypt password
        String encryptedPassword = AES.encrypt(String.valueOf(password), secretKey);
        password = null;
        User user = new User(email, encryptedPassword); // create a new user

        return user;
    }

    private static User loginUser() {
        System.out.println("\nLog in user");
        boolean login = true;
        while (login) {
            String email = cnsl.readLine("Enter E-Mail : ");
            
            // user can go back to the main menu by only pressing 'b'
            if (email.length() == 1 && email.contains("b")) {
                return null;
            } else {
                // read passoword method will hide the input of the user on the screen
                char[] password = cnsl.readPassword("Enter password : "); // read passwor int character array

                // check if user exists with that email
                User user = findUser(email);


                if (user != null) { // succesfull log in
                    String decryptedPassword = AES.decrypt(user.getPassword(), secretKey) ;
                    if (decryptedPassword.equals(String.valueOf(password))) {
                        decryptedPassword = null;
                        password = null; // reset password 
                        return user;
                    }
                    password = null; // reset password
                }
                System.out.println("Log in failed. " + errorMessage + "\n");
            }
        }
        return null;
    }

    // load the array list from text file using object input stream
    private static void loadUsers() {
        // try with resources -> no need to close
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("groceries.txt"))) {
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
            }
        } catch (Exception ex) {
            System.out.println("Database could not be found.");
        }
    }

    // store the arraylist of users in a text file using object output stream
    private static boolean storeUsers() {
        // try with resources -> stream does not need to be closed
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("groceries.txt"))) {
            outputStream.writeObject(users);
            return true;
        } catch (Exception ex) {
            return false;
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
  
    private static int getChoiceFromUser (char choices) {
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
            } else if (input.charAt(0) < '1' || input.charAt(0) > choices) {
                System.out.println(invalidChoice);
            } else {
                findChoice = false;
                return Integer.parseInt(input);
            }
        }
        return -1;
    }

    private static boolean passwordIsValid(String password) { 
  
        // For checking if password length 
        // is between 8 and 15 
        if (password.length() < 8 || password.length() > 15){
            System.out.println("Your password has not met the length requirements.");
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
