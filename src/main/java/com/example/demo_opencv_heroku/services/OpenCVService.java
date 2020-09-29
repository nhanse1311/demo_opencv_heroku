package com.example.demo_opencv_heroku.services;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.LocatorEx;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class OpenCVService {
    public String imageName = "";
    private DaemonThread myThread = null;
    int count = 0;
    VideoCapture cap = null;

    Mat frame =null;
    MatOfByte mem = null;

    public String getImageName() {
        return imageName;
    }

    public OpenCVService() {
//        String path = "D:\\101\\JavaSpringboot\\opencv_hekoru\\src\\main\\resources\\static\\opencv\\opencv\\build\\java\\x64";
//        System.load(path + "\\opencv_java249.dll");
//        System.load(path + "\\opencv_ffmpeg249_64.dll");
        File libOne = new File("src/main/resources/static/opencv/opencv/build/java/x64/opencv_java249.dll");
        File libTwo = new File("src/main/resources/static/opencv/opencv/build/java/x64/opencv_ffmpeg249_64.dll");
        System.load(libOne.getAbsolutePath());
        System.load(libTwo.getAbsolutePath());
        startStream();
    }

    class DaemonThread implements Runnable {

        protected volatile boolean runnable = false;

        @Override
        public void run() {
            synchronized (this) {
                while (runnable) {
                    if (cap.grab()) {
                        try {
                            cap.retrieve(frame);
                            Highgui.imencode(".bmp", frame, mem);
                            Image im = ImageIO.read(new ByteArrayInputStream(mem.toArray()));

//                            BufferedImage buff = (BufferedImage) im;
//                            Graphics g = jPanel1.getGraphics();
//
//                            if (g.drawImage(buff, 0, 0, getWidth(), getHeight() - 150, 0, 0, buff.getWidth(), buff.getHeight(), null)) {
//                                if (runnable == false) {
//                                    System.out.println("Going to wait()");
//                                    this.wait();
//                                }
//                            }
                        } catch (Exception ex) {
                            System.out.println("Error");
                        }
                    }
                }
            }
        }
    }

    private void openRTSP(boolean isOpened, VideoCapture capturedVideo, Mat cameraMat) {
        if (isOpened) {
            boolean tempBool = capturedVideo.read(cameraMat);
            System.out.println("VideoCapture returned mat? " + tempBool);

            if (!cameraMat.empty()) {
                System.out.println("Print image size: " + cameraMat.size());
                //processing image captured in cameraMat object

            } else {
                System.out.println("Mat is empty.");
            }
        } else {
            System.out.println("Camera connection problem. Check addressString");
        }
    }

    public void startStream(){
        cap = new VideoCapture();
        boolean isOpened = cap.open("rtsp://admin:admin@192.168.43.157:8554/unicast");
        frame = new Mat();
        mem =  new MatOfByte();
        openRTSP(isOpened, cap, frame);
        myThread = new DaemonThread();
        Thread t = new Thread(myThread);
        t.setDaemon(true);
        myThread.runnable = true;
        t.start();
    }
    public String snapShot(){
        LocalDateTime now = LocalDateTime.now();
        imageName = System.currentTimeMillis() + ".jpg";
        File file = new File("src/main/resources/static/images/" + imageName);
        try {
            file.createNewFile();
            Highgui.imwrite(file.getPath(), frame);
        } catch (IOException ex) {
            Logger.getLogger(LocatorEx.Snapshot.class.getName()).log(Level.SEVERE, null, ex);
            return "Cannot snapshot";
        }
        return imageName;
    }
}
