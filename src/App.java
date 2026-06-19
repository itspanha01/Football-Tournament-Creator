import java.util.*;

public class App {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        int MAX_WIDTH = 32;
        int choice;
        Display display = new Display();

        System.out.println("\nFootball Tournament Builder");
        System.out.println("┌" + "─".repeat(MAX_WIDTH-2) + "┐");
        System.out.println("│" + center("1. Show Bracket", MAX_WIDTH-2) + " │");
        System.out.println("│" + center("2. Add teams", MAX_WIDTH-2) + "│");
        System.out.println("│" + center("3. Edit teams", MAX_WIDTH-2) + " │");
        System.out.println("│" + center("4. Delete teams", MAX_WIDTH-2) + " │");
        System.out.println("│" + center("5. Exit", MAX_WIDTH-2) + " │");
        System.out.println("└" + "─".repeat(MAX_WIDTH-2) + "┘");

        do {
            System.out.print("Choose operation: ");
            choice = scan.nextInt();
            if (choice == 1) {display.bracket_all();}
            else if (choice == 2) {}
            else if (choice == 3) {}
            else if (choice == 4) {}
            else if (choice == 4) {}

        } while (choice != 5);
    }
    public static String center(String word, int max) {
        int padding = (max - word.length()) / 2;
        return " ".repeat(padding) + word + " ".repeat(padding);
    }
}
