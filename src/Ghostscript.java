import java.util.ArrayList;
import java.util.List;

public class Ghostscript {
    public static void print(
            final String ipAddress,
            final String fileName,
            final String pageList) throws Exception {
        List<String> command = new ArrayList<>();
        command.add("/usr/local/bin/gs");
        command.add("-dQUIET");
        command.add("-dSAFER");
        command.add("-dBATCH");
        command.add("-dNOPAUSE");
        command.add("-dFIXEDMEDIA");
        command.add("-dPSFitPage");
        command.add("-sPAPERSIZE=letter");
        command.add("-sDEVICE=ps2write");
        command.add("-sOutputFile=-");
        if (pageList != null) {
            command.add("-sPageList=" + pageList);
        }
        command.add("-c");
        command.add("save");
        command.add("pop");
        command.add("-f");
        command.add(fileName);
        PDFCaptain.writeToSocket(ipAddress, command);
    }
}
