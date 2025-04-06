import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.*;

// A class for manage physiotherapist details
public class PhysiotherapistManager{
    static String fileName = "Physiotherapist.txt";

    // A Method for add physiotherapist
    public static void addPhysiotherapist(Scanner sc) {
        try {
            System.out.print("Enter ID: ");
            String id = sc.nextLine();

            System.out.print("Enter Full Name: ");
            String name = sc.nextLine();

            System.out.print("Enter Address: ");
            String address = sc.nextLine();

            System.out.print("Enter Phone: ");
            String phone = sc.nextLine();

            System.out.print("Enter Expertise (comma-separated): ");
            String expertise = sc.nextLine();

            System.out.print("Enter Timetable: ");
            String timetable = sc.nextLine();

            String data = "{ID=" + id + ", Name=" + name + ", Address=" + address + ", Phone=" + phone +
                    ", Expertise=[" + expertise + "], Timetable=" + timetable + "}";

            FileWriter fw = new FileWriter(fileName, true);
            fw.write(data + "\n");
            fw.close();

            System.out.println("Physiotherapist added successfully.");

        } catch (Exception e) {
            System.out.println("Error adding physiotherapist: " + e.getMessage());
        }
    }

    // A Method for delete physiotherapist
    public static void deletePhysiotherapist(Scanner sc) {
        try {
            System.out.print("Enter ID to delete: ");
            String idToDelete = sc.nextLine();

            File file = new File(fileName);
            Scanner fileReader = new Scanner(file);
            List<String> lines = new ArrayList<>();
            boolean found = false;

            while (fileReader.hasNextLine()) {
                String line = fileReader.nextLine();
                if (!line.contains("ID=" + idToDelete + ",")) {
                    lines.add(line);
                } else {
                    found = true;
                }
            }
            fileReader.close();

            PrintWriter pw = new PrintWriter(file);
            for (String line : lines) {
                pw.println(line);
            }
            pw.close();

            if (found) {
                System.out.println("Physiotherapist deleted successfully.");
            } else {
                System.out.println("Physiotherapist not found.");
            }

        } catch (Exception e) {
            System.out.println("Error deleting physiotherapist: " + e.getMessage());
        }
    }

    public static void viewPhysiotherapists() {
        try {
            File file = new File(fileName);
            Scanner fileReader = new Scanner(file);
            boolean hasData = false;

            System.out.println("\n------------------------------------------------------------------------------------------");
            System.out.printf("| %-6s | %-20s | %-15s | %-12s | %-25s | %-15s |\n",
                    "ID", "Name", "Phone", "Timetable", "Expertise", "Address");
            System.out.println("------------------------------------------------------------------------------------------");

            while (fileReader.hasNextLine()) {
                String line = fileReader.nextLine();
                if (line.trim().isEmpty()) continue;

                // Parse the line (very basic parsing based on expected format)
                String id = getValue(line, "ID");
                String name = getValue(line, "Name");
                String phone = getValue(line, "Phone");
                String timetable = getValue(line, "Timetable");
                String expertise = getValue(line, "Expertise");
                String address = getValue(line, "Address");

                System.out.printf("| %-6s | %-20s | %-15s | %-12s | %-25s | %-15s |\n",
                        id, name, phone, timetable, expertise, address);

                hasData = true;
            }

            fileReader.close();
            System.out.println("------------------------------------------------------------------------------------------");

            if (!hasData) {
                System.out.println("No physiotherapist records found.");
            }

        } catch (Exception e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    // Helper method to extract field value from the line
    private static String getValue(String line, String key) {
        try {
            int start = line.indexOf(key + "=") + key.length() + 1;
            int end = line.indexOf(",", start);
            if (end == -1) end = line.indexOf("}", start); // for last element
            return line.substring(start, end).trim();
        } catch (Exception e) {
            return "";
        }
    }

}



