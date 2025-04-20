import java.io.File;
import java.util.Scanner;

public class Report {

    private static final String appointmentFile = "Appointment.txt";
    private static final String patientFile = "Patient.txt";
    private static final String physiotherapistFile = "Physiotherapist.txt";

    public static void printAppointmentReport(Scanner sc) {
        System.out.print("Enter Appointment ID to generate report: ");
        String appointmentId = sc.nextLine().trim();

        try {
            File file = new File(appointmentFile);
            if (!file.exists()) {
                System.out.println("Appointment file not found.");
                return;
            }

            Scanner reader = new Scanner(file);
            boolean found = false;

            while (reader.hasNextLine()) {
                String line = reader.nextLine().trim();
                if (line.contains("AppointmentID=" + appointmentId)) {
                    found = true;

                    // Extract fields
                    String patientId = extractValue(line, "PatientID");
                    String treatment = extractValue(line, "Treatment");
                    String physiotherapistId = extractValue(line, "PhysiotherapistID");
                    String schedule = extractSchedule(line);
                    String status = extractValue(line, "Status");

                    String patientName = getNameById(patientFile, patientId);
                    String physioName = getNameById(physiotherapistFile, physiotherapistId);

                    // Print formatted report
                    System.out.println("\n===================== APPOINTMENT REPORT =====================");
                    System.out.println("Appointment ID     : " + appointmentId);
                    System.out.println("Patient Name       : " + patientName);
                    System.out.println("Treatment          : " + treatment);
                    System.out.println("Physiotherapist    : " + physioName);
                    System.out.println("Schedule           : " + schedule);
                    System.out.println("Status             : " + status);
                    System.out.println("==============================================================\n");
                    break;
                }
            }

            reader.close();
            if (!found) {
                System.out.println("No appointment found with ID: " + appointmentId);
            }

        } catch (Exception e) {
            System.out.println("Error generating report: " + e.getMessage());
        }
    }

    // Helper method
    private static String extractValue(String line, String key) {
        int start = line.indexOf(key + "=");
        if (start == -1) return "";
        start += key.length() + 1;
        int end = line.indexOf(",", start);
        if (end == -1) end = line.indexOf("}", start);
        if (end == -1) end = line.length();
        return line.substring(start, end).trim();
    }

    private static String getNameById(String filename, String id) {
        try {
            File file = new File(filename);
            if (!file.exists()) return "";

            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                if (line.contains("ID=" + id)) {
                    return extractValue(line, "Name");
                }
            }
            reader.close();
        } catch (Exception e) {
            return "";
        }
        return "";
    }

    private static String extractSchedule(String line) {
        String scheduleContent = "";
        try {
            int scheduleStart = line.indexOf("Schedule=");

            if (scheduleStart == -1) return "";

            String sub = line.substring(scheduleStart);
            int openBrace = sub.indexOf("{");
            int closeBrace = sub.indexOf("}");

            if (openBrace != -1 && closeBrace != -1 && closeBrace > openBrace) {
                scheduleContent = sub.substring(openBrace + 1, closeBrace);
            } else {
                int end = sub.indexOf(", Status=");
                scheduleContent = sub.substring(9, end);
            }

            String day = extractValue(scheduleContent, "Day");
            String date = extractValue(scheduleContent, "Date");
            String time = extractValue(scheduleContent, "Time");

            return day + ", " + date + ", " + time;

        } catch (Exception e) {
            return "";
        }
    }

}