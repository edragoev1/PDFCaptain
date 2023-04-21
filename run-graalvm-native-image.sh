echo "Main-Class: PDFCaptain" > manifest.txt
/opt/graalvm-ce-java17-22.3.1/bin/javac -encoding utf-8 -Xlint -cp .:src:lib/linux-x86_64/swt.jar src/*.java
/opt/graalvm-ce-java17-22.3.1/bin/jar -cvfm PDFCaptain.jar manifest.txt src/*.class
# /opt/graalvm-ce-java17-22.3.1/bin/native-image -jar PDFCaptain.jar PDFCaptain.exe
# rm -f manifest.txt
# rm -f *.exe.build_artifacts.txt
# rm -f src/*.class

# ./PDFCaptain.exe
