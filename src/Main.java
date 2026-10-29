import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.util.System.in);

    public static void main(String[] args) {
        // Step 1: Initialize Database Tables
        DatabaseManager.initializeDatabase();

        while (true) {
            System.out.println("\n🏦 --- Welcome to SecureBank --- 🏦");
            System.out.println("1. Create New Account");
            System.out.println("2. Log In / Perform Transactions");
            System.out.println("3. Exit");
            System.out.print("Select an option: ");
            
            int choice = getIntInput();

            switch (choice) {
                case 1:
                    registerAccount();
                    break;
                case 2:
                    loginSession();
                    break;
                case 3:
                    System.out.println("Thank you for using SecureBank. Goodbye!");
                    return;
                default:
                    System.out.println("❌ Invalid choice. Try again.");
            }
        }
    }

    private static void registerAccount() {
        System.out.print("Enter Account Number: ");
        String accNum = scanner.next();
        System.out.print("Enter Name: ");
        scanner.nextLine(); // Clear scanner buffer
        String name = scanner.nextLine();
        System.out.print("Create Password: ");
        String password = scanner.next();

        if (DatabaseManager.createAccount(accNum, name, password)) {
            System.out.println("✅ Account created successfully!");
        }
    }

    private static void loginSession() {
        System.out.print("Enter Account Number: ");
        String accNum = scanner.next();
        System.out.print("Enter Password: ");
        String password = scanner.next();

        if (!DatabaseManager.authenticate(accNum, password)) {
            System.out.println("❌ Authentication failed. Incorrect credentials.");
            return;
        }

        System.out.println("🔓 Access Granted!");
        while (true) {
            System.out.println("\n--- Transaction Menu ---");
            System.out.println("1. Check Balance");
            System.out.println("2. Deposit Money");
            System.out.println("3. Withdraw Money (Overdraft Protection)");
            System.out.println("4. Delete Account");
            System.out.println("5. Log Out");
            System.out.print("Choose option: ");

            int action = getIntInput();
            switch (action) {
                case 1:
                    System.out.printf("Current Balance: ₹%.2f\n", DatabaseManager.getBalance(accNum));
                    break;

                case 2:
                    System.out.print("Enter amount to deposit: ");
                    double depAmount = getDoubleInput();
                    if (depAmount > 0) {
                        double newBal = DatabaseManager.getBalance(accNum) + depAmount;
                        DatabaseManager.updateBalance(accNum, newBal);
                        System.out.printf("✅ Deposited successfully. New Balance: ₹%.2f\n", newBal);
                    } else {
                        System.out.println("❌ Invalid deposit amount.");
                    }
                    break;

                case 3:
                    System.out.print("Enter amount to withdraw: ");
                    double withdrawAmount = getDoubleInput();
                    double currentBal = DatabaseManager.getBalance(accNum);

                    if (withdrawAmount <= 0) {
                        System.out.println("❌ Invalid withdrawal amount.");
                    } else if (withdrawAmount > currentBal) {
                        System.out.println("❌ Transaction Denied: Overdraft Protection activated.");
                    } else {
                        double newBal = currentBal - withdrawAmount;
                        DatabaseManager.updateBalance(accNum, newBal);
                        System.out.printf("✅ Withdrawal successful. Remaining Balance: ₹%.2f\n", newBal);
                    }
                    break;

                case 4:
                    System.out.print("Are you sure you want to delete this account permanently? (yes/no): ");
                    String confirm = scanner.next();
                    if (confirm.equalsIgnoreCase("yes")) {
                        if (DatabaseManager.deleteAccount(accNum)) {
                            System.out.println("💥 Account deleted successfully. Logging out.");
                            return;
                        }
                    } else {
                        System.out.println("Account deletion canceled.");
                    }
                    break;

                case 5:
                    System.out.println("Logged out successfully.");
                    return;

                default:
                    System.out.println("❌ Invalid transaction action.");
            }
        }
    }

    private static int getIntInput() {
        while (!scanner.hasNextInt()) {
            System.out.print("❌ Invalid input type. Please enter a valid number: ");
            scanner.next();
        }
        return scanner.nextInt();
    }

    private static double getDoubleInput() {
        while (!scanner.hasNextDouble()) {
            System.out.print("❌ Invalid entry. Enter numeric amount: ");
            scanner.next();
        }
        return scanner.nextDouble();
    }
}
