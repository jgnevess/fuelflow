package br.com.joaonevesdev.fuelflow.api.util;

import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.Map;

import static java.util.Map.entry;

@Component
public class StringNormalizer {

    private static final Map<String, String> ACCENT_MAP = Map.ofEntries(
            entry("ç", "c"),
            entry("Ç", "C"),
            entry("á", "a"),
            entry("à", "a"),
            entry("ã", "a"),
            entry("â", "a"),
            entry("Á", "A"),
            entry("À", "A"),
            entry("Ã", "A"),
            entry("Â", "A"),
            entry("é", "e"),
            entry("è", "e"),
            entry("ê", "e"),
            entry("É", "E"),
            entry("È", "E"),
            entry("Ê", "E"),
            entry("í", "i"),
            entry("ì", "i"),
            entry("î", "i"),
            entry("Í", "I"),
            entry("Ì", "I"),
            entry("Î", "I"),
            entry("ó", "o"),
            entry("ò", "o"),
            entry("õ", "o"),
            entry("ô", "o"),
            entry("Ó", "O"),
            entry("Ò", "O"),
            entry("Õ", "O"),
            entry("Ô", "O"),
            entry("ú", "u"),
            entry("ù", "u"),
            entry("û", "u"),
            entry("Ú", "U"),
            entry("Ù", "U"),
            entry("Û", "U")
    );

    public String normalize(String text) {
        if (text == null) return null;
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("\\p{M}", "");
        normalized = normalized.toLowerCase();
        normalized = normalized.replaceAll("[^a-z0-9\\s]", "");
        normalized = normalized.replaceAll("\\s+", " ").trim();
        return normalized;
    }

    public String normalizeSimple(String text) {
        if (text == null) return null;

        StringBuilder result = new StringBuilder();

        for (char c : text.toCharArray()) {
            String charStr = String.valueOf(c);
            String replacement = ACCENT_MAP.get(charStr);

            if (replacement != null) {
                result.append(replacement);
            } else {
                if (Character.isLetterOrDigit(c) || Character.isWhitespace(c)) {
                    result.append(c);
                }
            }
        }

        return result.toString()
                .toLowerCase()
                .replaceAll("\\s+", " ")
                .trim();
    }

    public String normalizeUF(String uf) {
        if (uf == null) return null;

        return uf.toUpperCase()
                .replaceAll("[^A-Z]", "")
                .substring(0, Math.min(2, uf.length()));
    }
}