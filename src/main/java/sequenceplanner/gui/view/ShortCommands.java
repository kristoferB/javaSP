package sequenceplanner.gui.view;

/**
*
* @author Peter
*/
public class ShortCommands {
    public String getString(){
        String string = "Select all operations: Ctrl + A \n\n"
                + "Select node: Ctrl + Shift + A \n\n"
                + "Select group of operations: Ctrl + G\n\n"
                + "Select sequence operations: Ctrl + S\n\n"
                + "Delete operation: Delete\n\n"
                + "Undo: Ctrl + Z\n\n"
                + "Redo: Ctrl + Y\n\n"
                + "Copy: Ctrl + C\n\n"
                + "Paste: Ctrl + V\n\n"
                + "Cut: Ctrl + X\n\n"
                + "Zoom: Ctrl + Scroll\n\n";
        return string;
    }
}