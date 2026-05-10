

package Admin_pkg;

import Common_pkg.Feedback;
import Common_pkg.Item;
import Common_pkg.Menu;
import Common_pkg.Paths;
import Common_pkg.Users;
import Interface_pkg.Notification;
import Interface_pkg.manageItem;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class AdminMenu extends Menu{
    Paths DataPath = new Paths();
    @Override
    public void DisplayMenu(Scanner input){
        
        System.out.println("\n^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        System.out.println("|\tWelcome To Admin Campus Movie Nights   |");
        System.out.println("\n^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        System.out.println("|  \t <<->> YourID: " +DataPath.UserID + " <<->> \t|");
        System.out.println("1. View All Lost and Found Items");
        System.out.println("2. Manage Reported Lost Items");
        System.out.println("3. Manage Reported Found Items");
        System.out.println("4. Process Claims");
        System.out.println("5. View Feedback Reports");
        System.out.println("6. Send Notifications");
        System.out.println("7. Generate Reports");
        System.out.println("8. view All users");
        System.out.println("9. Exit");
        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        handleInput(input);
    }
    
    @Override
      public void choiceOptionProcessing(Scanner input, int choice) {
         switch(choice){
            case 1:                
                loadAllItems(input);
                DisplayMenu(input);
                break;
            case 2: 
                manageItem item=new manageItem();
                item.lostManageItem(input);
                break;
            case 3: 
                new manageItem().foundManageItem(input);
                break;
            case 4: claimItem(input);
                    DisplayMenu(input);  
                break;
            case 5:
                  ViewFeedbackReports(input);  
                    DisplayMenu(input);
                break;
            case 6: SendNotifications(input);   
                    DisplayMenu(input);
                break;
            case 7: 
                    GenerateReports(input); 
                    DisplayMenu(input);
                break; 
            case 8:
                     AllUsers(input);   
                    DisplayMenu(input);
                break;   
            case 9: 
                    System.out.println("Logout ...GodBye!"); 
                    System.exit(0);
                break;
          
            default:System.out.println("Invalid choice, Please enter 1 upto 9");
                    DisplayMenu(input);
        }
   }
    private void AllUsers(Scanner input) {
    List<Users> users = new ArrayList<>();  
     String userData_path = "src\\Common_pkg\\users.txt";
     Users user = new Users();
     users = user.loadAllUsers(userData_path);
     
     for(Users usr:users){
         System.out.println(usr.DisplayFormat());
     }
    }
    public void loadAllItems(Scanner input) {
        List<Item> Items = new ArrayList<>();  
     String mData_path = "src\\Common_pkg\\items.txt";
     Item mov = new Item();
     Items = mov.loadAllItems(mData_path);
     
     for(Item m:Items){
         System.out.println(m.DisplayFormat());
    }
    }

    private void ViewFeedbackReports(Scanner input) {
    try (BufferedReader reader = new BufferedReader(new FileReader(DataPath.feedback_path))) {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            Feedback f = new Feedback(parts[0], parts[1], parts[2]);
            System.out.println(f.toDisplayFormat());
        }
    } catch (Exception e) {
        System.out.println("Failed to load feedback.");
    }
}

   private void SendNotifications(Scanner input) {
    input.nextLine();
    System.out.println("Enter User ID:");
    String userID = input.nextLine();
    System.out.println("Enter Message:");
    String message = input.nextLine();

    String id = UUID.randomUUID().toString().substring(0, 10);
    Notification note = new Notification(id, userID, message);

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(DataPath.notification_path, true))) {
        writer.write(note.toFileFormat());
        writer.newLine();
        System.out.println("Notification sent.");
    } catch (Exception e) {
        System.out.println("Failed to send notification.");
    }
}

    private void GenerateReports(Scanner input) {
    input.nextLine();
    System.out.println("Enter Report Name:");
    String reportName = input.nextLine();
    System.out.println("Enter Report Content:");
    String content = input.nextLine();

    try (BufferedWriter writer = new BufferedWriter(new FileWriter("src\\Common_pkg\\" + reportName + ".txt"))) {
        writer.write(content);
        System.out.println("Report generated: " + reportName + ".txt");
    } catch (Exception e) {
        System.out.println("Failed to generate report.");
    }
}

   private void claimItem(Scanner input) {
    input.nextLine();  // Clear the buffer
    System.out.print("Enter the ID of the item you want to claim: ");
    String claimId = input.nextLine();

    Item itemLoader = new Item();
    List<Item> items = itemLoader.loadAllItems(DataPath.items_path);
    boolean itemFound = false;

    for (Item item : items) {
        if (item.itemId.equalsIgnoreCase(claimId)) {
            itemFound = true;
            if (item.status.equalsIgnoreCase("claimed")) {
                System.out.println("This item has already been claimed.");
            } else {
                item.status = "claimed";
                item.ownerId = DataPath.UserID;  
                System.out.println("You have successfully claimed the item: ");
                System.out.println(item.DisplayFormat());
            }
            break;
        }
    }

    if (!itemFound) {
        System.out.println("Item with the given ID was not found.");
    } else {
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DataPath.items_path))) {
            for (Item item : items) {
                writer.write(item.InsertFormat());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error updating item status.");
        }
    }
}


}
   

   

   
    
 