package mainMenu;

import Admin_pkg.AdminMenu;
import Common_pkg.Menu;
import Common_pkg.Paths;
import Common_pkg.Users;
import Student_pkg.StudentMenu;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Scanner;
import java.util.UUID;

 public class MainMenu extends Menu{

 public static Paths DataPath = new Paths();
    
 public static void main(String[] args) {
       Scanner input = new Scanner(System.in);
         while(true){
           new MainMenu().DisplayMenu(input); 
         }    
    }

 @Override
 public void DisplayMenu(Scanner input){
        System.out.println("========================================");
        System.out.println("| WELCOME TO LOST AND FOUND MANAGEMENT SYSTEM \t|");
        System.out.println("========================================");
        System.out.println("1. Login");
        System.out.println("2. Signup");
        System.out.println("3. Exit");
        System.out.println("========================================");
        handleInput(input);
 }
 
 @Override
 public void choiceOptionProcessing(Scanner input, int choice) {
    switch(choice){
        case 1: LoginScreen(input);
            break;
        case 2: Signup(input);
            break;
        case 3: System.out.println("Exiting...... Godbaye!");
                System.exit(0);
                break;
        default:System.out.println("Invalid choice, Please enter 1 or 2");
    }
}

 private static void LoginScreen(Scanner input) {
        System.out.print("\n\t ========================");
        System.out.println("\n---====<| Welcome to LoginScreen |>====---");
        System.out.println("\t ========================\n");
        System.out.print("Enter Username: ");
        String username = input.next();
        System.out.print("Enter Password: ");
        String password = input.next();  
        
        // Suppose we have two types of database users us=admin, pwd= 1234 , us =student, pwd=1234
        
        String utype = checkUser(input,username,password);
        
        if (utype.equalsIgnoreCase("admin")) {            
            AdminMenu aMenu = new AdminMenu();
            aMenu.DisplayMenu(input);
         }
        else if (utype.equalsIgnoreCase("student")) {
            StudentMenu sMenu = new StudentMenu();
            sMenu.DisplayMenu(input);
        }
        else{
            System.out.println("Invalid Useraname or password, Please Try again!");
            LoginScreen(input);
        }   
    }
 
 private static String checkUser(Scanner input,String username, String password) {
    String usertype = "";

     try{
         BufferedReader reader = new BufferedReader(new FileReader(DataPath.users_path));
         String line = reader.readLine();
                  
          while (line!=null) {
             String userInfo[] =line.split(",");
               if(userInfo[1].equalsIgnoreCase(username)&& userInfo[2].equalsIgnoreCase(password)){
                  
                   DataPath.UserID = userInfo[0];
                   
                   if(userInfo[3].equalsIgnoreCase("admin")){
                       usertype ="admin";
                   }
                   else{
                       usertype= "student";
                   }
               }         
              line = reader.readLine();
         }
        }
     catch(Exception e){}

     return usertype;
 }

 private void Signup(Scanner input) {
     String username,password;
     
     System.out.println("Enter Username: ");
     username = input.next();
     
     System.out.println("Enter Password: ");
     password = input.next();

     String UserID =UUID.randomUUID().toString().replace("-", "").substring(0,12);

     Users user = new Users(UserID, username, password,"student");
     user.addUser();
  }
 
}
