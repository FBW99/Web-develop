
public class MaxMin {
    public static void main(String[] args) {

        int[] numbers = {5, 8, 2, 10, 3, 7};  
        
        int max = numbers[0];
        int min = numbers[0];

        for (int num:numbers) {
            if (num> max) {
                max = num;
            }
            if (num< min) {
                min = num;
            }
        }

        System.out.println("Maximum value = " + max);
        System.out.println("Minimum value = " + min);
    }
}