import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

public class JvaPrame {

    private static final List<String> suspiciousPatterns = Arrays.asList(
            "Runtime.getRuntime()",
            "ProcessBuilder",
            "exec(",
            "eval(",
            "Base64.getDecoder().decode",
            "new FileOutputStream",
            "new FileInputStream",
            "socket",
            "ServerSocket"
    );

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("검사할 파일 경로 또는 https URL을 입력하세요: ");
        String inputPath = scanner.nextLine();

        List<String> lines = new ArrayList<>();

        try {
            if (inputPath.startsWith("http://") || inputPath.startsWith("https://")) {
                // 웹 URL 처리
                URL url = new URL(inputPath);
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    lines.add(line);
                }
                in.close();
            } else {
                // 로컬 파일 처리
                lines = Files.readAllLines(Paths.get(inputPath));
            }

            boolean found = false;
            System.out.println("=== siteECheck: 간단한 악성 코드 탐지기 ===");

            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                for (String pattern : suspiciousPatterns) {
                    if (line.contains(pattern)) {
                        System.out.printf("⚠️ 의심 코드 발견 (줄 %d): '%s'%n", i + 1, pattern);
                        found = true;
                    }
                }
            }

            if (!found) {
                System.out.println("✅ 의심스러운 코드가 발견되지 않았습니다.");
            }

        } catch (IOException e) {
            System.out.println("파일을 읽는 도중 오류 발생: " + e.getMessage());
        }
    }
}
