package moaflow.sink;

/**
 * Prints the learning curve to a table form.
 *
 * @author Truong To (todinhtruong at gmail dot com)
 * Simplifies the implementation of Flip Tables (Github project https://github.com/JakeWharton/flip-tables)
 */
public class DrawTable {
    protected int[] columnWidths = null;
    String[] dataRow = null;

    /** sets the width of each column**/
    private void setColumnWidth(){
        this.columnWidths = new int[this.dataRow.length];
        for (int i = 0; i < this.dataRow.length; i++) {
            this.columnWidths[i] = this.dataRow[i].length() > 25 ? this.dataRow[i].length() : 25;
        }
    }

    /** Gets the header of learning curve
     * @return the output string in table form
     * **/
    public StringBuilder headerLine(String str){
        this.dataRow = str.split(",");
        setColumnWidth();
        StringBuilder output = new StringBuilder();
        String format = "╔═╤═╗";
        for (int col = 0; col < this.dataRow.length; col++) {
            output.append(col == 0 ? format.charAt(0) : format.charAt(2));
            output.append(String.format(" %1$-" + columnWidths[col] + "s ", "").replace(' ', format.charAt(1)));
        }
        output.append(format.charAt(4)).append('\n');

        for (int col = 0; col < this.dataRow.length; col++) {
            output.append(col == 0 ? '║' : '│');
            output.append(String.format(" %1$-" + this.columnWidths[col] + "s ", this.dataRow[col]));
        }
        output.append("║\n");

        format = "╚═╧═╝";
        for (int col = 0; col < this.dataRow.length; col++) {
            output.append(col == 0 ? format.charAt(0) : format.charAt(2));
            output.append(String.format(" %1$-" + columnWidths[col] + "s ", "").replace(' ', format.charAt(1)));
        }
        output.append(format.charAt(4)).append('\n');

        return output;
    }

    /** Gets a body line of learning curve
     * @return the output string in table form
     * **/
    public StringBuilder bodyLine(String str){
        this.dataRow = str.split(",");
        StringBuilder output = new StringBuilder();
        for (int col = 0; col < this.dataRow.length; col++) {
            output.append(col == 0 ? '║' : '│');
            if(this.dataRow[col].length() > this.columnWidths[col])
                this.columnWidths[col] = this.dataRow[col].length();
            output.append(String.format(" %1$-" + this.columnWidths[col] + "s ", this.dataRow[col]));
        }

        output.append("║\n");

        String format = "╙─┴─╜";

        for (int col = 0; col < this.dataRow.length; col++) {
            output.append(col == 0 ? format.charAt(0) : format.charAt(2));
            output.append(String.format(" %1$-" + columnWidths[col] + "s ", "").replace(' ', format.charAt(1)));
        }
        output.append(format.charAt(4)).append('\n');

        return output;
    }
}

