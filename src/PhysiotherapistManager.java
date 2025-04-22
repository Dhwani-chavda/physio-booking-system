import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.*;

// A class for manage physiotherapist details
public class PhysiotherapistManager{
    static String fileName = "Physiotherapist.txt";
    static String scheduleFile = "Schedules.txt";

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

            String data = "{ID=" + id + ", Name=" + name + ", Address=" + address + ", Phone=" + phone +
                    ", Expertise=[" + expertise + "]" + "}";

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

    // A Method for add physiotherapist schedules
    public static void addSchedule(Scanner sc) {
        try {
            System.out.print("Enter Unique Schedule ID (number): ");
            String scheduleId = sc.nextLine();

            // Check if ID already exists
            File file = new File(scheduleFile);
            if (file.exists()) {
                Scanner reader = new Scanner(file);
                while (reader.hasNextLine()) {
                    String line = reader.nextLine();
                    if (line.contains("ScheduleID=" + scheduleId + ",")) {
                        System.out.println("Schedule ID already exists. Please try again with a different ID.");
                        reader.close();
                        return;
                    }
                }
                reader.close();
            }

            System.out.print("Enter Physiotherapist ID: ");
            String id = sc.nextLine();

            System.out.print("Enter Day (e.g., Thursday): ");
            String day = sc.nextLine();

            System.out.print("Enter Date (e.g., 1st May 2025): ");
            String date = sc.nextLine();

            System.out.print("Enter Time (e.g., 10:00-11:00): ");
            String time = sc.nextLine();

            String scheduleEntry = "{ScheduleID=" + scheduleId + ", ID=" + id + ", Day=" + day + ", Date=" + date + ", Time=" + time + "}";

            FileWriter fw = new FileWriter(scheduleFile, true);
            fw.write(scheduleEntry + "\n");
            fw.close();

            System.out.println("Schedule added successfully.");
        } catch (Exception e) {
            System.out.println("Error adding schedule: " + e.getMessage());
        }
    }

    // A Method for view physiotherapist list
    public static void viewPhysiotherapists() {
        try {
            File file = new File(fileName);
            Scanner fileReader = new Scanner(file);
            boolean hasData = false;

            System.out.println("\n-----------------------------------------------------------------------------------------------------------------------------------------------");
            System.out.printf("| %-6s | %-25s | %-15s | %-28s | %-20s | %-30s |\n",
                    "ID", "Name", "Phone", "Expertise", "Address", "Available Schedules");
            System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------");

            while (fileReader.hasNextLine()) {
                String line = fileReader.nextLine();
                if (line.trim().isEmpty()) continue;

                // Parse the line (very basic parsing based on expected format)
                String id = getValue(line, "ID");
                String name = getValue(line, "Name");
                String phone = getValue(line, "Phone");
                String expertise = getValue(line, "Expertise");
                String address = getValue(line, "Address");
                String schedules = getSchedulesById(id);

                System.out.printf("| %-6s | %-25s | %-15s | %-28s | %-20s | %-30s |\n",
                        id, name, phone, expertise, address, schedules.replaceAll("\n", "\n" + pad(109) + "| "));

                hasData = true;
            }

            fileReader.close();
            System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------");

            if (!hasData) {
                System.out.println("No physiotherapist records found.");
            }

        } catch (Exception e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    // Helper methods
    private static String getSchedulesById(String id) {
        StringBuilder sb = new StringBuilder();
        List<String> scheduleList = new ArrayList<>();

        try {
            File file = new File(scheduleFile);
            if (!file.exists()) return "No Schedules Available";

            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                if (line.contains("ID=" + id + ",")) {
                    String day = getValue(line, "Day");
                    String date = getValue(line, "Date");
                    String time = getValue(line, "Time");
                    scheduleList.add(day + " " + date + " " + time);
                }
            }
            reader.close();
        } catch (Exception e) {
            return "Error loading schedules";
        }

        for (int i = 0; i < scheduleList.size(); i++) {
            sb.append(scheduleList.get(i));
            if (i < scheduleList.size() - 1) {
                sb.append("\n");
            }
        }

        return sb.length() == 0 ? "No Schedules" : sb.toString();
    }

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

    private static String pad(int n) {
        return " ".repeat(Math.max(0, n));
    }

}



