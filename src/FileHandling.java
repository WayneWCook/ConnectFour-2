/* FileHandling.java
 * Wayne Cook
 * 1 November 2018
 * Purpose:
 *   This file contains sample code for reading from and writing to a file. It has one private piece of
 *   information that must be accessed through methods that will set and retrieve (get) the information.
 *   First the java.io.* libraries need to be important.
 */

// import sun.java2d.pipe.BufferedRenderPipe;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;

/* Start of the File Handling class */
public class FileHandling {
    /* Private variables are accessible only to methods within this class.
     * There are some checks for validity I want to do before using the new file. */
    private String fileName = "";
    private File file;
    private PrintWriter output;
    private Scanner input;
    private boolean debugFlag = false;

    /* Define the open and close File access methods.
     * Since openFile calls closeFile, closeFile must come first.
     */

    public boolean openFile(String newFileName) {            // Set the file name
        boolean retVal = false;
        if (newFileName.length() > 0) {
            retVal = true;
           if (fileName.length() > 0) {                      // If there is already a File Name, must close
               this.closeFile();                             // that file first.
           }
           fileName = newFileName;
           file = new File(fileName);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch(IOException ex) {
                    System.out.println("file: " + file + " cannot be created.");
                }
            }
            if(file.exists() && !file.isDirectory()) {
               try {
                   output = new PrintWriter(file);
                   input = new Scanner(file);
                   retVal = true;
               } catch (FileNotFoundException exception) {
                   retVal = false;
               }
               if (retVal && debugFlag) {
                   System.out.println("Try to write and read from the file");
                   try {
                       output.println("I can write to the file");
                       output.flush();
                       System.out.println(input.nextLine());
                   }
                   catch (Exception ex) {
                       System.out.println("file: " + file + " cannot be opened.");
                   }
               }
           }

        }
        return retVal;
    }

    public boolean closeFile() {
        boolean retVal = false;
        try {
            input.close();
            output.close();
            retVal = true;
        }
        catch (Exception exception) {
            retVal = false;
        }
        return retVal;
    }

    /* Now for the override constructor and destructor, this may not work at the moment. It should.*/
    public FileHandling(String newFileName) {
        this.openFile(newFileName);
    }

    /* In case I cannot find a problem, I can initialize the debugFlag to print more info */
    public FileHandling(String newFileName, boolean debug) {
        debugFlag = debug;
        this.openFile(newFileName);
    }
    /* Go back to the beginning of the file for output */
    public boolean readReset() {
        boolean retVal = true;
        try {
            // Scanner has no way to rewind() the file or seek(0). Maybe a difference input would be better.
            //input.reset();                                           // Go back to the beginning of the file.
           input.close();                                              // Close the file
           input = new Scanner(file);                                  // Then create a new instance.
        } catch (Exception exception) {
            retVal = false;
        }
        return retVal;
    }

    /* ArrayList is the only subject in this file that I have not covered in the CSC160 Java class.
     * Borrowing and modifying the C++ official site for vectors (http://www.cplusplus.com/reference/vector/vector/)
     * ArrayLists are sequence containers representing arrays that can change in size.
     *
     * Just like arrays,  ArrayLists use contiguous storage locations for their elements, which means that
     * their elements can also be accessed using offsets on regular pointers to its elements, and just
     * as efficiently as in arrays. But unlike arrays, their size can change dynamically, with their
     * storage being handled automatically by the container.
     +
     + The reason I am using a vector here is to allow a variable number of Strings to be returned.
     * A line may have different number of Strings seperated by spaces or other delimeters.
     */
    void splitLine(ArrayList<String> splitArray, String inString, char delim) {
        int len = inString.length();
        int j = 0, k = 0;
        splitArray.clear();
        for (int i = 0; i < len; i++) {
            if (inString.charAt(i) == ',') {
                String elem = inString.substring(j, i);
                System.out.println("Element " + k++ + " is " + elem);
                splitArray.add(elem);
                j = i + 1;
            }
            if (i == len - 1) {
                String elem = inString.substring(j);
                System.out.println("Element " + k++ + " is " + elem);
                splitArray.add(elem);
            }
        }
    }


    /* Have common named routines for file I/O and user interface (keyboard and display) I/O
     * retType True returns a string message while false returns a NULL ptr. */
    public String getLine(boolean retType) {
        String retVal = "";
        try {
            retVal = input.nextLine();
        } catch (Exception exception) {
            if (retType) retVal = "No line found";
            else retVal = null;
        }
        return retVal;
    }

    public void writeLine(String line) {
        output.println(line);
        output.flush();
    }


}
