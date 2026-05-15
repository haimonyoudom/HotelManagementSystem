package hotel.util;

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
		ensureReportsDir();
		String normalizedName = filename.endsWith(".txt") ? filename : filename + ".txt";
		Path outputFile = Paths.get(REPORTS_DIR, normalizedName);
		try {
			Files.writeString(outputFile, content, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new RuntimeException("Failed to export report file", e);
		}
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
