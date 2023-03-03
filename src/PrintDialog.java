import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public class PrintDialog {
    public PrintDialog(final Shell shell, final TableItem[] items) {
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
                PDFCaptain.printFile(table.getSelection()[0].getText(1),
                        items[0].getText(0), text1.getText(), text2.getText());
                dialog.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        dialog.open();
    }
}
