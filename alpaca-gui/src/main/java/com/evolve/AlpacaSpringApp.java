package com.evolve;

import com.evolve.gui.AlpacaJavafxApp;
import com.evolve.gui.SplashScreenLoader;
import com.google.common.io.Resources;
import javafx.application.Application;
import javafx.scene.Node;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.spring.InjectionPointLazyFxControllerAndViewResolver;
import net.rgielen.fxweaver.spring.SpringFxWeaver;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Optional;

import static com.evolve.alpaca.conf.LocalUserConfiguration.ALPACA_CONF_DIR;

@SpringBootApplication(scanBasePackages = "com.evolve")
@Slf4j
public class AlpacaSpringApp {
    private static final String LOCK_FILE_NAME = ALPACA_CONF_DIR + "App.lock";
    static final File LOCK_FILE = new File(LOCK_FILE_NAME);

    public static void main(String[] args) {
        if (appIsReadyRunning()) {
            JOptionPane.showMessageDialog(null, "Alpaca application is already running!");
            return;
        }
        setIconOnMac();
        loadSplashScreen(args);
        Application.launch(AlpacaJavafxApp.class, args);
    }

    private static void loadSplashScreen(String[] args) {
        final boolean showSplash = args == null || !Arrays.asList(args).contains("noSplash");

        if (showSplash) {
            System.setProperty("javafx.preloader", SplashScreenLoader.class.getCanonicalName());
        }
    }

    @Bean
    public FxWeaver fxWeaver(ConfigurableApplicationContext applicationContext) {
        // Would also work with javafx-weaver-core only:
        // return new FxWeaver(applicationContext::getBean, applicationContext::close);
        return new SpringFxWeaver(applicationContext);
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public <C, V extends Node> FxControllerAndView<C, V> controllerAndView(FxWeaver fxWeaver,
            InjectionPoint injectionPoint) {
        return new InjectionPointLazyFxControllerAndViewResolver(fxWeaver)
                .resolve(injectionPoint);
    }

    private static boolean appIsReadyRunning() {
        if (lockExistsAndProcessIsRunning()) {
            log.warn("Alpaca application is already running!");
            return true;
        }

        final boolean dirsCreated = LOCK_FILE.getParentFile().mkdirs();
        log.info("Directories created: {}", dirsCreated);

        long pid = ProcessHandle.current().pid();
        log.info("current pid {}", pid);

        try (FileOutputStream fileOutputStream = new FileOutputStream(LOCK_FILE);
             FileChannel channel = fileOutputStream.getChannel();
             PrintWriter printWriter = new PrintWriter(Channels.newOutputStream(channel));
             FileLock lock = channel.lock();
        ) {
            printWriter.print(pid);
            printWriter.flush();
            log.info("Lock file created: {}", lock);
        } catch (IOException e) {
            log.error("Problem with lock");
            throw new RuntimeException(e);
        }

        /*
         * Register a shutdown hook to delete the lock file when the application is closed. Even when forcefully closed
         * with the task manager. (Tested on Windows 11 with JavaFX 19)
         */
        LOCK_FILE.deleteOnExit();
        return false;
    }

    private static boolean lockExistsAndProcessIsRunning() {
        if (LOCK_FILE.exists()) {
            // if the lock file exists it doesn't mean the process is still there - let's check the process
            try {
                final long otherPid = Long.parseLong(Files.readString(LOCK_FILE.toPath()));
                final Optional<String> info = fetchProcessInfo(otherPid)
                        .flatMap(ProcessHandle.Info::command);
                final boolean isOtherProcessRunning = info.isPresent() && StringUtils.containsIgnoreCase(info.get(), "java");
                if (isOtherProcessRunning) {
                    log.warn("It looks like there is another java process running with pid {}", otherPid);
                }
                return isOtherProcessRunning;
            } catch (Exception e) {
                log.error("Unable to get pid from the lock file - assuming another instance of Alpaca is running", e);
                return true;
            }
        }
        return false;
    }

    private static Optional<ProcessHandle.Info> fetchProcessInfo(long pid) {
        return ProcessHandle.allProcesses().filter(
                        handle -> handle.pid() == pid)
                .findAny()
                .map(ProcessHandle::info);
    }

    /**
     * Mac's dock behaves a little different than Windows taskbar.<br>
     * This is a special feature that is why we also check {@link Taskbar.Feature#ICON_IMAGE}
     */
    private static void setIconOnMac() {
        if (SystemUtils.IS_OS_MAC && Taskbar.getTaskbar().isSupported(Taskbar.Feature.ICON_IMAGE)) {
            try {
                final URL imageResource = Resources.getResource("alpaca.png");
                final Image image = Toolkit.getDefaultToolkit().getImage(imageResource);

                Taskbar.getTaskbar().setIconImage(image);
            } catch (Exception ex) {
                log.warn("failed to set icon", ex);
            }
        }
    }

}