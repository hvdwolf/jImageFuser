This version is using gradle as build system. Import the project as gradle project and all will be setup correctly.

All IntelliJ specific files must not be within the source base.

# GUI Builder settings
Currently, you must define that the GUI Desinger outputs the form into Java Code. Otherwise, it would not create a proper UI and would not be able to build without IntelliJ IDEA.

**Goto:** *Settings -> Editor -> GUI Designer* and set **Generate GUI into: Java source code**

## Reason
Java does not know the concept of partial classes, like C# does. IntelliJ IDEA GUI Designer would build the .form with the .java file into the class file **directly** and any other build system would never know what happened. So Gradle would not be able to build the code as jetbrains does.

Jetbrains uses their own libraries **javac2.jar**, **forms_rt.jar** and some other libs to build it. The configuration to set it up with gradle is not straight forward, so currently the easiest way to do it is using the settings as defined above.
