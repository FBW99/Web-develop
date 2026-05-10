
package Common_pkg;

import java.util.Scanner;

public abstract class Menu {
 public abstract void DisplayMenu(Scanner input);
 public void handleInput(Scanner input){
        System.out.print("Enter your choice: ");
        int choice = input.nextInt();
        choiceOptionProcessing(input, choice);
}

   public abstract void choiceOptionProcessing(Scanner input,int choice);
}