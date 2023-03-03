import java.util.ArrayList;
import java.util.List;

public class Xpdf {
    protected static void print(
            final String ipAddress,
            final String fileName,
            final String firstPage,
            final String lastPage) throws Exception {
        final List<String> command = new ArrayList<>();
        command.add("pdftops");
        command.add("-q");
        if (firstPage != null) {
            command.add("-f");
            command.add(firstPage);
        }
        if (lastPage != null) {
            command.add("-l");
            command.add(lastPage);
        }
        command.add("-level3");
        command.add("-paper");
        command.add("letter");
        command.add(fileName);
        command.add("-");
        PDFCaptain.writeToSocket(ipAddress, command);
    }
}
