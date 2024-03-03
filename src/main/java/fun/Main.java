package fun;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;


public class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        String[] input = br.readLine().split(" ");

        int N = Integer.parseInt(input[0]);
        int M = Integer.parseInt(input[1]);

        int[][] aArray = new int[N][M];
        int[][] bArray = new int[N][M];

        for(int i=0;i<N;i++) {
            String[] numbers = br.readLine().split(" ");
            for(int j=0;j<M;j++) {
                aArray[i][j] = Integer.parseInt(numbers[j]);
            }
        }


        for(int i=0;i<N;i++) {
            String[] numbers = br.readLine().split(" ");
            for(int j=0;j<M;j++) {
                bArray[i][j] = Integer.parseInt(numbers[j]);
            }
        }


        int[][] resultArray = new int[N][M];

        for(int i=0;i<N;i++) {
            for(int j=0;j<M;j++) {
                resultArray[i][j] = aArray[i][j] + bArray[i][j];
            }
        }

        for(int i=0;i<N;i++) {
            for(int j=0;j<M;j++) {
                System.out.print(resultArray[i][j] +" ");
            }
            System.out.println();
        }
    }
}
