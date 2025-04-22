import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.*;

public class PatientManager {
    static String patientFile = "Patient.txt";

    // Add a new patient
    public static void addPatient(Scanner sc) {
        try {
            System.out.print("Enter Patient ID: ");
            String id = sc.nextLine();

            System.out.print("Enter Full Name: ");
            String name = sc.nextLine();

            System.out.print("Enter Address: ");
            String address = sc.nextLine();

            System.out.print("Enter Phone Number: ");
            String phone = sc.nextLine();

            String data = "{ID=" + id + ", Name=" + name + ", Address=" + address + ", Phone=" + phone + "}";

            FileWriter fw = new FileWriter(patientFile, true);
            fw.write(data + "\n");
            fw.close();

            System.out.println("Patient added successfully.");
        } catch (Exception e) {
            System.out.println("Error adding patient: " + e.getMessage());
        }
    }

    // Remove a patient by ID
    public static void removePatient(Scanner sc) {
        try {
            System.out.print("Enter Patient ID to remove: ");
            String idToDelete = sc.nextLine();

            File file = new File(patientFile);
            Scanner reader = new Scanner(file);
            List<String> lines = new ArrayList<>();
            boolean found = false;

            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                if (!line.contains("ID=" + idToDelete + ",")) {
                    lines.add(line);
                } else {
                    found = true;
                }
            }
            reader.close();

            PrintWriter pw = new PrintWriter(file);
            for (String line : lines) {
                pw.println(line);
            }
            pw.close();

            if (found) {
                System.out.println("Patient removed successfully.");
            } else {
                System.out.println("Patient not found.");
            }

        } catch (Exception e) {
            System.out.println("Error removing patient: " + e.getMessage());
        }
    }

    // View all patients
    public static void viewPatients() {
        try {
            File file = new File(patientFile);
            if (!file.exists()) {
                System.out.println("No patient records found.");
                return;
            }

            Scanner reader = new Scanner(file);
            boolean hasData = false;

            System.out.println("\n------------------------------------------------------------------------------------");
            System.out.printf("| %-6s | %-25s | %-15s | %-25s |\n",
                    "ID", "Name", "Phone", "Address");
            System.out.println("------------------------------------------------------------------------------------");

            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                if (line.trim().isEmpty()) continue;

                String id = getValue(line, "ID");
                String name = getValue(line, "Name");
                String phone = getValue(line, "Phone");
                String address = getValue(line, "Address");

                System.out.printf("| %-6s | %-25s | %-15s | %-25s |\n",
                        id, name, phone, address);

                hasData = true;
            }

            reader.close();
            System.out.println("------------------------------------------------------------------------------------");

            if (!hasData) {
                System.out.println("No patient records found.");
            }

        } catch (Exception e) {
            System.out.println("Error reading patients: " + e.getMessage());
        }
    }

    // Helper to extract value
    private static String getValue(String line, String key) {
        try {
            int start = line.indexOf(key + "=") + key.length() + 1;
            int end = line.indexOf(",", start);
            if (end == -1) end = line.indexOf("}", start);
            return line.substring(start, end).trim();
        } catch (Exception e) {
            return "";
        }
    }
}
