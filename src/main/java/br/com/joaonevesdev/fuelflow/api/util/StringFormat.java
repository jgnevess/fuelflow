package br.com.joaonevesdev.fuelflow.api.util;

import java.util.Set;

public abstract class StringFormat {
    private static final Set<String> lowers = Set.of(
            "de", "da", "do", "das", "dos", "e"
    );

    private static final Set<String> UFS = Set.of(
            "AC", "AL", "AP", "AM",
            "BA", "CE", "DF", "ES",
            "GO", "MA", "MT", "MS",
            "MG", "PA", "PB", "PR",
            "PE", "PI", "RJ", "RN",
            "RS", "RO", "RR", "SC",
            "SP", "SE", "TO"
    );

    public static String format(String text) {
        String[] string = text.toLowerCase().split("\\s+");
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < string.length; i++) {
            String p = string[i];

            if (i != 0 && lowers.contains(p)) {
                sb.append(p);
            } else if (UFS.contains(p.toUpperCase())) {
                sb.append(p.toUpperCase());
            } else {
                sb.append(p.substring(0, 1).toUpperCase())
                        .append(p.substring(1));
            }


            sb.append(" ");
        }

        return sb.toString().trim();
    }

}
