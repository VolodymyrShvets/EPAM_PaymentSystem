import java.time.LocalDate;

public class Demo {
    public static void main(String[] args) {
        LocalDate now = LocalDate.now();
        LocalDate date = LocalDate.parse("2022-04-04");

        int comp = date.compareTo(now);
        if (comp <= 0)
            System.out.println(date + " is earlier/equal than " + now);
        //else if (comp == 0)
        //    System.out.println(date + " is /equal to " + now);
        else
            System.out.println(date + " is latter than " + now);
    }
}
