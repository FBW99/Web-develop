
package Interface_pkg;

import Admin_pkg.AdminMenu;
import Common_pkg.Item;
import java.time.LocalDate;
import java.util.Scanner;
import java.util.UUID;

public class manageItem {
    public void lostManageItem(Scanner input){
        System.out.println("\n^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        System.out.print("|\t  Welcome to Lost Manage Item Screen\t|");
        System.out.println("\n^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        System.out.println("1.Add lost item.");
        System.out.println("2.Update the status of lost item.");
        System.out.println("3.Delete last lost item.");
        System.out.println("4.Back. ");
        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        System.out.print("Enter your choice: ");
        int Report_choice = input.nextInt();
        switch(Report_choice){
            case 1: 
                    AddNewItem(input);
                    lostManageItem(input);
                break;
            case 2:
                     updateItem(input); 
                    lostManageItem(input);
                break;
            case 3:
                    DeleteItem (input);
                    lostManageItem(input);
                break;
            case 4:   
                 new AdminMenu().DisplayMenu(input);
                break;
            default:
                System.out.print("Invalid choice, Please enter 1 upto 4");
                    lostManageItem(input);
        }
    }
   public void foundManageItem(Scanner input){
       System.out.println("\n^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        System.out.print("|\t  Welcome to Found Manage Item Screen\t|");
        System.out.println("\n^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        System.out.println("1.Add found item reported .");
        System.out.println("2.Update the status of found.");
        System.out.println("3.Delete last found item reported.");
        System.out.println("4.Back. ");
        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        System.out.print("Enter your choice: ");
        int Report_choice = input.nextInt();
        switch(Report_choice){
            case 1: 
                    AddNewItem(input);
                    foundManageItem(input);
                break;
            case 2:
                    updateItem(input); 
                    foundManageItem(input);
                break;
            case 3:  
                    DeleteItem (input);
                    foundManageItem(input);
                break;
            case 4:   
                 new AdminMenu().DisplayMenu(input);
                break;
            default:
                System.out.print("Invalid choice, Please enter 1 upto 4");
                    foundManageItem(input);
        } 
 
   }

    private void AddNewItem(Scanner input) {
          System.out.print("Enter Item ID: ");
                  String itemId = input.nextLine();
                  input.nextLine(); 

                 System.out.print("Enter Name: ");
                 String name = input.nextLine();

                 System.out.print("Enter Description: ");
                 String description = input.nextLine();

                 System.out.print("Enter Category: ");
                 String category = input.nextLine();
                     
                 System.out.print("Enter Status: ");
                 String status = input.nextLine();

                 System.out.print("Enter location lost/found: ");
                 String locationLost = input.nextLine();
                    
                 System.out.println("Enter your ID: ");
                 String reporterId=input.nextLine();
                    
                 System.out.println("Enter your Contact information: ");
                 String contactInfo=input.nextLine();
                    
                  
                 String itemID=UUID.randomUUID().toString();
                    
                 Item item=new Item(itemID,name,description,category,status,locationLost,reporterId,contactInfo,LocalDate.now());
                  item.addItem(); 
    }
    

    private void DeleteItem(Scanner input) {
        System.out.println("Enter ID of the Item");
        String mID=input.next();
        Item ite=new Item();
        ite.DeleteItem(mID);
    }    

    private void updateItem(Scanner input) {
        input.nextLine();
        System.out.println("Enter Item ID");
        String mID = input.nextLine();
        
        System.out.println("Enter Field to update");
        String field=input.nextLine();
        
        System.out.println("Enter new Value");
        String fieldNewValue=input.nextLine();
        
        Item ite=new Item();
        ite.UpdateItem(mID,field,fieldNewValue);
        
    
    }
}


