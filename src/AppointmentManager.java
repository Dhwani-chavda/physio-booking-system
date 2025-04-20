import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AppointmentManager {
    static String appointmentFile = "Appointment.txt";
    static String scheduleFile = "Schedules.txt";
    static String patientFile = "Patient.txt";
    static String physiotherapistFile = "Physiotherapist.txt";
    static String treatmentFile = "Treatment.txt";

    public static void bookAppointment(Scanner sc) {
        try {
            // Ensure unique appointment ID
            System.out.print("Enter Appointment ID: ");
            String appointmentId = sc.nextLine().trim();
            if (isAppointmentIdExists(appointmentId)) {
                System.out.println("Appointment ID already exists. Please enter a unique ID.");
                return;
            }

            // Select Patient by Name
            System.out.println("\n--- Patient List ---");
            displayFileContents(patientFile);
            System.out.print("Enter Patient Name: ");
            String patientName = sc.nextLine().trim();
            String patientId = getIdByName(patientFile, patientName);
            if (patientId == null) {
                System.out.println("Patient not found.");
                return;
            }

            // Select Treatment
            System.out.println("\n--- Treatment List ---");
            displayFileContents(treatmentFile);
            System.out.print("Enter Treatment ID: ");
            String treatmentId = sc.nextLine().trim();
            String treatmentName = getTreatmentNameById(treatmentId);
            if (treatmentName.isEmpty()) {
                System.out.println("Invalid Treatment ID.");
                return;
            }

            // Select Physiotherapist by Name
            System.out.println("\n--- Physiotherapist List ---");
            displayFileContents(physiotherapistFile);
            System.out.print("Enter Physiotherapist Name: ");
            String physioName = sc.nextLine().trim();
            String physiotherapistId = getIdByName(physiotherapistFile, physioName);
            if (physiotherapistId == null) {
                System.out.println("Physiotherapist not found.");
                return;
            }

            // Fetch schedules
            List<String> schedules = getScheduleObjectsById(physiotherapistId);
            if (schedules.isEmpty()) {
                System.out.println("No available schedules for this Physiotherapist.");
                return;
            }

            System.out.println("\n--- Available Schedules ---");
            for (int i = 0; i < schedules.size(); i++) {
                System.out.println((i + 1) + ". " + schedules.get(i).replace("{", "").replace("}", ""));
            }

            System.out.print("Select schedule number to book: ");
            int scheduleIndex = Integer.parseInt(sc.nextLine()) - 1;
            if (scheduleIndex < 0 || scheduleIndex >= schedules.size()) {
                System.out.println("Invalid selection.");
                return;
            }

            String selectedSchedule = schedules.get(scheduleIndex).trim();

            // Build and store the appointment
            String data = "{AppointmentID=" + appointmentId +
                    ", PatientID=" + patientId +
                    ", Treatment=" + treatmentName +
                    ", PhysiotherapistID=" + physiotherapistId +
                    ", Schedule=" + selectedSchedule +
                    ", Status=Booked}";

            FileWriter fw = new FileWriter(appointmentFile, true);
            fw.write(data + "\n");
            fw.close();

            // Remove selected schedule
            removeScheduleObject(physiotherapistId, selectedSchedule);
            System.out.println("Appointment booked successfully.");

        } catch (Exception e) {
            System.out.println("Error booking appointment: " + e.getMessage());
        }
    }

    public static void attendAppointment(Scanner sc) {
        try {
            System.out.print("Enter Appointment ID to Attend: ");
            String appointmentId = sc.nextLine();

            File file = new File(appointmentFile);
            Scanner reader = new Scanner(file);
            List<String> updatedLines = new ArrayList<>();
            boolean found = false;

            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                if (line.contains("AppointmentID=" + appointmentId + ",")) {
                    found = true;
                    if (line.contains("Status=Booked")) {
                        line = line.replace("Status=Booked", "Status=Attended");
                        System.out.println("Appointment attended successfully.");
                    } else if (line.contains("Status=Attended")) {
                        System.out.println("Appointment already marked as attended.");
                    } else if (line.contains("Status=Cancelled")) {
                        System.out.println("Cannot attend a cancelled appointment.");
                    }
                }
                updatedLines.add(line);
            }
            reader.close();

            if (found) {
                PrintWriter writer = new PrintWriter(file);
                for (String l : updatedLines) {
                    writer.println(l);
                }
                writer.close();
            } else {
                System.out.println("Appointment ID not found.");
            }

        } catch (Exception e) {
            System.out.println("Error attending appointment: " + e.getMessage());
        }
    }

    public static void cancelAppointment(String appointmentIdToCancel) {
        try {
            File appointmentFile = new File("Appointment.txt");
            File scheduleFile = new File("Schedules.txt");

            if (!appointmentFile.exists()) {
                System.out.println("Appointment file not found.");
                return;
            }

            List<String> updatedAppointments = new ArrayList<>();
            String restoredSchedule = null;

            // Read appointments and update the matching one
            Scanner reader = new Scanner(appointmentFile);
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                if (line.contains("AppointmentID=" + appointmentIdToCancel)) {
                    // Update status
                    line = line.replace("Status=Booked", "Status=Cancelled");

                    // Extract schedule string
                    int start = line.indexOf("Schedule={") + "Schedule={".length();
                    int end = line.indexOf("}", start);
                    if (start > 0 && end > start) {
                        restoredSchedule = line.substring(start, end); // no outer braces
                    }
                }
                updatedAppointments.add(line);
            }
            reader.close();

            // Write the updated appointments back
            FileWriter writer = new FileWriter(appointmentFile, false);
            for (String appt : updatedAppointments) {
                writer.write(appt + "\n");
            }
            writer.close();

            // Append the restored schedule (with single outer braces)
            if (restoredSchedule != null) {
                FileWriter scheduleWriter = new FileWriter(scheduleFile, true);
                scheduleWriter.write("{" + restoredSchedule + "}\n");
                scheduleWriter.close();
            }

            System.out.println("Appointment status updated to 'Cancelled' and schedule restored.");
        } catch (Exception e) {
            System.out.println("Error canceling appointment: " + e.getMessage());
        }
    }

    public static void changeAppointment(Scanner sc) {
        System.out.print("Enter Appointment ID to change: ");
        String appointmentId = sc.nextLine();

        cancelAppointmentById(appointmentId);

        System.out.println("\nNow please rebook a new appointment:");
        bookAppointment(sc);
    }

    public static void viewAppointments() {
        try {
            File file = new File("Appointment.txt");
            if (!file.exists()) {
                System.out.println("No appointments found.");
                return;
            }

            List<String> appointments = new ArrayList<>();
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                appointments.add(reader.nextLine());
            }
            reader.close();

            if (appointments.isEmpty()) {
                System.out.println("No appointments to show.");
                return;
            }

            System.out.println("----------------------------------------------------------------------------------------------------------------------------------------");
            System.out.printf("| %-8s | %-20s | %-20s | %-20s | %-40s | %-10s |\n", "ID", "Patient", "Treatment", "Physiotherapist", "Schedule", "Status");
            System.out.println("----------------------------------------------------------------------------------------------------------------------------------------");

            for (String line : appointments) {
                try {
                    String appointmentId = extractValue(line, "AppointmentID");
                    String patientId = extractValue(line, "PatientID");
                    String treatment = extractValue(line, "Treatment");
                    String physioId = extractValue(line, "PhysiotherapistID");
                    String status = extractValue(line, "Status");

                    String physioName = getPhysiotherapistNameById(physioId);
                    String patientName = getPatientNameById(patientId);

                    String schedule = "";
                    int scheduleStart = line.indexOf("Schedule={");
                    int scheduleEnd = line.indexOf("}, Status");
                    if (scheduleStart != -1 && scheduleEnd != -1 && scheduleEnd > scheduleStart) {
                        String rawSchedule = line.substring(scheduleStart + 9, scheduleEnd).trim(); // skip "Schedule={"
                        rawSchedule = rawSchedule.replaceAll("ScheduleID=\\d+,\\s*", ""); // remove ScheduleID
                        rawSchedule = rawSchedule.replaceAll("ID=\\w+,\\s*", "");         // remove ID
                        schedule = rawSchedule.replace("{", "").replace("}", "").trim();
                    } else {
                        schedule = "Invalid Schedule";
                    }

                    System.out.printf("| %-8s | %-20s | %-20s | %-20s | %-40s | %-10s |\n",
                            appointmentId, patientName, treatment, physioName, schedule, status);

                } catch (Exception e) {
                    System.out.println("Error displaying an appointment: " + e.getMessage());
                }
            }

            System.out.println("----------------------------------------------------------------------------------------------------------------------------------------");

        } catch (Exception e) {
            System.out.println("Error displaying appointments: " + e.getMessage());
        }
    }

    // Helper Methods
    private static String getIdByName(String filename, String name) {
        try {
            Scanner reader = new Scanner(new File(filename));
            while (reader.hasNextLine()) {
                String line = reader.nextLine().trim();
                String extractedName = extractValue(line, "Name");
                if (extractedName.equalsIgnoreCase(name)) {
                    return extractValue(line, "ID");
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    private static boolean isAppointmentIdExists(String appointmentId) {
        try {
            File file = new File("Appointment.txt");
            if (!file.exists()) return false;

            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                if (line.contains("AppointmentID=" + appointmentId)) {
                    reader.close();
                    return true;
                }
            }
            reader.close();
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    private static void displayFileContents(String filename) {
        try {
            File file = new File(filename);
            if (!file.exists()) {
                System.out.println("File not found.");
                return;
            }
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                // Remove all curly braces before printing
                line = line.replace("{", "").replace("}", "");
                System.out.println(line);
            }
            reader.close();
        } catch (Exception e) {
            System.out.println("Error reading file: " + filename);
        }
    }

    private static String getTreatmentNameById(String id) {
        try {
            File file = new File("Treatment.txt");
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                if (line.contains("ID=" + id + ",")) {
                    int start = line.indexOf("Name=") + 5;
                    int end = line.indexOf("}", start);
                    reader.close();
                    return line.substring(start, end).trim();
                }
            }
            reader.close();
        } catch (Exception e) {
            return "Unknown";
        }
        return "Unknown";
    }

    private static List<String> getScheduleObjectsById(String id) {
        List<String> schedules = new ArrayList<>();
        try {
            File file = new File(scheduleFile);
            if (!file.exists()) return schedules;

            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                String line = reader.nextLine().trim();
                if (line.contains("ID=" + id + ",")) {
                    if (line.startsWith("{") && line.endsWith("}")) {
                        line = line.substring(1, line.length() - 1).trim();
                    }
                    schedules.add("{" + line + "}");
                }
            }
            reader.close();
        } catch (Exception e) {
            System.out.println("Error reading schedules: " + e.getMessage());
        }
        return schedules;
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

    private static String extractValue(String line, String key) {
        int start = line.indexOf(key + "=");
        if (start == -1) return "";
        start += key.length() + 1;
        int end = line.indexOf(",", start);
        if (end == -1) end = line.indexOf("}", start);
        if (end == -1) end = line.length();
        return line.substring(start, end).trim();
    }

    private static String getPatientNameById(String id) {
        try {
            Scanner reader = new Scanner(new File("Patient.txt"));
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                if (line.contains("ID=" + id + ",")) {
                    return getValue(line, "Name");
                }
            }
            reader.close();
        } catch (Exception e) {
            return "Unknown";
        }
        return "Unknown";
    }

    private static String getPhysiotherapistNameById(String id) {
        try {
            Scanner reader = new Scanner(new File("Physiotherapist.txt"));
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                if (line.contains("ID=" + id + ",")) {
                    return getValue(line, "Name");
                }
            }
            reader.close();
        } catch (Exception e) {
            return "Unknown";
        }
        return "Unknown";
    }

    private static void removeScheduleObject(String physiotherapistId, String scheduleToRemove) {
        try {
            File file = new File("Schedules.txt");
            if (!file.exists()) return;

            List<String> updatedSchedules = new ArrayList<>();
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                String line = reader.nextLine().trim();
                if (!line.equals(scheduleToRemove.trim())) {
                    updatedSchedules.add(line);
                }
            }
            reader.close();

            FileWriter writer = new FileWriter(file, false); // Overwrite the file
            for (String schedule : updatedSchedules) {
                writer.write(schedule + "\n");
            }
            writer.close();

        } catch (Exception e) {
            System.out.println("Error removing schedule: " + e.getMessage());
        }
    }

    public static void cancelAppointmentById(String appointmentIdToCancel) {
        try {
            File appointmentFile = new File("Appointment.txt");
            File scheduleFile = new File("Schedules.txt");

            if (!appointmentFile.exists()) {
                System.out.println("Appointment file not found.");
                return;
            }

            List<String> updatedAppointments = new ArrayList<>();
            String restoredSchedule = null;

            Scanner reader = new Scanner(appointmentFile);
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                if (line.contains("AppointmentID=" + appointmentIdToCancel)) {
                    // Update status only if it was previously Booked
                    if (line.contains("Status=Booked")) {
                        line = line.replace("Status=Booked", "Status=Cancelled");

                        // Extract schedule portion to restore
                        int start = line.indexOf("Schedule={") + "Schedule={".length();
                        int end = line.indexOf("}", start);
                        if (start > 0 && end > start) {
                            restoredSchedule = line.substring(start, end);
                        }
                    }
                }
                updatedAppointments.add(line);
            }
            reader.close();

            // Write back updated appointments
            FileWriter writer = new FileWriter(appointmentFile, false);
            for (String appt : updatedAppointments) {
                writer.write(appt + "\n");
            }
            writer.close();

            // Restore the schedule
            if (restoredSchedule != null) {
                FileWriter scheduleWriter = new FileWriter(scheduleFile, true);
                scheduleWriter.write("{" + restoredSchedule + "}\n");
                scheduleWriter.close();
            }

            System.out.println("Appointment cancelled and schedule restored.");
        } catch (Exception e) {
            System.out.println("Error canceling appointment: " + e.getMessage());
        }
    }

}