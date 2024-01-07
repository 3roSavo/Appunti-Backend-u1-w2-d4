package savogineros;

import com.github.javafaker.Faker;
import org.apache.commons.io.FileUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Application {

    public static void main(String[] args) {

        Faker faker = new Faker(Locale.ITALIAN);

        List<User> usersList = new ArrayList<>();

        //Random random = new Random();
        for (int i = 0; i < 50; i++) {
            usersList.add(new User(
                    faker.lordOfTheRings().character(),
                    faker.name().lastName(),
                    new Random().nextInt(1, 100),
                    faker.harryPotter().location()));
        }
        usersList.forEach(user -> System.out.println(user));
        int etaMinorenniSommate = usersList.stream().filter(user -> user.getAge() < 18).map(user -> user.getAge()).reduce(0, (partialSum, currentAge) -> partialSum + currentAge);
        // Avviando il debugger e cliccando sull'opzione 'Trace Current Stream Chain' ho una rappresentazione grafica di tutta la catena dello stream
        System.out.println(etaMinorenniSommate);
        // ricordiamoci che possiamo crearci un paio di Supplier per generare un'età random e un utente!
        System.out.println("-----------------FACCIAMOLO COI SUPPLIER---------------------");

        Supplier<Integer> randomAge = () -> new Random().nextInt(1, 100);
        System.out.println(randomAge.get());

        Supplier<User> newUser = () -> new User(faker.rickAndMorty().character(), faker.name().lastName(), randomAge.get(), faker.rickAndMorty().location());

        for (int i = 0; i < 10; i++) {
            System.out.println(newUser.get());
        }

        // TODO: COLLECTORS
        // Anche questi sono terminatori di streams
        System.out.println("---------------------COLLECTORS------------------------");

        // TODO: collect(Collectors.groupingBy()
        // ritorna una Map collection di n° elementi quanti sono i valori di ritorno della lambda (che faranno anche da chiave), senza i doppioni
        // e come valore di ogni elemento chiave-valore una List di elementi che condivideranno lo stesso ritorno della lambda
        // IN SOSTANZA qui sotto avremo una MAP COLLECTION dove le chiavi sono generate in base ai valori restituiti dalla lambda per ciascun elemento nell'elenco.
        // e i valori saranno una List di User aventi nel campo city lo stesso valore della chiave.

        System.out.println("----groupingBy() per città----");
        // Raggruppiamo gli users per città
         Map<String, List<User>> usersPerCitta = usersList.stream().collect(Collectors.groupingBy(user -> user.getCity()));
        usersPerCitta.forEach((citta , listaUsers) -> System.out.println(citta + " = " + listaUsers));
        System.out.println(usersPerCitta.size());

        System.out.println("----groupingBy() per età----");
        // Raggruppiamo gli users per età
        Map<Integer, List<User>> usersPerEta = usersList.stream().collect(Collectors.groupingBy(user -> user.getAge()));
        usersPerEta.forEach((eta, user) -> System.out.println("età: " + eta + ", lista users: " + user));
        System.out.println(usersPerEta.size());

        // TODO: collect(Collectors.joining())
        System.out.println("----------collect(Collectors.joining())---------");
        // Col joining puoi facilmente lavorare con la concatenazione delle stringhe, puoi anche decidere quale sarà il delimitatore tra un valore e un altro
        // Ora concateniamo tutti i nomi + cognomi degli user separati da una virgola
        String stringa = usersList.stream().map(user -> user.getName() + " " + user.getSurname()).collect(Collectors.joining(", "));
        System.out.println("Nomi concatenati = " + stringa);

        // TODO: collect(Collectors.averagingInt())
        System.out.println("------------collect(Collectors.averagingInt())--------------");

        // Facciamo la media dei valori contenuti in una List
        List<Integer> valori = new ArrayList<>(Arrays.asList(2,12,55,23,1450));
        double mediaValori = valori.stream().collect(Collectors.averagingInt(value -> value));
        System.out.println("media dei numeri della lista: " + mediaValori);

        // Calcoliamo la media delle età degli users
        double mediaEta = usersList.stream().collect(Collectors.averagingInt(user -> user.getAge()));
        System.out.println("media delle età degli users: " + mediaEta);

        // Raggruppiamo gli users per città e facciamo la media delle età per tutti gli users che condividono quella specifica città (modalità difficile)
        System.out.println("Raggruppiamo per città e facciamo la media delle età per user--------------------");

        Map<String, Double> usersPerCittaEMediaEta =  usersList.stream().collect(Collectors.groupingBy(user -> user.getCity(), Collectors.averagingInt(user -> user.getAge())));
        usersPerCittaEMediaEta.forEach((citta, media) -> System.out.println("città: " + citta + ", media età: " + media));

        // TODO: collect(Collectors.summarizingInt())
        // Raggruppiamo per città con info generali sulle età (età minima, massima, somma, media e la quantità di età quindi di utenti)
        System.out.println("-------------------collect(Collectors.summarizingInt())-------------------");
        Map<String, IntSummaryStatistics> usersPerCittaEdEtaStats = usersList.stream().collect(Collectors.groupingBy(user -> user.getCity(), Collectors.summarizingInt(user -> user.getAge())));
        usersPerCittaEdEtaStats.forEach((citta, stats) -> System.out.println("città: " + citta + ", " + stats));

        // Vediamo ora un altro metodo INTERMEDIO
        // TODO: sorted()
        // ATTENZIONE il sorted() è un'operazione intermedia degli streams, semplicemente lo ordina secondo unn criterio base o personalizzato
        // E' spesso utilizzato con il metodo Comparator, che consente operazioni più complesse di ordinamento degli streams
        List<Integer> valoriDisordinati = new ArrayList<>(List.of(12,34,3,68,50));

        List<Integer> numeriOrdinati = valoriDisordinati.stream().sorted().toList();
        // qui sorted ha dei valori corrispondenti a primitivi, quindi non ha problemi a ordinare, vale lo stesso per valori String
        System.out.println("--------sorted() di numeri---------");
        System.out.println(numeriOrdinati);


        // MA nel caso il tipo sia complesso (tipo un oggetto lista) oppure io voglia utilizzare un criterio
        // di ordinamento personalizzato dovò utilizzare il Comparator

        // TODO: Comparator.comparing()
        // Ordiniamo quindi gli user in base alla loro età con un questo metodo più avanzato
        List<User> listaUsersOrdinata = usersList.stream().sorted(Comparator.comparing(user -> user.getAge())).toList();

        // Ho notato che si può utilizzare sia comparing() che comparingInt() con gli interi.
        // Nel caso avessi dovuto comparare delle String avrei utilizzato per forza comparing()
        System.out.println("---------sorted(Comparator.comparing()) di età degli user----------)");
        listaUsersOrdinata.forEach(user -> System.out.println(user));

        // TODO: reversed()
        // Proviamo a ordinare per età degli user però DECRESCENTE
        // PROBLEMA, il metodo reversed() non riconosce la tipizzazione del ritorno della lambda....
        // Probabilmente perché è passata implicitamente dai metodi precedenti.
        // Con :: è specificata invece la tipizzazione, quindi non ci sono più ambiguità. Penso sia per questo. :(
        // user -> user.getAge() diventa User::getAge(), così funzionerà

        //List<User> usersOrdinatiPerEtaDecrescente = usersList.stream().sorted(Comparator.comparingInt(user -> user.getAge())).toList();

        System.out.println("------------------------------user ordinati per età decrescente----------------------------------");
        List<User> usersOrdinatiPerEtaDecrescente = usersList.stream().sorted(Comparator.comparingInt(User::getAge).reversed()).toList();
        usersOrdinatiPerEtaDecrescente.forEach(System.out::println);

        // Ordiniamo gli user per nome crescente A->Z
        System.out.println("---------------------------- users per nome CRESCENTE-------------------------------");

        List<User> usersOrdinatiPerNomeCrescente = usersList.stream().sorted(Comparator.comparing(user -> user.getName())).toList();
        usersOrdinatiPerNomeCrescente.forEach(user -> System.out.println(user));

        // Proviamo anche qui il metodo DECRESCENTE con i nomi Z -> A
        System.out.println("-------------------------------users per nome DECRESCENTE---------------------------------");

        List<User> usersOrdinatiPerNomeDecrescente = usersList.stream().sorted(Comparator.comparing(User::getName).reversed()).toList();
        usersOrdinatiPerNomeDecrescente.forEach(user -> System.out.println(user));

        // Altre operazioni intermedie oltre map(), filter() e sorted()

        // TODO: limit()
        System.out.println("-------------------limit()----------------------");
        usersList.stream().limit(5).forEach(System.out::println);

        // TODO: distinct()
        System.out.println("-------------------distinct()----------------------");
        List<Integer> listaConDoppioni = Arrays.asList(23,23,34,12,34,14);
        System.out.println(listaConDoppioni.stream().distinct().toList());

        // TODO: skip()
        System.out.println("-------------------skip()----------------------");
        System.out.println(listaConDoppioni.stream().skip(3).toList());

        // Mescoliamo gli elementi di una lista
        System.out.println("-------------------Collections.shuffle()----------------------");
        System.out.println("Prima: " + listaConDoppioni); // prima
        Collections.shuffle(listaConDoppioni);
        System.out.println("Dopo: " + listaConDoppioni); // dopo


        // TODO: mapToInt(), mapToDouble, mapToLong
        // Sono metodi atti a semplificare specifiche operazioni
        System.out.println("-------------------------------- mapToInt()------------------------------------");
        // mapToInt(), mapToDouble, mapToLong
        // Calcoliamo la somma totale delle età con mapTo al posto di map() e reduce()
        // metodo vecchio
        int eta = usersList.stream().map(user -> user.getAge()).reduce(0, (partialSum, currentValue) -> partialSum + currentValue);
        System.out.println(eta);

        // TODO: sum()
        // metodo con mapToInt().sum()
        int eta2 = usersList.stream().mapToInt(user -> user.getAge()).sum();
        System.out.println(eta2);

        // Esempio con lista di primitivi
        List<Integer> listaDiNumeri = List.of(23,98,33,2,45);
        System.out.println(listaDiNumeri.stream().mapToInt(num -> num).sum());

        // TODO: average()
        // Calcoliamo anche la media col mapToInt().average(), ci sono due modi
        System.out.println("-----------------------media età users con average()-----------------------");


        double media = usersList.stream().mapToDouble(user -> user.getAge()).average().getAsDouble();
        // getAsDouble() converte il risultato da optionalDouble in double
        // si poteva mettere anche mapToInt(), tanto è compatibile con il metodo average()
        System.out.println(media);

        // oppure tornando l'oggetto OptionalDouble dal metodo average()
        OptionalDouble media2 = usersList.stream().mapToInt(user -> user.getAge()).average();
        System.out.println(media2);

        // TODO: max(), min()
        // Otteniamo l'età MAX con max(), lo stesso vale con min()
        int etaMax = usersList.stream().mapToInt(User::getAge).max().getAsInt(); // possiamo tornare anche un oggetto OptionalInt
        System.out.println("---------------------età massima con max()--------------------------");
        System.out.println(etaMax);


        // ---------------GESTIONE DEI FILES----------------

        File file = new File("src/main/java/savogineros/output.txt");

        // in automatico writeStringToFile() mi chiede di gestire l'eccezione checked, quindi facciamola
        try {
            FileUtils.writeStringToFile(file, "Mi chiamo Asdrubale, ", StandardCharsets.UTF_8);
            // Così a ogni avvio dell'app viene sovrascritto il contenuto del documento,
            // se invece vuoi aggiungere del contenuto dovrai aggiungere un quarto parametro -true- a writeStringToFile()
            // se voglio aggiungere del contenuto a capo uso System.lineSeparator() nel parametro data di writeStringToFile()
            String contenuto = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            System.out.println("Il contenuto del file localizzato in " + file.getPath() + ", ha il seguente contenuto:" + System.lineSeparator() + contenuto);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Mi sono anche costruito una funzione per cancellare eventualmente il documento!
        // è statica! Quindi alla mia classe Application basta l'invocazione della funzione!

        //deleteFile();

        // RIPASSO STATIC
        // Nel caso io dovessi richiamarmi la funzione deleteFile() senza che sia statica,
        // dovrò crearmi una nuova istanza di questa classe Application.
        // Una volta creata riuscirò ad accedervi tramite l'istanza creata.

        //Application application = new Application();
        //application.deleteFile();

    }

    public void deleteFile() {
        // Facciamo una funzione per eliminare il documento!
        File file = new File("src/main/java/savogineros/output.txt");
        if (file.exists()) {
            if (file.delete()) {
                System.out.println("File eliminato con successo!");
            } else {
                System.out.println("Problemi con l'eliminazione del file");
            }
        } else {
            System.out.println("File non presente");
        }
    }
}
