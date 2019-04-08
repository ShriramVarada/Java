import javax.swing.*;
import java.awt.*;
import java.util.concurrent.TimeUnit;

public class Panel extends JPanel {
    private int[] arr = new int[1750];
    Panel(){
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        for (int i=0; i< arr.length; ++i){
            arr[i] = (int)(Math.random() * 500);
        }
        this.setSize(1000, 1000);
    }

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        g.setColor(Color.RED);
        for (int i =0; i< arr.length; i++){
            g.drawRect(i, this.getHeight()-arr[i]*2 ,1 , arr[i]*2);
        }

    }

    public void sort(){


//            int n = arr.length;
//
//            // One by one move boundary of unsorted subarray
//            for (int i = 0; i < n-1; i++)
//            {
//                // Find the minimum element in unsorted array
//                int min_idx = i;
//                for (int j = i+1; j < n; j++)
//                    if (arr[j] < arr[min_idx])
//                        min_idx = j;
//
//                // Swap the found minimum element with the first
//                // element
//                int temp = arr[min_idx];
//                arr[min_idx] = arr[i];
//                arr[i] = temp;
//                repaint();
//                sleep(10);
//            }
        mergeSort(arr, 0, arr.length-1);

    }

    public void mergeSort(int arr[], int l, int r){
        if (l < r)
        {
            // Find the middle point
            int m = (l+r)/2;

            // Sort first and second halves
            mergeSort(arr, l, m);
            mergeSort(arr , m+1, r);

            // Merge the sorted halves
            merge(arr, l, m, r);
        }
    }

    void merge(int arr[], int l, int m, int r)
    {
        // Find sizes of two subarrays to be merged
        int n1 = m - l + 1;
        int n2 = r - m;

        /* Create temp arrays */
        int L[] = new int [n1];
        int R[] = new int [n2];

        /*Copy data to temp arrays*/
        for (int i=0; i<n1; ++i)
            L[i] = arr[l + i];
        for (int j=0; j<n2; ++j)
            R[j] = arr[m + 1+ j];


        /* Merge the temp arrays */

        // Initial indexes of first and second subarrays
        int i = 0, j = 0;

        // Initial index of merged subarry array
        int k = l;
        while (i < n1 && j < n2)
        {
            if (L[i] <= R[j])
            {
                arr[k] = L[i];
                i++;
            }
            else
            {
                arr[k] = R[j];
                j++;
            }
            k++;
            sleep(1);
            repaint(100000, 100, 100, 500, 500);
            sleep(1);
            repaint(100000, 100, 100, 500, 500);

        }

        /* Copy remaining elements of L[] if any */
        while (i < n1)
        {
            arr[k] = L[i];
            i++;
            k++;
            sleep(1);
            //repaint(100000);
            repaint(100000, 100, 100, 500, 500);

        }

        /* Copy remaining elements of R[] if any */
        while (j < n2)
        {
            arr[k] = R[j];
            j++;
            k++;
            sleep(1);
            //repaint(100000);
            repaint(100000, 100, 100, 500, 500);
        }
    }



    public void sleep(int millis){
        try {
            Thread.sleep(millis);
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }

}
