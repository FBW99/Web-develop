package Common_pkg;
public class Feedback {
    String feedbackID, userID, comment;

    public Feedback(String feedbackID, String userID, String comment) {
        this.feedbackID = feedbackID;
        this.userID = userID;
        this.comment = comment;
    }

    public String toFileFormat() {
        return feedbackID + "," + userID + "," + comment;
    }

    public String toDisplayFormat() {
        return "FeedbackID: " + feedbackID + "\tUserID: " + userID + "\tComment: " + comment;
    }
}
