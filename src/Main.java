import ru.labs.cards.service.Client;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Client client = new Client();
        Client client2 = new Client();
        client.registration();
        client2.registration();
        client.joinGame();
        client2.joinGame();
        int dec = 0;
        int activePlayerIndex = 0;
        while (dec != -1){
            Scanner scr = new Scanner(System.in);
            client.getGameInfo();
            System.out.println("Player " + activePlayerIndex + "'s turn\n");
            System.out.println("\n1 - draw, 2 - pass\n");
            dec = scr.nextInt();
            if (activePlayerIndex == 0){
                if (dec == 1){
                    client.drawACard();
                }
                else if (dec == 2){
                    client.pass();
                }
            }
            else{
                if (dec == 1){
                    client2.drawACard();
                }
                else if (dec == 2){
                    client2.pass();
                }
            }
            activePlayerIndex = activePlayerIndex == 0 ? 1 : 0;
        }
    }
}