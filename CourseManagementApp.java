/**
 * CourseManagementApp
 * -------------------
 * Entry point of the Student Course Management System.
 *
 * main() creates a CourseManager and calls showMenu() ONCE.
 * Every later menu display comes from showMenu() calling itself recursively,
 * so there is no loop here.
 *
 * Compile:  javac *.java
 * Run:      java CourseManagementApp
 */
public class CourseManagementApp {

    /** Program starts here. */
    public static void main(String[] args) {
        CourseManager manager = new CourseManager();
        manager.showMenu();   // starts the recursive menu
    }
}
