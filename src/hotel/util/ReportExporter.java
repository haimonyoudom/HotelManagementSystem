package hotel.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ReportExporter {

	private static final String REPORTS_DIR = "reports";

	private ReportExporter() {
	}

	public static void exportToTxt(String content, String filename) {
		Path outputFile = normalizeOutputPath(filename, ".txt");
		try {
			Files.writeString(outputFile, content, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new RuntimeException("Failed to export report file", e);
		}
	}

	public static void exportToCsv(java.util.List<String[]> rows, String filename) {
		Path outputFile = normalizeOutputPath(filename, ".csv");
		try {
			StringBuilder builder = new StringBuilder();
			for (String[] row : rows) {
				for (int i = 0; i < row.length; i++) {
					builder.append(escapeCsv(row[i]));
					if (i < row.length - 1) builder.append(',');
				}
				builder.append(System.lineSeparator());
			}
			Files.writeString(outputFile, builder.toString(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new RuntimeException("Failed to export CSV file", e);
		}
	}

	private static Path normalizeOutputPath(String filename, String extension) {
		Path outputFile = Paths.get(filename);
		if (outputFile.getParent() == null) {
			ensureReportsDir();
			outputFile = Paths.get(REPORTS_DIR, outputFile.toString());
		}
		String normalized = outputFile.toString();
		if (!normalized.toLowerCase().endsWith(extension)) {
			outputFile = outputFile.resolveSibling(outputFile.getFileName() + extension);
		}
		return outputFile;
	}

	private static String escapeCsv(String value) {
		if (value == null) return "";
		String escaped = value.replace("\"", "\"\"");
		if (escaped.contains(",") || escaped.contains("\n") || escaped.contains("\r") || escaped.contains("\"")) {
			return '"' + escaped + '"';
		}
		return escaped;
	}

	public static void ensureReportsDir() {
		Path reportsDir = Paths.get(REPORTS_DIR);
		try {
			if (!Files.exists(reportsDir)) {
				Files.createDirectories(reportsDir);
			}
		} catch (IOException e) {
			throw new RuntimeException("Failed to create reports directory", e);
		}
	}
}
