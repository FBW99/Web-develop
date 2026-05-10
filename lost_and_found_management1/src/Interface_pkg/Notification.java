
package Interface_pkg;
public class Notification {
    String notificationID, userID, message;

    public Notification(String notificationID, String userID, String message) {
        this.notificationID = notificationID;
        this.userID = userID;
        this.message = message;
    }

    public String toFileFormat() {
        return notificationID + "," + userID + "," + message;
    }

    public String toDisplayFormat() {
        return "NotificationID: " + notificationID + "\tUserID: " + userID + "\tMessage: " + message;
    }
}
