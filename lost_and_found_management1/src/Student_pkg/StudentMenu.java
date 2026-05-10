
package Student_pkg;

import Admin_pkg.AdminMenu;
import Common_pkg.Feedback;
import Common_pkg.Item;
import Common_pkg.lostItem;
import Common_pkg.Menu;
import Common_pkg.Paths;
import Common_pkg.foundItem;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class StudentMenu extends Menu {
    Paths DataPath = new Paths();
  @Override
  public void  DisplayMenu(Scanner input){
      System.out.println("\n^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^"); 
      System.out.print("|   Welcome To Student Campus Movie Nights\t|");       
      System.out.println("\n^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");    
      System.out.print("|  \t <<->> YourID: " +DataPath.UserID + " <<->> \t|");
      System.out.println("\n^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
      System.out.println("1. view all lost and found item");
      System.out.println("2. Report a lost item");
      System.out.println("3. Report a found item");
      System.out.println("4. Search for an item");
      System.out.println("5. Claim a lost item");
      System.out.println("6. provide Feedback Reports");
      System.out.println("7. View found items and owners");
      System.out.println("8. Exit");
      handleInput(input);
      
  }  
  @Override
  public void choiceOptionProcessing(Scanner input,int choice){

       switch(choice){
            case 1:
                AdminMenu smenu=new AdminMenu();
                smenu.loadAllItems(input);
                DisplayMenu(input);
                break;
            case 2:
                lostItem lost=new lostItem();
                lost.lostItems(input);
                break;
            case 3:   
                foundItem found=new foundItem();
                 found.foundItems(input);
                break;
            case 4:
                    searchItem(input);
                    DisplayMenu(input);      
                break;
            case 5:
                     claimItem(input); 
                    DisplayMenu(input);            
                break;
            case 6: 
                    provideFeedback(input);
                    DisplayMenu(input);  
                break;
            case 7: viewFoundItemsAndOwners(input);  
                    DisplayMenu(input);
                break;
            case 8:
                System.out.println("Logout ....Godbaye!");
                System.exit(0);
           
                break;
            default:System.out.println("Invalid choice, Please enter 1 upto 8");
                    DisplayMenu(input);
        }
   
    }

     private void searchItem(Scanner input) {
        input.nextLine();
        System.out.print("Enter item name or ID to search: ");
        String keyword = input.nextLine().toLowerCase();

        Item ite = new Item();
        List<Item> items = ite.loadAllItems(DataPath.items_path);
        boolean found = false;
        for (Item m : items) {
            if (m.name.toLowerCase().contains(keyword)||m.itemId.contains(keyword)) {
                System.out.println(m.DisplayFormat());
                found = true;
            }
        }
        if (!found) {
            System.out.println("No matching item found.");
        }
    }

    private void provideFeedback(Scanner input) {
        input.nextLine();
        System.out.print("Enter Your User ID: ");
        String userID = input.nextLine();
        System.out.print("Enter Your Feedback: ");
        String comment = input.nextLine();
        String id = UUID.randomUUID().toString().substring(0, 10);
        Feedback feedback = new Feedback(id, userID, comment);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DataPath.feedback_path, true))) {
            writer.write(feedback.toFileFormat());
            writer.newLine();
            System.out.println("Thank you for your feedback!");
        } catch (IOException e) {
            System.out.println("Error saving feedback.");
        }
    }

    private void claimItem(Scanner input) {
    input.nextLine(); // Clear the buffer
    System.out.print("Enter the ID of the item you want to claim: ");
    String claimId = input.nextLine();

    Item itemLoader = new Item();
    List<Item> items = itemLoader.loadAllItems(DataPath.items_path);
    boolean itemFound = false;

    for (Item item : items) {
        if (item.itemId.equalsIgnoreCase(claimId)) {
            itemFound = true;
            if (item.status != null && item.status.equalsIgnoreCase("claimed")) {
                System.out.println("This item has already been claimed.");
            } else {
                item.status = "claimed";
                item.ownerId = DataPath.UserID;
                System.out.println("✅ You have successfully claimed the item:");
                System.out.println(item.DisplayFormat());
            }
            break;
        }
    }

    if (!itemFound) {
        System.out.println("❌ Item with the given ID was not found.");
    } else {
        // Save updated list back to file
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

  private void viewFoundItemsAndOwners(Scanner input) {
    Item itemLoader = new Item();
    List<Item> items = itemLoader.loadAllItems(DataPath.items_path);
    boolean anyFound = false;

    System.out.println("\n--- Found Items and Their Owners ---");

    for (Item item : items) {
        if ("claimed".equalsIgnoreCase(item.status) && item.itemId != null && !item.itemId.isEmpty()) {
            System.out.println(item.DisplayFormat());
            anyFound = true;
        }
    }

    if (!anyFound) {
        System.out.println("No claimed items found.");
    }
}



     
    
    }
    





