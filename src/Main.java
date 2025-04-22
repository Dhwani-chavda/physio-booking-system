import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n========== Main Menu ==========");
            System.out.println("1. Manage Physiotherapist");
            System.out.println("2. Manage Patient");
            System.out.println("3. Manage Appointments");
            System.out.println("4. Generate Appointment Report");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    managePhysiotherapist(sc);
                    break;
                case "2":
                    managePatient(sc);
                    break;
                case "3":
                    manageAppointments(sc);
                    break;
                case "4":
                    Report.generateWeeklyAppointmentReport();
                    break;
                case "0":
                    System.out.println("Exiting Program...");
                    return;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    public static void managePhysiotherapist(Scanner sc) {
        while (true) {
            System.out.println("\n--- Manage Physiotherapist ---");
            System.out.println("1. Add Physiotherapist");
            System.out.println("2. Delete Physiotherapist");
            System.out.println("3. Add Schedule");
            System.out.println("4. View Physiotherapists");
            System.out.println("0. Back");
            System.out.print("Enter your choice: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    PhysiotherapistManager.addPhysiotherapist(sc);
                    break;
                case "2":
                    PhysiotherapistManager.deletePhysiotherapist(sc);
                    break;
                case "3":
                    PhysiotherapistManager.addSchedule(sc);
                    break;
                case "4":
                    PhysiotherapistManager.viewPhysiotherapists();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    public static void managePatient(Scanner sc) {
        while (true) {
            System.out.println("\n--- Manage Patient ---");
            System.out.println("1. Add Patient");
            System.out.println("2. Remove Patient");
            System.out.println("3. View Patients");
            System.out.println("0. Back");
            System.out.print("Enter your choice: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    PatientManager.addPatient(sc);
                    break;
                case "2":
                    PatientManager.removePatient(sc);
                    break;
                case "3":
                    PatientManager.viewPatients();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    public static void manageAppointments(Scanner sc) {
        while (true) {
            System.out.println("\n--- Manage Appointments ---");
            System.out.println("1. Book Appointment");
            System.out.println("2. View Appointments");
            System.out.println("3. Attend Treatment Appointment");
            System.out.println("4. Cancel Booking");
            System.out.println("5. Change Booking");
            System.out.println("0. Back");
            System.out.print("Enter your choice: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    AppointmentManager.bookAppointment(sc);
                    break;
                case "2":
                    AppointmentManager.viewAppointments();
                    break;
                case "3":
                    AppointmentManager.attendAppointment(sc);
                    break;
                case "4":
                    System.out.print("Enter Appointment ID to cancel: ");
                    String cancelAppointmentId = sc.nextLine();
                    AppointmentManager.cancelAppointment(cancelAppointmentId);
                    break;
                case "5":
                    AppointmentManager.changeAppointment(sc);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }
}