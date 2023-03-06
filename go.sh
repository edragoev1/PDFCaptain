javac --class-path .:lib/linux-x86_64/swt.jar src/*.java -d out/production/PDFCaptain
java --class-path .:lib/linux-x86_64/swt.jar:out/production/PDFCaptain PDFCaptain
