/**
 * Course
 * ------
 * Represents a single course with a code, a title and a credit unit value.
 *
 * OOP concepts shown here:
 *  - Encapsulation: all fields are private and can only be read through getters.
 *  - Data validation in the constructor: a Course object can never be created
 *    with invalid data (empty code/title, commas, or non-positive units).
 */
public class Course {

    // ---------- Fields (private = encapsulated) ----------
    private final String courseCode;   // e.g. "CSC201"
    private final String courseTitle;  // e.g. "Data Structures"
    private final int creditUnit;      // e.g. 3

    /**
     * Constructor: creates a new Course after checking that the data is valid.
     *
     * @param courseCode  the course code (must not be empty or contain commas)
     * @param courseTitle the course title (must not be empty or contain commas)
     * @param creditUnit  the number of credit units (must be greater than 0)
     * @throws IllegalArgumentException if any value is invalid
     */
    public Course(String courseCode, String courseTitle, int creditUnit) {
        if (courseCode == null || courseCode.isEmpty()) {
            throw new IllegalArgumentException("Course code cannot be empty.");
        }
        if (courseTitle == null || courseTitle.isEmpty()) {
            throw new IllegalArgumentException("Course title cannot be empty.");
        }
        // Commas are not allowed because we use commas to separate values in the CSV file.
        if (courseCode.contains(",") || courseTitle.contains(",")) {
            throw new IllegalArgumentException("Course code and title cannot contain commas (,).");
        }
        if (creditUnit <= 0) {
            throw new IllegalArgumentException("Credit unit must be a positive whole number.");
        }
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.creditUnit = creditUnit;
    }

    // ---------- Getters ----------

    /** Returns the course code. */
    public String getCourseCode() {
        return courseCode;
    }

    /** Returns the course title. */
    public String getCourseTitle() {
        return courseTitle;
    }

    /** Returns the number of credit units. */
    public int getCreditUnit() {
        return creditUnit;
    }

    /**
     * Converts this course into one line of CSV text, used when saving to a file.
     * Example output: "CSC201,Data Structures,3"
     */
    public String toCsvLine() {
        return courseCode + "," + courseTitle + "," + creditUnit;
    }

    /**
     * Builds a Course object from one line of CSV text, used when loading from a file.
     * This is a "static factory method": it is called on the class (Course.fromCsvLine(...)),
     * not on an existing object.
     *
     * @param line a line such as "CSC201,Data Structures,3"
     * @return a new Course object
     * @throws IllegalArgumentException if the line does not have exactly 3 valid parts
     */
    public static Course fromCsvLine(String line) {
        String[] parts = line.split(",");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Expected 3 values (code,title,units) but found " + parts.length + ".");
        }
        String code = parts[0].trim();
        String title = parts[1].trim();
        int units;
        try {
            units = Integer.parseInt(parts[2].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Credit unit '" + parts[2].trim() + "' is not a number.");
        }
        // The constructor performs the remaining validation.
        return new Course(code, title, units);
    }

    /**
     * Returns a neatly formatted row for displaying this course in a table.
     * %-10s = left-aligned text, 10 characters wide; %5d = right-aligned number, 5 wide.
     */
    @Override
    public String toString() {
        return String.format("%-10s %-35s %5d", courseCode, courseTitle, creditUnit);
    }
}
