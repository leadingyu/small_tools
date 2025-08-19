import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class Codec {

    // Storage
    private final ConcurrentHashMap<Long, String> idToLong = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> longToId = new ConcurrentHashMap<>();

    private final AtomicLong counter;

    private static final String BASE62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE = BASE62.length();
    private static final String DEFAULT_BASE_URL = "http://tinyurl.com/";

    public Codec() {
        counter = new AtomicLong(1L);
    }

    public String encode(String longUrl) {
        // Encodes a URL to a shortened URL.
        if (longUrl == null || longUrl.isEmpty()) {
            throw new IllegalArgumentException("longUrl must be non-empty");
        }
        // Reuse existing mapping if present
        Long existing = longToId.get(longUrl);
        if (existing != null) {
            return DEFAULT_BASE_URL + toBase62(existing);
        }

        // Create new mapping atomically for concurrent callers of the same URL
        long id = longToId.computeIfAbsent(longUrl, k -> {
            long newId = counter.getAndIncrement();
            idToLong.put(newId, k);
            return newId;
        });

        return DEFAULT_BASE_URL + toBase62(id);
    }


    public String decode(String shortUrl) {
        // Decodes a shortened URL to its original URL.
        if (shortUrl == null || shortUrl.isEmpty()) {
            throw new IllegalArgumentException("shortUrl must be non-empty");
        }
        String token = extractToken(shortUrl);
        long id = fromBase62(token);
        String longUrl = idToLong.get(id);
        if (longUrl == null) {
            throw new IllegalArgumentException("Unknown short URL: " + shortUrl);
        }
        return longUrl;
    }

    private String extractToken(String shortUrl) {
        int idx = shortUrl.lastIndexOf('/');
        if (idx == -1 || idx == shortUrl.length() - 1) {
            throw new IllegalArgumentException("Invalid short URL format");
        }
        return shortUrl.substring(idx + 1);
    }

    private String toBase62(long n) {
        if (n == 0) return "0";
        StringBuilder sb = new StringBuilder();
        long x = n;
        while (x > 0) {
            int r = (int) (x % BASE);
            sb.append(BASE62.charAt(r));
            x /= BASE;
        }
        return sb.reverse().toString();
    }

    private long fromBase62(String s) {
        long n = 0L;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int val = indexOfBase62(c);
            if (val == -1) throw new IllegalArgumentException("Invalid Base62 char: " + c);
            n = n * BASE + val;
        }
        return n;
    }

    private int indexOfBase62(char c) {
        if (c >= '0' && c <= '9') return c - '0';
        if (c >= 'a' && c <= 'z') return 10 + (c - 'a');
        if (c >= 'A' && c <= 'Z') return 36 + (c - 'A');
        return -1;
    }
}
// Your Codec object will be instantiated and called as such:
// Codec codec = new Codec();
// codec.decode(codec.encode(url));
