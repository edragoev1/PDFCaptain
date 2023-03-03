import java.io.ByteArrayOutputStream;
import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class FileInfo {
    String fileName;
    String title = "";
    String creationDate;
    String numberOfPages = "0";
    String pageSize = "";
    String fileSize;

    public FileInfo(File file) throws Exception {
        this.fileName = file.getName();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        List<String> command = new ArrayList<>();
        command.add("pdfinfo");
        command.add(fileName);
        final var process = new ProcessBuilder(command).start();
        final var input = process.getInputStream();
        final var buf = new byte[4096];
        int len;
        while ((len = input.read(buf)) != -1) {
            baos.write(buf, 0, len);
        }
        final String[] lines = baos.toString().split("\\n");
        for (String line : lines) {
            final String[] tokens = line.split(":");
            if (tokens[0].equals("Title")) {
                title = tokens[1].trim();
            } else if (tokens[0].equals("Pages")) {
                numberOfPages = tokens[1].trim();
            } else if (tokens[0].equals("Page size")) {
                final String[] xy = tokens[1].split(" x ");
                if (xy[0].contains(".")) {
                    pageSize = xy[0].substring(0, xy[0].indexOf("."));
                } else {
                    pageSize = xy[0];
                }
                pageSize += " x ";
                if (xy[1].contains(".")) {
                    pageSize += xy[1].substring(0, xy[1].indexOf(".")) + xy[1].substring(xy[1].indexOf(" pts"));
                } else {
                    pageSize += xy[1];
                }
            }
        }
        final String timestamp = new Timestamp(file.lastModified()).toString();
        this.creationDate = timestamp.substring(0, timestamp.lastIndexOf('.'));
        this.fileSize = String.valueOf(file.length());
    }
}
