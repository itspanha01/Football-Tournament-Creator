import java.util.*;

public class Bracket {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        System.out.print("Enter 1st team name: ");
        String team1 = scan.nextLine();

        System.out.print("Enter 2nd team name: ");
        String team2 = scan.nextLine();

        System.out.printf("Match today: %s vs %s", team1, team2);
    }
}
