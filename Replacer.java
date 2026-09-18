import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Replacer {
    public static void main(String[] args) throws IOException {
        Path startPath = Paths.get("C:/Dev/java/BsDnD-0.1/bsdnd/src/test");
        try (Stream<Path> stream = Files.walk(startPath)) {
            List<Path> testFiles = stream
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith("Test.java"))
                    .collect(Collectors.toList());

            for (Path file : testFiles) {
                List<String> lines = Files.readAllLines(file);
                boolean changed = false;
                for (int i = 0; i < lines.size(); i++) {
                    String line = lines.get(i);
                    if (line.matches("^\\s*//\\s*(Arrange|Act|Assert|Act & Assert).*$")) {
                        lines.set(i, null);
                        changed = true;
                    } else if (line.contains(".getMessageKey()")) {
                        lines.set(i, line.replace(".getMessageKey()", ".getMessage()"));
                        changed = true;
                    }
                }
                if (changed) {
                    List<String> newLines = lines.stream().filter(l -> l != null).collect(Collectors.toList());
                    Files.write(file, newLines);
                    System.out.println("Updated " + file);
                }
            }
        }
    }
}
