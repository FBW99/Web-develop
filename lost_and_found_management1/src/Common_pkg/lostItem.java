package Common_pkg;

import Student_pkg.StudentMenu;
import java.time.LocalDate;
import java.util.Scanner;
import java.util.UUID;

public class lostItem extends Item {
   
    private String location;
    private String reporterId;
    private String contactInfo;

    public lostItem() {}

    public lostItem(String itemId, String name, String description, String category, String status, String location, String reporterId, String contactInfo, LocalDate dateReported) {
        super(itemId, name, description, category, status, location, reporterId, contactInfo, dateReported);
        
        this.location = location;
        this.reporterId = reporterId;
        this.contactInfo = contactInfo;
    }


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getReporterId() {
        return reporterId;
    }

    public void setReporterId(String reporterId) {
        this.reporterId = reporterId;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

 
    public void lostItems(Scanner input) {
        System.out.println("| Welcome to Lost Items Page |");
        System.out.println("1. Report a new lost item");
        System.out.println("2. View and edit your report");
        System.out.println("3. Back");
        System.out.println("4. Exit");
        System.out.print("Enter your choice: ");

        int choice = input.nextInt();
        input.nextLine(); // consume leftover newline

        switch (choice) {
            case 1:
                reportLostItem(input);
                break;
            case 2:
                editItem(input);
                break;
            case 3:
                new StudentMenu().DisplayMenu(input);
                break;
            case 4:
                System.exit(0);
                break;
            default:
                System.out.println("❌ Invalid option. Try again.");
                lostItems(input);
        }
    }

    private void reportLostItem(Scanner input) {
        System.out.print("Enter Item Name: ");
        String name = input.nextLine();

        System.out.print("Enter Description: ");
        String description = input.nextLine();

        System.out.print("Enter Category: ");
        String category = input.nextLine();

        System.out.print("Enter Status: ");
        String status = input.nextLine();

        System.out.print("Enter Location: ");
        String location = input.nextLine();

        System.out.print("Enter Your ID (Reporter ID): ");
        String reporterId = input.nextLine();

        System.out.print("Enter Contact Information: ");
        String contactInfo = input.nextLine();

        String itemID = UUID.randomUUID().toString().substring(0, 8);
        LocalDate reportedDate = LocalDate.now();

        Item item = new Item(itemID, name, description, category, status, location, reporterId, contactInfo, reportedDate);
        item.addItem();

        System.out.println("✅ Lost item reported successfully.");
    }

    public void editItem(Scanner input) {
        input.nextLine(); 

        System.out.print("Enter Item ID to edit: ");
        String mID = input.nextLine();

        System.out.print("Enter Field to update (name, description, etc.): ");
        String field = input.nextLine();

        System.out.print("Enter New Value: ");
        String newValue = input.nextLine();

        Item ite = new Item();
        ite.UpdateItem(mID, field, newValue);

    }
}
