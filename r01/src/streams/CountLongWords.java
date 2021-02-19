package streams;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Clock;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;

public class CountLongWords
{
    public static void main(String[] args)
    {
        String contents = null;
        try
        {
            contents = new String(Files.readAllBytes(
                Paths.get("../gutenberg/alice30.txt")), StandardCharsets.UTF_8);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        List<String> words = Arrays.asList(contents.split("\\PL+"));

        long count = 0;

        long end;
        long start = System.currentTimeMillis();
        for (String w : words)
            if (w.length() > 12) ++count;
        end = System.currentTimeMillis();
        System.out.println(count + " czas=" + (end - start));

        start = System.currentTimeMillis();
        count = words.stream().filter(w -> w.length() > 12).count();
        end = System.currentTimeMillis();
        System.out.println(count + " czas=" + (end - start));

        start = System.currentTimeMillis();
        count = words.parallelStream().filter(w -> w.length() > 12).count();
        end = System.currentTimeMillis();
        System.out.println(count + " czas=" + (end - start));
    }
}
