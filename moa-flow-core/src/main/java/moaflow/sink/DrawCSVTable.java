package moaflow.sink;

public class DrawCSVTable {

    public StringBuilder csv(String str) {
        StringBuilder output = new StringBuilder();
        output.append(str).append('\n');
        return output;
    }

}