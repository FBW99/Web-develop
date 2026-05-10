package Common_pkg;

import Student_pkg.StudentMenu;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.Scanner;
import java.util.UUID;

public class foundItem extends Item {
    private String location;
    private String reporterId;
    private String contactInfo;

    // Constructors
    public foundItem() {}

    public foundItem(String itemId, String name, String description, String category, String status, String location, String reporterId, String contactInfo, LocalDate dateReported) {
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

 
    public void foundItems(Scanner input) {
        System.out.println("| Welcome to Found Item Report Page |");
        System.out.println("1. Report a new found item");
        System.out.println("2. View/edit reported found items");
        System.out.println("3. Back");
        System.out.println("4. Exit");
        System.out.print("Enter your choice: ");
        int choice = input.nextInt();
        input.nextLine(); 

        switch (choice) {
            case 1:
                reportFoundItem(input);
                break;
            case 2:
                lostItem lost = new lostItem();
                lost.editItem(input);
                break;
            case 3:
                StudentMenu st = new StudentMenu();
                st.DisplayMenu(input);
                break;
            case 4:
                System.exit(0);
                break;
            default:
                System.out.println("Invalid option. Try again.");
                foundItems(input);
        }
    }

    private void reportFoundItem(Scanner input) {
        System.out.print("Enter Item Name: ");
        String name = input.nextLine();

        System.out.print("Enter Description: ");
        String description = input.nextLine();

        System.out.print("Enter Category: ");
        String category = input.nextLine();

        System.out.print("Enter Status: ");
        String status = input.nextLine();

        System.out.print("Enter Location Found: ");
        String location = input.nextLine();

        System.out.print("Enter Your ID (Reporter ID): ");
        String reporterId = input.nextLine();

        System.out.print("Enter Contact Information: ");
        String contactInfo = input.nextLine();

        String itemID = UUID.randomUUID().toString().substring(0, 8);
        LocalDate reportedDate = LocalDate.now();

        Item item = new Item(itemID, name, description, category, status, location, reporterId, contactInfo, reportedDate);
        item.addItem();
    }
}
