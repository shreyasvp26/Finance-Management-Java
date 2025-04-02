import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class SpendingTracker {
    private static final String FILE_NAME = "spending.txt";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String continueInput;

        do {
            System.out.print("Enter the amount spent: ");
            double amount = scanner.nextDouble();
            scanner.nextLine();

            System.out.print(
                    "Enter the category of the spend: \n1) Food 2) Entertainment 3) Travelling 4) Subscriptions 5) Misc. : ");
            String category = scanner.nextLine();
            switch (category) {
                case "1":
                    category = "Food";
                    break;
                case "2":
                    category = "Entertainment";
                    break;
                case "3":
                    category = "Travelling";
                    break;
                case "4":
                    category = "Subscriptions";
                    break;
                case "5":
                    category = "Miscellaneous";
                    break;
                default:
                    category = "Unknown";
            }

            LocalDate date = LocalDate.now();

            saveExpense(amount, category, date);

            System.out.print("Do you want to add another expense? (yes/no): ");
            while (true) {
                continueInput = scanner.nextLine();
                try {
                    if(!continueInput.equalsIgnoreCase("yes") && !continueInput.equalsIgnoreCase("no"))
                    {
                        throw  new Exception("Error! Enter valid Input : ");
                    }
                    else
                    {
                        break;
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        } while (continueInput.equalsIgnoreCase("yes"));

        System.out.print("\nDo you want to see an expense summary? (yes/no): ");
        String showSummary ;
        while (true) {
            showSummary = scanner.nextLine();
            try {
                if(!showSummary.equalsIgnoreCase("yes") && !showSummary.equalsIgnoreCase("no"))
                {
                    throw  new Exception("Error! Enter valid Input : ");
                }
                else
                {
                    break;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }


        if (showSummary.equalsIgnoreCase("yes")) {
            System.out.print("\nChoose Summary Type:\n1) Monthly Summary\n2) Today's Summary\nEnter choice (1/2): ");
            String summaryChoice;
            while (true) {
                summaryChoice = scanner.nextLine();
                try {
                    if(!summaryChoice.equalsIgnoreCase("1") && !summaryChoice.equalsIgnoreCase("2"))
                    {
                        throw  new Exception("Error! Enter valid Input : ");
                    }
                    else
                    {
                        break;
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }

            if (summaryChoice.equals("1")) {
                Map<String, Double> categoryTotals = readSpendingData();
                showMonthlySummary();
                System.out.println("Most Spend Category : " + getMostSpentCategory(categoryTotals)
                        + "\nLeast Spend Category : " + getLeastSpentCategory(categoryTotals));
            } else if (summaryChoice.equals("2")) {
                try {
                    Map<String, Double> categoryTotals = readSpendingDataToday();
                    LocalDate specificDate = LocalDate.now();
                    showDateWiseSummary(specificDate);
                    System.out.println("Most Spend Category : " + getMostSpentCategory(categoryTotals)
                            + "\nLeast Spend Category : " + getLeastSpentCategory(categoryTotals));
                } catch (Exception e) {
                    System.out.println("Invalid date format! Please enter in YYYY-MM-DD format.");
                }
            } else {
                System.out.println("Invalid choice! Exiting summary section.");
            }
        }

        scanner.close();
    }

    private static void saveExpense(double amount, String category, LocalDate date) {
        try (FileWriter fw = new FileWriter(FILE_NAME, true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw)) {

            out.println(amount + "," + category + "," + date);
            System.out.println("Expense recorded successfully!");

        } catch (IOException e) {
            System.out.println("Error saving expense: " + e.getMessage());
        }
    }

    private static void showMonthlySummary() {
        Map<String, Double> categoryTotals = new HashMap<>();
        double totalSpent = 0;
        LocalDate currentDate = LocalDate.now();
        int currentMonth = currentDate.getMonthValue();
        int currentYear = currentDate.getYear();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 3)
                    continue;

                double amount = Double.parseDouble(parts[0]);
                String category = parts[1];
                LocalDate date = LocalDate.parse(parts[2]);

                if (date.getMonthValue() == currentMonth && date.getYear() == currentYear) {
                    categoryTotals.put(category, categoryTotals.getOrDefault(category, 0.0) + amount);
                    totalSpent += amount;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading expenses: " + e.getMessage());
        }

        System.out.println("\n====== Monthly Expense Summary ======");
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            System.out.println(entry.getKey() + ": Rs." + entry.getValue());
        }
        System.out.println("Total spent this month: Rs." + totalSpent);
        System.out.println("=====================================");
    }

    private static String getMostSpentCategory(Map<String, Double> categoryTotals) {
        String mostSpentCategory = null;
        double maxSpent = 0;

        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            if (entry.getValue() > maxSpent) {
                maxSpent = entry.getValue();
                mostSpentCategory = entry.getKey();
            }
        }

        return mostSpentCategory;
    }

    private static String getLeastSpentCategory(Map<String, Double> categoryTotals) {
        String leastSpentCategory = null;
        double maxSpent = 1000000;

        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            if (entry.getValue() < maxSpent) {
                maxSpent = entry.getValue();
                leastSpentCategory = entry.getKey();
            }
        }

        return leastSpentCategory;
    }

    private static void showDateWiseSummary(LocalDate targetDate) {
        Map<String, Double> categoryTotals = new HashMap<>();
        double totalSpent = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 3)
                    continue;

                double amount = Double.parseDouble(parts[0]);
                String category = parts[1];
                LocalDate date = LocalDate.parse(parts[2]);

                if (date.equals(targetDate)) {
                    categoryTotals.put(category, categoryTotals.getOrDefault(category, 0.0) + amount);
                    totalSpent += amount;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading expenses: " + e.getMessage());
        }

        System.out.println("\n====== Expense Summary for " + targetDate + " ======");
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            System.out.println(entry.getKey() + ": Rs." + entry.getValue());
        }
        System.out.println("Total spent on " + targetDate + ": Rs." + totalSpent);
        System.out.println("=====================================");
    }

    private static Map<String, Double> readSpendingDataToday() {
        Map<String, Double> categoryTotals = new HashMap<>();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3 && LocalDate.now().equals(LocalDate.parse(parts[2]))) {
                    double amount = Double.parseDouble(parts[0]);
                    String category = parts[1];

                    categoryTotals.put(category, categoryTotals.getOrDefault(category, 0.0) + amount);
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while reading the file.");
            e.printStackTrace();
        }

        return categoryTotals;
    }

    private static Map<String, Double> readSpendingData() {
        Map<String, Double> categoryTotals = new HashMap<>();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    double amount = Double.parseDouble(parts[0]);
                    String category = parts[1];

                    categoryTotals.put(category, categoryTotals.getOrDefault(category, 0.0) + amount);
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while reading the file.");
            e.printStackTrace();
        }

        return categoryTotals;
    }
}