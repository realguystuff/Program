package io.github.realguystuff.Program;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;

public class Main {
    private static InetAddress receiverIpAddress;
    private static int receiverPort;

    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket()) {
            receiverIpAddress = InetAddress.getByName("192.168.1.59"); // Replace with the actual receiver IP address
            receiverPort = 5000; // Replace with the actual receiver port number
            int fps = 60;

            Timer screenSharingTimer = new Timer();
            screenSharingTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    try {
                        BufferedImage screen = captureScreen();
                        byte[] imageData = imageToByteArray(screen);
                        sendImageData(imageData, socket);
                    } catch (AWTException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }, 0, 1000 / fps);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static BufferedImage captureScreen() throws AWTException {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle screenBounds = new Rectangle(screenSize);

        Robot robot = new Robot();
        return robot.createScreenCapture(screenBounds);
    }

    private static byte[] imageToByteArray(BufferedImage image) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", outputStream);
        return outputStream.toByteArray();
    }

    private static void sendImageData(byte[] imageData, DatagramSocket socket) throws IOException {
        DatagramPacket packet = new DatagramPacket(imageData, imageData.length, receiverIpAddress, receiverPort);
        socket.send(packet);
    }
}
