package hu.petrik.konyvtarasztali;

import hu.petrik.konyvtarasztali.Konyv;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class Statisztika {
    static List<Konyv> konyvek;

    public static void main(String[] args) {
        try {
            Beolvas();
            if (konyvek.isEmpty()) {
                throw new Exception("Nincs könyv az adatbázisban");
            }
            megszamol500nalHosszabbStream();
            vanE1950nelRegebbiStream();
            leghosszabbKonyvStream();
            legtobbKonyvvelSzerzoStream();
            hanyszorKolcsonzott();
        } catch (SQLException e) {
            System.out.println("Hiba történt az adatbázis kapcsolat kialakításakor.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void hanyszorKolcsonzott() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Adjon meg egy könyv címet: ");
        String title = sc.nextLine();
        hanyszorKolcsonzottStream(title);
    }

    private static void hanyszorKolcsonzottStream(String title) {
        Optional<Konyv> optionalKonyv = konyvek.stream().filter(konyv -> konyv.getTitle().equals(title)).findFirst();
        if (optionalKonyv.isEmpty()){
            System.out.println("Nincs ilyen könyv");
        } else {
            System.out.printf("A megadott könyv %dx lett kikölcsönözve.\n", optionalKonyv.get().getRental_count());
        }
    }

    private static void hanyszorKolcsonzott(String title) {
        int ind = 0;
        while (ind < konyvek.size() && !konyvek.get(ind).getTitle().equals(title)){
            ind++;
        }
        if (ind == konyvek.size()){
            System.out.println("Nincs ilyen könyv");
        } else {
            System.out.printf("A megadott könyv %dx lett kikölcsönözve.\n", konyvek.get(ind).getRental_count());
        }
    }

    private static void legtobbKonyvvelSzerzoStream() {
        String legtobbKonyvvelRendelkezo = konyvek.stream()
                .collect(Collectors.groupingBy(Konyv::getAuthor, Collectors.counting())).entrySet()
                .stream().max(Comparator.comparingLong(Map.Entry::getValue)).get().getKey();
        System.out.println("A legtöbb könyvvel rendelkező szerző: " + legtobbKonyvvelRendelkezo);
    }

    private static void legtobbKonyvvelSzerzo() {
        Map<String, Long> szerzok = new HashMap<>();
        for (Konyv konyv : konyvek) {
            szerzok.putIfAbsent(konyv.getAuthor(), 0L);
            long ujErtek = szerzok.get(konyv.getAuthor()) + 1;
            szerzok.put(konyv.getAuthor(), ujErtek);
        }
        Map.Entry<String, Long> legtobbKonyvvelRendelkezo = null;
        for (Map.Entry<String, Long> szerzo : szerzok.entrySet()) {
            if (legtobbKonyvvelRendelkezo == null || legtobbKonyvvelRendelkezo.getValue() < szerzo.getValue()) {
                legtobbKonyvvelRendelkezo = szerzo;
            }
        }
        System.out.println("A legtöbb könyvvel rendelkező szerző: " + legtobbKonyvvelRendelkezo.getKey());
    }

    private static void leghosszabbKonyvStream() {
        Konyv leghosszabb = konyvek.stream().max(Comparator.comparingInt(Konyv::getPage_count)).get();
        System.out.printf("A leghosszabb könyv:\n" +
                        "\tSzerző: %s\n" +
                        "\tCím: %s\n" +
                        "\tKiadás éve: %d\n" +
                        "\tOldalszám: %d\n", leghosszabb.getAuthor(), leghosszabb.getTitle(),
                leghosszabb.getPublish_year(), leghosszabb.getPage_count());
    }

    private static void leghosszabbKonyv() {
        Konyv leghosszabb = konyvek.get(0);
        for (int i = 1; i < konyvek.size(); i++) {
            if (konyvek.get(i).getPage_count() > leghosszabb.getPage_count()) {
                leghosszabb = konyvek.get(i);
            }
        }
        System.out.printf("A leghosszabb könyv:\n" +
                        "\tSzerző: %s\n" +
                        "\tCím: %s\n" +
                        "\tKiadás éve: %d\n" +
                        "\tOldalszám: %d\n", leghosszabb.getAuthor(), leghosszabb.getTitle(),
                leghosszabb.getPublish_year(), leghosszabb.getPage_count());
    }

    private static void vanE1950nelRegebbiStream() {
        boolean van = konyvek.stream().anyMatch(konyv -> konyv.getPublish_year() < 1950);
        System.out.printf("%s 1950-nél régebbi könyv\n", van ? "Van" : "Nincs");
    }

    private static void vanE1950nelRegebbi() {
        int ind = 0;
        while (ind < konyvek.size() && konyvek.get(ind).getPublish_year() >= 1950) {
            ind++;
        }
        boolean van = ind < konyvek.size();
        System.out.printf("%s 1950-nél régebbi könyv\n", van ? "Van" : "Nincs");
    }

    private static void megszamol500nalHosszabbStream() {
        long db = konyvek.stream().filter(konyv -> konyv.getPage_count() > 500).count();
        System.out.println("500 oldalnál hosszabb könyvek száma: " + db);
    }

    private static void megszamol500nalHosszabb() {
        int db = 0;
        for (Konyv konyv : konyvek) {
            if (konyv.getPage_count() > 500) {
                db++;
            }
        }
        System.out.println("500 oldalnál hosszabb könyvek száma: " + db);
    }

    private static void Beolvas() throws SQLException {
        KonyvDB db = new KonyvDB();
        konyvek = db.readKonyv();
    }
}
