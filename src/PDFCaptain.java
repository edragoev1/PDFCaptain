import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.eclipse.swt.SWT.KeyDown;

public class PDFCaptain {
    public static void main(String[] args) throws Exception {
        java.util.List<FileInfo> list = getFileList(".");

        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setText("PDF Captain");
        shell.setLayout(new GridLayout());

        final Table table = new Table(shell,
                SWT.VIRTUAL | SWT.SINGLE | SWT.BORDER | SWT.FULL_SELECTION);
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        table.setLinesVisible(true);
        table.setHeaderVisible(true);

        TableColumn column1 = new TableColumn(table, SWT.NONE);
        column1.setText("File Name");
        TableColumn column2 = new TableColumn(table, SWT.NONE);
        column2.setText("Title");
        TableColumn column3 = new TableColumn(table, SWT.NONE);
        column3.setText("Creation Date");
        TableColumn column4 = new TableColumn(table, SWT.NONE);
        column4.setText("Pages");
        column4.setAlignment(SWT.RIGHT);
        TableColumn column5 = new TableColumn(table, SWT.NONE);
        column5.setText("Page Size");
        TableColumn column6 = new TableColumn(table, SWT.NONE);
        column6.setText("File Size");
        column6.setAlignment(SWT.RIGHT);

        table.addListener(SWT.MouseDoubleClick, event -> {
            TableItem[] items = table.getSelection();
            if (items.length > 0) {
                openPrintDialog(shell, items);
            }
        });
        table.addListener(KeyDown, event -> {
            TableItem[] items = table.getSelection();
            if (event.keyCode == SWT.CR) {
                openPrintDialog(shell, items);
            }
        });

        List<String[]> tableData = new ArrayList<>();
        for (FileInfo fileInfo : list) {
            String[] row = new String[]{
                    fileInfo.fileName,
                    fileInfo.title,
                    fileInfo.creationDate,
                    fileInfo.numberOfPages,
                    fileInfo.pageSize,
                    fileInfo.fileSize
            };
            tableData.add(row);
            TableItem item = new TableItem(table, SWT.NONE);
            item.setText(row);
        }
        for (TableColumn column : table.getColumns()) {
            column.pack();
        }
        column4.setWidth(column4.getWidth() + 20);

        table.addListener(SWT.SetData, event -> {
            TableItem item = (TableItem) event.item;
            String[] row = tableData.get(table.indexOf(item));
            item.setText(new String[]{
                    row[0],
                    row[1],
                    row[2],
                    row[3],
                    row[4],
                    row[5]
            });
        });

        Listener sortListener = getSortListener(table, tableData);
        column1.addListener(SWT.Selection, sortListener);
        column2.addListener(SWT.Selection, sortListener);
        column3.addListener(SWT.Selection, sortListener);
        column4.addListener(SWT.Selection, sortListener);
        column5.addListener(SWT.Selection, sortListener);
        column6.addListener(SWT.Selection, sortListener);
        table.setSortDirection(SWT.UP);
        table.setSortColumn(column1);

        final Composite composite = new Composite(shell, SWT.NONE);
        composite.setLayout(new GridLayout(3, false));
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        final Label label1 = new Label(composite, SWT.LEFT);
        label1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final Button button1 = new Button(composite, SWT.PUSH);
        GridData gridData = new GridData(SWT.END, SWT.FILL, false, false);
        gridData.widthHint = 100;
        button1.setLayoutData(gridData);
        button1.setText("Preview");
        button1.addListener(SWT.Selection, event -> {
            TableItem[] items = table.getSelection();
            if (items.length > 0) {
                ProcessBuilder pb = new ProcessBuilder("firefox", items[0].getText(0));
                try {
                    pb.start();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        final Button button2 = new Button(composite, SWT.PUSH);
        gridData = new GridData(SWT.END, SWT.FILL, false, false);
        gridData.widthHint = 100;
        button2.setLayoutData(gridData);
        button2.setText("Print");
        button2.addListener(SWT.Selection, event -> {
            TableItem[] items = table.getSelection();
            if (items.length > 0) {
                openPrintDialog(shell, items);
            }
        });

        table.addListener(SWT.Selection, event -> {
            TableItem[] items = table.getSelection();
            if (items.length > 0) {
                label1.setText(items[0].getText(0));
            }
        });

        setMenu(shell, table);

        shell.setSize(1024, 768);
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.dispose();
    }

    private static Listener getSortListener(final Table table, final List<String[]> tableData) {
        return event -> {
            // Determine new sort column and direction
            TableColumn sortColumn = table.getSortColumn();
            TableColumn clickedColumn = (TableColumn) event.widget;
            int dir = table.getSortDirection();
            if (clickedColumn == sortColumn) {
                dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
            } else {
                table.setSortColumn(clickedColumn);
                dir = SWT.UP;
            }

            // Sort the data based on column and direction
            TableColumn[] columns = table.getColumns();
            final int direction = dir;
            tableData.sort((e1, e2) -> {
                if (clickedColumn == columns[0]) {
                    String s1 = e1[0];
                    String s2 = e2[0];
                    if (s1.equalsIgnoreCase(s2)) {
                        return 0;
                    }
                    if (direction == SWT.UP) {
                        return s1.compareToIgnoreCase(s2) < 0 ? -1 : 1;
                    }
                    return s1.compareToIgnoreCase(s2) < 0 ? 1 : -1;
                } else if (clickedColumn == columns[1]) {
                    String s1 = e1[1];
                    String s2 = e2[1];
                    if (s1.equalsIgnoreCase(s2)) {
                        return 0;
                    }
                    if (direction == SWT.UP) {
                        return s1.compareToIgnoreCase(s2) < 0 ? -1 : 1;
                    }
                    return s1.compareToIgnoreCase(s2) < 0 ? 1 : -1;
                } else if (clickedColumn == columns[2]) {
                    Timestamp t1 = Timestamp.valueOf(e1[2]);
                    Timestamp t2 = Timestamp.valueOf(e2[2]);
                    if (t1.equals(t2)) {
                        return 0;
                    }
                    if (direction == SWT.UP) {
                        return t1.compareTo(t2) < 0 ? -1 : 1;
                    }
                    return t1.compareTo(t2) < 0 ? 1 : -1;
                } else if (clickedColumn == columns[3]) {
                    int i1 = Integer.parseInt(e1[3]);
                    int i2 = Integer.parseInt(e2[3]);
                    if (i1 == i2) {
                        return 0;
                    }
                    if (direction == SWT.UP) {
                        return i1 < i2 ? -1 : 1;
                    }
                    return i1 < i2 ? 1 : -1;
                } else if (clickedColumn == columns[4]) {
                    String s1 = e1[4];
                    String s2 = e2[4];
                    if (s1.equalsIgnoreCase(s2)) {
                        return 0;
                    }
                    if (direction == SWT.UP) {
                        return s1.compareToIgnoreCase(s2) < 0 ? -1 : 1;
                    }
                    return s1.compareToIgnoreCase(s2) < 0 ? 1 : -1;
                } else if (clickedColumn == columns[5]) {
                    int i1 = Integer.parseInt(e1[5]);
                    int i2 = Integer.parseInt(e2[5]);
                    if (i1 == i2) {
                        return 0;
                    }
                    if (direction == SWT.UP) {
                        return i1 < i2 ? -1 : 1;
                    }
                    return i1 < i2 ? 1 : -1;
                }
                return 0;
            });
            // Update data displayed in table
            table.setSortDirection(dir);
            table.clearAll();
        };
    }

    private static void setMenu(final Shell shell, final Table table) {
        final Menu menu = new Menu(shell, SWT.POP_UP);
        final MenuItem item1 = new MenuItem(menu, SWT.PUSH);
        item1.setText("Preview");
        item1.addListener(SWT.Selection, event -> {
            final TableItem[] items = table.getSelection();
            if (items.length > 0) {
                final ProcessBuilder pb = new ProcessBuilder("firefox", items[0].getText(0));
                try {
                    pb.start();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        final MenuItem item2 = new MenuItem(menu, SWT.PUSH);
        item2.setText("Print");
        item2.addListener(SWT.Selection, event -> {
            final TableItem[] items = table.getSelection();
            if (items.length > 0) {
                openPrintDialog(shell, items);
            }
        });
        table.setMenu(menu);
    }

    private static void openPrintDialog(final Shell shell, final TableItem[] items) {
        final Shell dialog = new Shell(shell,
                SWT.CLOSE | SWT.TITLE | SWT.BORDER | SWT.OK | SWT.APPLICATION_MODAL);
        dialog.setText("Print Dialog");
        dialog.setSize(640, 480);
        dialog.setLocation(
                shell.getLocation().x + (shell.getSize().x - dialog.getSize().x) / 2,
                shell.getLocation().y + (shell.getSize().y - dialog.getSize().y) / 2);
        dialog.setLayout(new GridLayout());

        final Label label = new Label(dialog, SWT.NONE);
        label.setText("You are about to print: " + items[0].getText(0));
        label.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false));

        final Table table = new Table(dialog,
                SWT.SINGLE | SWT.BORDER | SWT.FULL_SELECTION | SWT.NO_SCROLL);
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        gridData.heightHint = 200;
        table.setLayoutData(gridData);
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        String[] titles = {"Printer", "IP Address", "Location"};
        for (String title : titles) {
            TableColumn column = new TableColumn(table, SWT.NONE);
            column.setText(title);
        }
        TableItem item = new TableItem(table, SWT.NONE);
        item.setText(0, "Xerox Phaser 6180");
        item.setText(1, "192.168.1.101");
        item.setText(2, "Eugene's Office");
        item = new TableItem(table, SWT.NONE);
        item.setText(0, "Lexmark B2236dw");
        item.setText(1, "192.168.1.103");
        item.setText(2, "Mei's Office");
        for (TableColumn column : table.getColumns()) {
            column.pack();
        }

        Composite composite = new Composite(dialog, SWT.NONE);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final Label label1 = new Label(dialog, SWT.LEFT);
        label1.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));

        final Button button1 = new Button(composite, SWT.RADIO);
        gridData = new GridData(SWT.LEFT, SWT.FILL, false, false);
        gridData.widthHint = 150;
        gridData.heightHint = 30;
        button1.setLayoutData(gridData);
        button1.setText("All Pages");
        button1.setSelection(true);

        final Button button2 = new Button(composite, SWT.RADIO);
        gridData = new GridData(SWT.LEFT, SWT.FILL, false, false);
        gridData.widthHint = 150;
        gridData.heightHint = 30;
        button2.setLayoutData(gridData);
        button2.setText("Pages:");

        final Composite composite2 = new Composite(composite, SWT.NONE);
        composite2.setLayout(new GridLayout(4, false));
        composite2.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));

        final Label label2 = new Label(composite2, SWT.LEFT);
        label2.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        label2.setText("    ");

        final Text text1 = new Text(composite2, SWT.SINGLE | SWT.BORDER);
        gridData = new GridData(SWT.LEFT, SWT.FILL, false, false);
        gridData.widthHint = 40;
        text1.setLayoutData(gridData);
        text1.setEnabled(false);
        text1.setToolTipText("First page");

        final Label label3 = new Label(composite2, SWT.LEFT);
        label3.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        label3.setText("-");

        final Text text2 = new Text(composite2, SWT.SINGLE | SWT.BORDER);
        gridData = new GridData(SWT.LEFT, SWT.FILL, false, false);
        gridData.widthHint = 40;
        text2.setLayoutData(gridData);
        text2.setEnabled(false);
        text2.setToolTipText("Last page or leave empty to print until the end of the document");

        final Label label4 = new Label(composite, SWT.LEFT);
        label4.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
        label4.setText("      Specify page range, e.g. 7 - 11");
        label4.setForeground(new Color(96, 96, 255));

        button1.addListener(SWT.Selection, event -> {
            text1.setText("");
            text1.setEnabled(false);
            text1.setEditable(false);
            text2.setText("");
            text2.setEnabled(false);
            text2.setEditable(false);
        });

        button2.addListener(SWT.Selection, event -> {
            text1.setEnabled(true);
            text1.setEditable(true);
            text2.setEnabled(true);
            text2.setEditable(true);
            text1.setFocus();
        });

        composite = new Composite(dialog, SWT.NONE);
        composite.setLayout(new GridLayout(2, false));
        composite.setLayoutData(new GridData(SWT.END, SWT.BOTTOM, true, false));

        final Button button3 = new Button(composite, SWT.PUSH);
        gridData = new GridData(SWT.END, SWT.FILL, false, false);
        gridData.widthHint = 100;
        button3.setLayoutData(gridData);
        button3.setText("Cancel");
        button3.setSelection(true);
        button3.addListener(SWT.Selection, event -> {
            dialog.close();
        });

        final Button button4 = new Button(composite, SWT.PUSH);
        gridData = new GridData(SWT.END, SWT.FILL, false, false);
        gridData.widthHint = 100;
        button4.setLayoutData(gridData);
        button4.setText("Print");
        button4.addListener(SWT.Selection, event -> {
            try {
                printFile(table.getSelection()[0].getText(1),
                        items[0].getText(0), text1.getText(), text2.getText());
                dialog.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        dialog.open();
    }

    private static java.util.List<FileInfo> getFileList(final String directory) throws Exception {
        final java.util.List<FileInfo> fileList = new ArrayList<>();
        final File dir = new File(directory);
        if (dir.isDirectory()) {
            final File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        if (file.getName().toLowerCase().endsWith(".pdf")) {
                            fileList.add(new FileInfo(file));
                        }
                    }
                }
            }
        }
        fileList.sort((e1, e2) -> {
            if (e1.fileName.equalsIgnoreCase(e2.fileName)) {
                return 0;
            }
            return e1.fileName.compareToIgnoreCase(e2.fileName) > 0 ? 1 : -1;
        });
        return fileList;
    }

    private static void printFile(
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
        writeToSocket(ipAddress, command);
    }

    private static void writeToSocket(
            final String ipAddress, final List<String> command) throws IOException {
/*
        System.out.println(ipAddress);
        for (String el : command) {
            System.out.print(el + " ");
        }
        System.out.println();
*/
        final var process = new ProcessBuilder(command).start();
        final var input = process.getInputStream();
        final var buf = new byte[4096];
        try (final var socket = new Socket(ipAddress, 9100)) {
            final var output = socket.getOutputStream();
            int len;
            while ((len = input.read(buf)) != -1) {
                output.write(buf, 0, len);
            }
            output.flush();
        }
    }

    private void printFileUsingGS(
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
        writeToSocket(ipAddress, command);
    }
}

class FileInfo {
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
