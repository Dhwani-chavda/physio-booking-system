import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n1. Add Physiotherapist");
            System.out.println("2. Delete Physiotherapist");
            System.out.println("3. View Physiotherapists");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();
            sc.nextLine(); // consume newline

            if (choice == 1) {
                PhysiotherapistManager.addPhysiotherapist(sc);
            } else if (choice == 2) {
                PhysiotherapistManager.deletePhysiotherapist(sc);
            } else if (choice == 3) {
                PhysiotherapistManager.viewPhysiotherapists();
            } else if (choice == 4) {
                break;
            } else {
                System.out.println("Invalid choice!");
            }
        }

        sc.close();
    }
}
