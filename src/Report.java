import java.io.File;
import java.util.*;

public class Report {

    private static final String appointmentFile = "Appointment.txt";
    private static final String patientFile = "Patient.txt";
    private static final String physiotherapistFile = "Physiotherapist.txt";

    public static void generateWeeklyAppointmentReport() {
        Map<String, String> physioNames = loadNames(physiotherapistFile);
        Map<String, String> patientNames = loadNames(patientFile);

        Map<String, List<String[]>> appointmentsByPhysio = new HashMap<>();
        Map<String, Integer> attendedCount = new HashMap<>();

        try {
            File file = new File(appointmentFile);
            if (!file.exists()) {
                System.out.println("No appointments to display.");
                return;
            }

            Scanner reader = new Scanner(file);

            while (reader.hasNextLine()) {
                String line = reader.nextLine().trim();
                String physioId = extractValue(line, "PhysiotherapistID");
                String physioName = physioNames.getOrDefault(physioId, "Unknown");

                String patientId = extractValue(line, "PatientID");
                String patientName = patientNames.getOrDefault(patientId, "Unknown");

                String treatment = extractValue(line, "Treatment");
                String status = extractValue(line, "Status");
                String schedule = extractSchedule(line);

                String[] appointmentDetails = {
                        patientName, treatment, schedule, status
                };

                appointmentsByPhysio.putIfAbsent(physioName, new ArrayList<>());
                appointmentsByPhysio.get(physioName).add(appointmentDetails);

                if (status.equalsIgnoreCase("Attended")) {
                    attendedCount.put(physioName, attendedCount.getOrDefault(physioName, 0) + 1);
                }
            }

            reader.close();

            System.out.println("\n===================== 4-WEEK APPOINTMENT REPORT =====================");
            for (String physio : appointmentsByPhysio.keySet()) {
                System.out.println("\nPhysiotherapist: " + physio);
                System.out.println("------------------------------------------------------------");
                for (String[] appt : appointmentsByPhysio.get(physio)) {
                    System.out.println("Patient Name       : " + appt[0]);
                    System.out.println("Treatment          : " + appt[1]);
                    System.out.println("Schedule           : " + appt[2]);
                    System.out.println("Status             : " + appt[3]);
                    System.out.println("------------------------------------------------------------");
                }
            }

            System.out.println("\n================ PHYSIOTHERAPISTS BY ATTENDED COUNT ================");
            attendedCount.entrySet().stream()
                    .sorted((a, b) -> b.getValue() - a.getValue())
                    .forEach(entry -> System.out.println(entry.getKey() + " - Attended: " + entry.getValue()));
            System.out.println("=====================================================================\n");

        } catch (Exception e) {
            System.out.println("Error generating report: " + e.getMessage());
        }
    }


    // Reusable methods
    private static String extractValue(String line, String key) {
        int start = line.indexOf(key + "=");
        if (start == -1) return "";
        start += key.length() + 1;
        int end = line.indexOf(",", start);
        if (end == -1) end = line.indexOf("}", start);
        if (end == -1) end = line.length();
        return line.substring(start, end).trim();
    }

    private static Map<String, String> loadNames(String fileName) {
        Map<String, String> nameMap = new HashMap<>();
        try {
            File file = new File(fileName);
            if (!file.exists()) return nameMap;

            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                String line = reader.nextLine().trim();
                String id = extractValue(line, "ID");
                String name = extractValue(line, "Name");
                nameMap.put(id, name);
            }
            reader.close();
        } catch (Exception e) {
            // Ignore and return whatever is loaded
        }
        return nameMap;
    }

    private static String extractSchedule(String line) {
        try {
            int scheduleStart = line.indexOf("Schedule=");
            if (scheduleStart == -1) return "";

            String sub = line.substring(scheduleStart);
            int openBrace = sub.indexOf("{");
            int closeBrace = sub.indexOf("}");

            String content;
            if (openBrace != -1 && closeBrace != -1 && closeBrace > openBrace) {
                content = sub.substring(openBrace + 1, closeBrace);
            } else {
                int end = sub.indexOf(", Status=");
                content = sub.substring(9, end);
            }

            String day = extractValue(content, "Day");
            String date = extractValue(content, "Date");
            String time = extractValue(content, "Time");

            return day + ", " + date + ", " + time;

        } catch (Exception e) {
            return "N/A";
        }
    }
}
