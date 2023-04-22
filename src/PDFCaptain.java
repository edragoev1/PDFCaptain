/**
 * Copyright (C) 2023 Innovatics Inc.
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.eclipse.swt.SWT.KeyDown;

public class PDFCaptain {
    static String documentsFolder;

    public static void main(String[] args) throws Exception {
        String homeFolder = Objects.requireNonNull(PDFCaptain.class.getResource(".")).getPath();
        int index = homeFolder.indexOf("/out/production/");
        if (index > 0) {
            homeFolder = homeFolder.substring(0, index);
        }
        if (args.length > 0) {
            documentsFolder = args[0];
        } else {
            documentsFolder = homeFolder + "/pdf";
        }

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
                new PrintDialog(shell, items);
            }
        });
        table.addListener(KeyDown, event -> {
            TableItem[] items = table.getSelection();
            if (event.keyCode == SWT.CR) {
                new PrintDialog(shell, items);
            }
        });

        List<String[]> tableData = new ArrayList<>();
        setTableData(table, tableData, getFileList(documentsFolder));

        column4.setWidth(column4.getWidth() + 20);
        table.addListener(SWT.SetData, event -> {
            TableItem item = (TableItem) event.item;
            int index2 = table.indexOf(item);
            if (index2 < tableData.size()) {
                String[] row = tableData.get(index2);
                item.setText(row);
            }
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
        composite.setLayout(new GridLayout(4, false));
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        final Label label1 = new Label(composite, SWT.LEFT);
        label1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final Button button1 = new Button(composite, SWT.PUSH);
        GridData gridData = new GridData(SWT.END, SWT.FILL, false, false);
        gridData.widthHint = 100;
        button1.setLayoutData(gridData);
        button1.setText("Folder");
        button1.addListener(SWT.Selection, event -> {
            DirectoryDialog dialog = new DirectoryDialog(shell);
            String platform = SWT.getPlatform();
            dialog.setFilterPath (platform.equals("win32") ? "c:\\" : "/home/eugene");
            final String selectedFolder = dialog.open();
            documentsFolder = selectedFolder;
            try {
                setTableData(table, tableData, getFileList(documentsFolder));
            } catch (Exception exception) {
            }
        });

        final Button button2 = new Button(composite, SWT.PUSH);
        gridData = new GridData(SWT.END, SWT.FILL, false, false);
        gridData.widthHint = 100;
        button2.setLayoutData(gridData);
        button2.setText("Preview");
        button2.addListener(SWT.Selection, event -> {
            TableItem[] items = table.getSelection();
            if (items.length > 0) {
                ProcessBuilder pb = new ProcessBuilder(
                        "firefox", documentsFolder + "/" + items[0].getText(0));
                try {
                    pb.start();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        final Button button3 = new Button(composite, SWT.PUSH);
        gridData = new GridData(SWT.END, SWT.FILL, false, false);
        gridData.widthHint = 100;
        button3.setLayoutData(gridData);
        button3.setText("Print");
        button3.addListener(SWT.Selection, event -> {
            TableItem[] items = table.getSelection();
            if (items.length > 0) {
                new PrintDialog(shell, items);
            }
        });

        table.addListener(SWT.Selection, event -> {
            TableItem[] items = table.getSelection();
            if (items.length > 0) {
                label1.setText(documentsFolder + "/" + items[0].getText(0));
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

    private static void setTableData(Table table, List<String[]> tableData, List<FileInfo> list) {
        table.clearAll();
        tableData.clear();
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
                final ProcessBuilder pb = new ProcessBuilder(
                        "firefox", documentsFolder + "/" + items[0].getText(0));
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
                new PrintDialog(shell, items);
            }
        });
        table.setMenu(menu);
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

    protected static void writeToSocket(
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
}
