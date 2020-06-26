/*
 * package ch.frequenceBanane.bananaBroadcast;
 * 
 * import java.io.IOException; import java.util.*;
 * 
 * import javax.sound.sampled.UnsupportedAudioFileException;
 * 
 * import ch.frequenceBanane.bananaBroadcast.audio.AudioPlayer; import
 * ch.frequenceBanane.bananaBroadcast.audio.MixersUtilities;
 * 
 * public class ConsoleApp {
 * 
 * BananaBroadcast app = new BananaBroadcast();
 * 
 * public ConsoleApp() { showSplashart();
 * 
 * Scanner scanner = new Scanner(System.in); String token = "";
 * 
 * while(!token.equals("quit")) {
 * 
 * try {
 * 
 * token = scanner.next();
 * 
 * if(token != null) { switch(token) { /* case "player": token = scanner.next();
 * if(token.equals("add")) { token = scanner.nextLine().strip();
 * System.out.println("Adding new player as '"+token+"'...");
 * app.addPlayer(token); } else if(app.getPlayer(token) != null) { String name =
 * token; token = scanner.next().strip(); switch(token) { case "load" : token =
 * scanner.next();
 * System.out.println("Loading '"+token+"' on player '"+name+"'");
 * app.getPlayer(name).load(token); break; case "pause" :
 * System.out.println("Pausing player "+name); app.getPlayer(name).pause();
 * break; case "stop" : System.out.println("Stopping player "+name);
 * app.getPlayer(name).close(); break; case "play" :
 * System.out.println("Resuming player "+name); app.getPlayer(name).play();
 * break; default: throw new InputMismatchException(); } } else throw new
 * InputMismatchException(); break;
 * 
 * case "gpio": token = scanner.next(); switch(token) { case "start":
 * System.out.println("Starting GPIOs..."); app.startGPIO(); break; case "stop"
 * : System.out.println("Stopping GPIOs..."); app.stopGPIO(); break; default:
 * throw new InputMismatchException(); } break;
 * 
 * case "listMixers": token = scanner.next(); switch(token) { case "input":
 * ArrayList<String> infosInput = MixersUtilities.getInputMixersInfos(); for(int
 * i=0; i<infosInput.size(); i++) {
 * System.out.println(i+". "+infosInput.get(i)); } break; case "output" :
 * ArrayList<String> infosOutput = MixersUtilities.getOutputMixersInfos();
 * for(int i=0; i<infosOutput.size(); i++) {
 * System.out.println(i+". "+infosOutput.get(i)); } break; default: throw new
 * InputMismatchException(); } break;
 * 
 * case "test": AudioPlayer player = new AudioPlayer(); try {
 * player.load("sample3.wav"); player.getWaveform(); } catch (Exception e) { //
 * Auto-generated catch block e.printStackTrace(); } break;
 * 
 * case "quit": break; default: throw new InputMismatchException(); } }
 * 
 * } catch(InputMismatchException e) {
 * System.out.println("Malformatted command"); scanner.nextLine(); }
 * 
 * } System.out.println("See ya!"); scanner.close(); }
 * 
 * public static void main(String[] args) { ConsoleApp console = new
 * ConsoleApp(); }
 * 
 * private void showSplashart() { System.out.
 * print("______                              ______                     _               _    \r\n"
 * +
 * "| ___ \\                             | ___ \\                   | |             | |   \r\n"
 * +
 * "| |_/ / __ _ _ __   __ _ _ __   __ _| |_/ /_ __ ___   __ _  __| | ___ __ _ ___| |_  \r\n"
 * +
 * "| ___ \\/ _` | '_ \\ / _` | '_ \\ / _` | ___ \\ '__/ _ \\ / _` |/ _` |/ __/ _` / __| __| \r\n"
 * +
 * "| |_/ / (_| | | | | (_| | | | | (_| | |_/ / | | (_) | (_| | (_| | (_| (_| \\__ \\ |_  \r\n"
 * +
 * "\\____/ \\__,_|_| |_|\\__,_|_| |_|\\__,_\\____/|_|  \\___/ \\__,_|\\__,_|\\___\\__,_|___/\\__| \r\n"
 * +
 * "___________________________________________________________________________________\r\n"
 * +
 * "___________________________________________________________________________________"
 * ); System.out.println("\r\n"); } }
 */
