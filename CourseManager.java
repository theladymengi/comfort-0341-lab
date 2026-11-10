import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * CourseManager
 * -------------
 * Holds the list of courses and contains all the program logic:
 * the menu, adding, viewing, searching, totalling, saving and loading.
 *
 * Assignment requirements and where they are met:
 *  - RECURSION  : showMenu() re-displays the menu by calling itself (no while loop).
 *  - ITERATION  : the for loops in viewAllCourses(), computeTotalUnits() and findCourse(),
 *                 and the line-by-line while loop in loadFromFile().
 *  - STRING PROCESSING : readTrimmedInput() trims all text typed by the user,
 *                 and addCourse() converts course codes to uppercase.
 *  - VALIDATION / EXCEPTIONS : try/catch in readMenuChoice(), addCourse(),
 *                 saveToFile() and loadFromFile().
 *  - FILE I/O   : saveToFile() and loadFromFile() use a plain CSV text file.
 */
public class CourseManager {

    /** Name of the CSV file used for saving and loading. */
    private static final String FILE_NAME = "courses.csv";

    /** The list of all courses currently in memory. */
    private ArrayList<Course> courses;

    /** Reads keyboard input from the console. */
    private Scanner scanner;

    /** Constructor: starts with an empty course list and a console Scanner. */
    public CourseManager() {
        courses = new ArrayList<>();
        scanner = new Scanner(System.in);
    }

    // =====================================================================
    //  RECURSIVE MENU
    // =====================================================================

    /**
     * *** THIS IS THE RECURSIVE METHOD ***
     *
     * Shows the menu, reads the user's choice and performs the chosen action.
     * Then it CALLS ITSELF (showMenu()) to show the menu again, instead of
     * using a while loop.
     *
     *  - Base case (when the recursion stops): the user chooses 7 (Exit).
     *    The method returns without calling itself again.
     *  - Recursive case: any other choice, valid or invalid. After handling it,
     *    the method calls showMenu() again.
     *
     * Note: each menu action adds one method call to the Java call stack.
     * This is fine for normal use. A user would need thousands of actions in a
     * single run to cause a StackOverflowError, which is the main trade-off of
     * using recursion here instead of a loop.
     */
    public void showMenu() {
        System.out.println();
        System.out.println("========== STUDENT COURSE MANAGEMENT SYSTEM ==========");
        System.out.println("1) Add Course");
        System.out.println("2) View All Courses");
        System.out.println("3) Search Course by Code");
        System.out.println("4) Compute Total Units");
        System.out.println("5) Save to File");
        System.out.println("6) Load from File");
        System.out.println("7) Exit Program");
        System.out.println("======================================================");

        int choice;
        try {
            choice = readMenuChoice();
        } catch (NoSuchElementException e) {
            // The input stream has ended (e.g. Ctrl+D), so there is nothing more to read.
            System.out.println("\nNo more input. Exiting program.");
            return;
        }

        switch (choice) {
            case 1:
                addCourse();
                break;
            case 2:
                viewAllCourses();
                break;
            case 3:
                searchCourseByCode();
                break;
            case 4:
                computeTotalUnits();
                break;
            case 5:
                saveToFile();
                break;
            case 6:
                loadFromFile();
                break;
            case 7:
                exitProgram();
                return;   // BASE CASE: stop the recursion, do NOT call showMenu() again
            case -1:
                // Input was not a number. readMenuChoice() has already printed
                // the error, so nothing more is printed here.
                break;
            default:
                System.out.println("Error: Invalid choice. Please enter a number from 1 to 7.");
        }

        showMenu();   // RECURSIVE CALL: the method calls itself to display the menu again
    }

    /**
     * Reads the menu choice from the user and converts it to an integer.
     * If the user types something that is not a whole number (e.g. "abc"),
     * Integer.parseInt throws a NumberFormatException. We catch it, print an
     * error and return -1. showMenu() then skips its own "invalid choice"
     * message for -1, so only one error line is shown.
     *
     * @return the number entered, or -1 if the input was not a valid number
     */
    private int readMenuChoice() {
        String input = readTrimmedInput("Enter your choice (1-7): ");
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Error: '" + input + "' is not a number.");
            return -1;
        }
    }

    // =====================================================================
    //  STRING PROCESSING
    // =====================================================================

    /**
     * *** STRING PROCESSING REQUIREMENT ***
     *
     * Shows a prompt, reads one line of text from the user and TRIMS it,
     * which removes leading and trailing spaces. For example, "  CSC201  "
     * becomes "CSC201". All user text input in this program (menu choice,
     * course code, course title, credit unit, search code) is read through
     * this method, so stray spaces never get stored or cause failed searches.
     *
     * @param prompt the message shown to the user
     * @return the trimmed text the user typed
     */
    private String readTrimmedInput(String prompt) {
        System.out.print(prompt);
        String rawInput = scanner.nextLine();
        return rawInput.trim();   // STRING PROCESSING: remove surrounding whitespace
    }

    // =====================================================================
    //  MENU OPERATIONS (options 1 to 7)
    // =====================================================================

    /**
     * Option 1: Add Course.
     * Asks for the course code, title and credit unit, validates them and
     * adds a new Course to the list. Invalid data never crashes the program.
     * The catch blocks print a clear error and the user goes back to the menu.
     */
    private void addCourse() {
        System.out.println("\n--- Add Course ---");
        try {
            // STRING PROCESSING: code and title are trimmed by readTrimmedInput().
            // The code is also converted to UPPERCASE so codes are always stored
            // consistently (e.g. "mth101" is stored as "MTH101").
            String code = readTrimmedInput("Enter course code: ").toUpperCase();
            if (code.isEmpty()) {
                throw new IllegalArgumentException("Course code cannot be empty.");
            }
            // Duplicate check: two courses cannot share the same code.
            if (findCourse(code) != null) {
                throw new IllegalArgumentException("A course with code '" + code + "' already exists.");
            }

            String title = readTrimmedInput("Enter course title: ");
            if (title.isEmpty()) {
                throw new IllegalArgumentException("Course title cannot be empty.");
            }

            String unitsText = readTrimmedInput("Enter credit unit: ");
            int units = Integer.parseInt(unitsText);   // may throw NumberFormatException

            // The Course constructor does final validation (commas, units > 0).
            Course course = new Course(code, title, units);
            courses.add(course);
            System.out.println("Course '" + code + "' added successfully.");

        } catch (NumberFormatException e) {
            System.out.println("Error: Credit unit must be a whole number (e.g. 3). Course not added.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage() + " Course not added.");
        }
    }

    /**
     * Option 2: View All Courses.
     * Prints every course in a formatted table.
     */
    private void viewAllCourses() {
        System.out.println("\n--- All Courses ---");
        if (courses.isEmpty()) {
            System.out.println("No courses available.");
            return;
        }
        System.out.println(String.format("%-10s %-35s %5s", "CODE", "TITLE", "UNITS"));
        System.out.println("------------------------------------------------------");

        // *** ITERATION REQUIREMENT ***
        // A for-each loop visits every course in the list and prints it.
        for (Course course : courses) {
            System.out.println(course);   // calls Course.toString()
        }
        System.out.println("------------------------------------------------------");
        System.out.println("Number of courses: " + courses.size());
    }

    /**
     * Option 3: Search Course by Code.
     * Asks for a course code and displays the matching course, if any.
     * The search is case-insensitive, so "csc201" finds "CSC201".
     */
    private void searchCourseByCode() {
        System.out.println("\n--- Search Course ---");
        // STRING PROCESSING: the search code is trimmed by readTrimmedInput()
        String code = readTrimmedInput("Enter course code to search: ");
        if (code.isEmpty()) {
            System.out.println("Error: Course code cannot be empty.");
            return;
        }
        Course found = findCourse(code);
        if (found != null) {
            System.out.println("Course found:");
            System.out.println(String.format("%-10s %-35s %5s", "CODE", "TITLE", "UNITS"));
            System.out.println(found);
        } else {
            System.out.println("No course found with code '" + code + "'.");
        }
    }

    /**
     * Helper method: looks for a course with the given code.
     * Used by searchCourseByCode() and by addCourse() for the duplicate check.
     *
     * @param code the course code to look for
     * @return the matching Course, or null if there is none
     */
    private Course findCourse(String code) {
        // *** ITERATION REQUIREMENT ***
        // Loop through the list and compare each code, ignoring upper/lower case.
        for (Course course : courses) {
            if (course.getCourseCode().equalsIgnoreCase(code)) {
                return course;
            }
        }
        return null;
    }

    /**
     * Option 4: Compute Total Units.
     * Adds up the credit units of all courses and prints the total.
     */
    private void computeTotalUnits() {
        System.out.println("\n--- Total Units ---");
        int total = 0;

        // *** ITERATION REQUIREMENT ***
        // A for-each loop adds each course's credit unit to the running total.
        for (Course course : courses) {
            total += course.getCreditUnit();
        }
        System.out.println("Total credit units for " + courses.size() + " course(s): " + total);
    }

    /**
     * Option 5: Save to File.
     * Writes every course to courses.csv, one course per line in the form
     * code,title,units. Any existing file is overwritten.
     *
     * "try-with-resources" (the brackets after try) closes the file
     * automatically even if an error happens.
     */
    private void saveToFile() {
        System.out.println("\n--- Save to File ---");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            // *** ITERATION REQUIREMENT ***
            // Loop through the list and write each course as one CSV line.
            for (Course course : courses) {
                writer.write(course.toCsvLine());
                writer.newLine();
            }
            System.out.println(courses.size() + " course(s) saved to " + FILE_NAME + ".");
        } catch (IOException e) {
            System.out.println("Error: Could not save to file. " + e.getMessage());
        }
    }

    /**
     * Option 6: Load from File.
     * Reads courses.csv line by line and rebuilds the course list.
     *
     *  - Loading REPLACES the courses currently in memory.
     *  - Blank lines are ignored.
     *  - Bad lines (wrong format, non-numeric units, duplicates) are skipped
     *    with a warning instead of crashing the program.
     *  - If the file does not exist, a clear message is shown.
     */
    private void loadFromFile() {
        System.out.println("\n--- Load from File ---");

        // Load into a temporary list first. The current list is only replaced
        // if the whole file was read without an I/O error.
        ArrayList<Course> loaded = new ArrayList<>();
        int lineNumber = 0;
        int skipped = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;

            // *** ITERATION REQUIREMENT ***
            // A while loop reads the file one line at a time until readLine()
            // returns null, which means the end of the file has been reached.
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) {
                    continue;   // skip blank lines
                }
                try {
                    Course course = Course.fromCsvLine(line);
                    if (containsCode(loaded, course.getCourseCode())) {
                        throw new IllegalArgumentException("Duplicate course code '" + course.getCourseCode() + "'.");
                    }
                    loaded.add(course);
                } catch (IllegalArgumentException e) {
                    skipped++;
                    System.out.println("Warning: Skipped line " + lineNumber + ": " + e.getMessage());
                }
            }

            courses = loaded;   // replace the in-memory list with the loaded courses
            System.out.println(loaded.size() + " course(s) loaded from " + FILE_NAME
                    + (skipped > 0 ? " (" + skipped + " invalid line(s) skipped)." : "."));

        } catch (FileNotFoundException e) {
            System.out.println("Error: File '" + FILE_NAME + "' not found. Save some courses first (option 5).");
        } catch (IOException e) {
            System.out.println("Error: Could not read file. " + e.getMessage());
        }
    }

    /**
     * Helper method used while loading: checks whether a list already
     * contains a course with the given code (case-insensitive).
     */
    private boolean containsCode(ArrayList<Course> list, String code) {
        // *** ITERATION REQUIREMENT ***
        for (Course course : list) {
            if (course.getCourseCode().equalsIgnoreCase(code)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Option 7: Exit Program.
     * Prints a goodbye message. showMenu() then returns without calling
     * itself, which is the base case that ends the recursion.
     */
    private void exitProgram() {
        System.out.println("Exiting program. Goodbye!");
        scanner.close();
    }
}
