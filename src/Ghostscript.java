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

import java.util.ArrayList;
import java.util.List;

public class Ghostscript {
    public static void print(
            final String ipAddress,
            final String fileName,
            final String pageList) throws Exception {
        List<String> command = new ArrayList<>();
        command.add("gs");
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
