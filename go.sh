javac --class-path .:lib/swt.jar src/*.java -d out/production/PDFCaptain
java --class-path .:lib/swt.jar:out/production/PDFCaptain PDFCaptain
