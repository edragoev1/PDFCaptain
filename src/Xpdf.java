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
