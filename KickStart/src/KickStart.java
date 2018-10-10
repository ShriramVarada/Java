import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class KickStart {

    public static void main(String[] args) throws IOException {

        File file = new File("E:\\Java\\Java\\KickStart\\src\\sample");
        BufferedReader br = new BufferedReader(new FileReader(file));

        int numberofTestCases, numberofGBuses, numberofCities;
        String[] cityNumbers;

        HashMap<Integer, Integer> map = new HashMap<>();

        numberofTestCases = Integer.parseInt(br.readLine());

        for(int i =0; i < numberofTestCases; ++i){
            map.clear();

            numberofGBuses = Integer.parseInt(br.readLine());

            cityNumbers = br.readLine().split(" ");

            for(int j = 0; j < numberofGBuses; ++j) {

                int firstCity = Integer.parseInt(cityNumbers[2 * j]);
                int secondCity = Integer.parseInt(cityNumbers[2 * j + 1]);

                for (int k = firstCity; k <= secondCity; ++k) {
                    if (map.get(k) == null)
                        map.put(k, 1);
                    else {
                        map.put(k, map.get(k) + 1);
                    }
                }

            }

            System.out.print("Case#" + (i+1) +": ");

            numberofCities = Integer.parseInt(br.readLine());

            for(int k = 0; k < numberofCities; ++k){
                System.out.print(map.get(Integer.parseInt(br.readLine())) + " ");
            }
            System.out.println();
            br.readLine(); // Flush the newline
        }
    }

}
